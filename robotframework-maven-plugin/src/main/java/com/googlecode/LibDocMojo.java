package com.googlecode;

/*
 * Copyright 2011 Michael Mallete, Dietrich Schulten
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResource;
import org.python.core.PyString;
import org.python.core.PyStringMap;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import com.googlecode.util.WildCardUtil;

/**
 * Runs the "libdoc" command to generate documentation of user libraries.
 * <p/>
 * This script can generate keyword documentation in HTML and XML formats. The former is suitable for humans and the
 * latter for RIDE, RFDoc, and other tools. This script can also upload XML documentation to RFDoc system.
 * <p/>
 * Documentation can be created for both test libraries and resource files. All library and resource file types are
 * supported, and also earlier generated documentation in XML format can be used as input.
 * <p/>
 * Tries to find libdoc.py on the PYTHONPATH, otherwise uses libdoc version 2.6.3.
 *
 * @goal libdoc
 * @requiresDependencyResolution test
 */
public class LibDocMojo
    extends AbstractMojoWithLoadedClasspath
{
    private WildCardUtil wildCardUtil = new WildCardUtil();

    protected void subclassExecute()
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            documentLibraryOrResourceDirectory();
            documentLibraryOrResourceFile();
            documentLibraryName();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Failed to execute libdoc script", e );
        }
    }

    private void documentLibraryName()
        throws IOException
    {
        if ( libraryName != null )
        {
            runLibDoc( libraryName );
        }
    }

    private void documentLibraryOrResourceFile()
        throws MojoExecutionException, IOException
    {
        if ( libraryOrResourceFile != null )
        {
            if ( libraryOrResourceFile.isDirectory() )
                throw new MojoExecutionException( "libraryOrResourceFile '" + libraryOrResourceFile
                    + "' must not be a directory" );

            runLibDoc( libraryOrResourceFile.getAbsolutePath() );
        }
    }

    private void documentLibraryOrResourceDirectory()
        throws MojoExecutionException, IOException
    {
        if ( libraryOrResourceDirectory != null )
        {
            if ( includes == null || includes.length == 0 )
            {
                includes = new String[] { "**/*.*" };
            }
            Iterator<PlexusIoFileResource> fileResources =
                extractFilesFromLibraryOrResourceDirectory( libraryOrResourceDirectory );
            while ( fileResources.hasNext() )
            {
                runLibDoc( fileResources.next().getFile().getAbsolutePath() );
            }
        }
        else
        {
            allowNoExcludesOrIncludes();
        }
    }

    private void allowNoExcludesOrIncludes()
        throws MojoExecutionException
    {
        if ( doExcludesOrIncludesExist() )
        {
            throw new MojoExecutionException(
                                              "excludes or includes are only allowed when libraryOrResourceDirectory is given" );
        }
    }

    private boolean doExcludesOrIncludesExist()
    {
        return ( excludes != null && excludes.length > 0 ) || ( includes != null && includes.length > 0 );
    }

    private void runLibDoc( String libraryOrResource )
        throws IOException
    {
        checkIfOutputDirectoryExists();

        String[] runArguments = generateRunArguments( libraryOrResource );
        PythonInterpreter pythonInterpreter = getPythonInterpreter( runArguments );
        pythonInterpreter.execfile( getLibdocScriptAsStream() );
    }

    private InputStream getLibdocScriptAsStream()
        throws FileNotFoundException
    {
        final InputStream ret;
        File libdocOnPythonPath = findOnPythonPath();
        if ( libdocOnPythonPath != null )
        {
            ret = new FileInputStream( libdocOnPythonPath );
        }
        else
        {
            String libdocScript = "/libdoc-2.6.3.py";
            ret = getClass().getResourceAsStream( libdocScript );
        }
        return ret;
    }

    private File findOnPythonPath()
    {
        File ret = null;
        List<File> extraPathDirectoriesWithDefault = getExtraPathDirectoriesWithDefault();
        for ( File dir : extraPathDirectoriesWithDefault )
        {
            File file = new File( dir, "libdoc.py" );
            if ( file.exists() )
            {
                ret = file;
                break;
            }
        }
        return ret;
    }

    private PythonInterpreter getPythonInterpreter( String[] runArguments )
    {
        PySystemState pySystemState = new PySystemState();
        for ( String argument : runArguments )
        {
            PyString pyString = new PyString( argument );
            pySystemState.argv.append( pyString );
        }
        PyStringMap dict = new PyStringMap();
        PythonInterpreter pythonInterpreter = new PythonInterpreter( dict, pySystemState );
        return pythonInterpreter;
    }

    private String[] generateRunArguments( String libraryOrResource )
    {
        // options must come before arguments, see arguments below
        ArrayList<String> generatedArguments = new ArrayList<String>();
        addNonEmptyStringToArguments( generatedArguments, argument, "--argument" );
        addNonEmptyStringToArguments( generatedArguments, name, "--name" );
        addFileToArguments( generatedArguments, output, "--output" );
        addNonEmptyStringToArguments( generatedArguments, format, "--format" );
        addNonEmptyStringToArguments( generatedArguments, title, "--title" );
        addNonEmptyStringToArguments( generatedArguments, styles, "--styles" );
        addFileListToArguments( generatedArguments, getExtraPathDirectoriesWithDefault(), "--pythonpath" );

        // arguments:
        generatedArguments.add( libraryOrResource );

        return generatedArguments.toArray( new String[generatedArguments.size()] );
    }

    private List<File> getExtraPathDirectoriesWithDefault()
    {
        final List<File> ret;
        if ( extraPathDirectories == null )
        {
            ret = Collections.singletonList( defaultExtraPath );
        }
        else
        {
            ret = Arrays.asList( extraPathDirectories );
        }
        return ret;
    }

    private void checkIfOutputDirectoryExists()
        throws IOException
    {
        if ( output == null )
        {
            return;
        }

        if ( !output.exists() )
        {
            if ( !output.mkdirs() )
            {
                throw new IOException( "Target output directory cannot be created." );
            }
        }
    }

    private Iterator<PlexusIoFileResource> extractFilesFromLibraryOrResourceDirectory( File directoryToRecurse )
        throws MojoExecutionException
    {
        try
        {
            if ( directoryToRecurse == null || !directoryToRecurse.exists() )
            {
                return Collections.<PlexusIoFileResource> emptyList().iterator();
            }

            return wildCardUtil.listFiles( directoryToRecurse, includes, excludes );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "failed to list files from " + directoryToRecurse, e );
        }
    }

    /**
     * List of files to include, in conjunction with libraryOrResourceDirectory. Specified as fileset patterns which are
     * relative to the libraryOrResourceDirectory. Example configuration:
     *
     * <pre>
     *   &lt;libraryOrResourceDirectory&gt;src/test/resources/tests&lt;/libraryOrResourceDirectory&gt;
     *   &lt;includes&gt;
     *     &lt;include&gt;**&#x2F;*resource.txt&lt;/include&gt;
     *   &lt;/includes&gt;
     * </pre>
     *
     * Documents all test resources ending in resource.txt found recursively below the libraryOrResourceDirectory.
     *
     * @parameter
     */
    private String[] includes;

    /**
     * List of files to exclude, in conjunction with libraryOrResourceDirectory. Specified as fileset patterns which are
     * relative to the libraryOrResourceDirectory.
     *
     * @parameter
     */
    private String[] excludes;

    /**
     * Possible arguments that a library needs.
     *
     * @parameter expression="${argument}"
     */
    private String argument;

    /**
     * Specifies whether to generate HTML or XML output. The default value is HTML.
     *
     * @parameter expression="${format}"
     */
    private String format;

    /**
     * Specifies where to write the generated documentation. If the given path is a directory, the documentation is
     * written there using a file name like '&lt;name&gt;.&lt;format&gt;'. If a file with that name already exists, an
     * index is added after the '&lt;name&gt;' part. If the given path is not a directory, it is used directly and possible
     * existing files are overwritten. The default value for the path is the directory where the script is executed
     * from.
     *
     * @parameter expression="${output}" default-value="${project.build.directory}/robotframework"
     */
    private File output;

    /**
     * Sets the name of the documented library or resource.
     *
     * @parameter expression="${name}"
     */
    private String name;

    /**
     * Sets the title of the generated HTML documentation. Underscores in the given title are automatically converted to
     * spaces.
     *
     * @parameter expression="${title}"
     */
    private String title;

    /**
     * Overrides the default styles. If the given 'styles' is a path to an existing files, styles will be read from it.
     * If it is string a 'NONE', no styles will be used. Otherwise the given text is used as-is.
     *
     * @parameter expression="${styles}"
     */
    private String styles;

    /**
     * Fully qualified path to the Java class (source code) or the resource file.
     * <p/>
     * e.g. src/main/java/com/test/ExampleLib.java
     *
     * @parameter expression="${libraryOrResourceFile}"
     */
    private File libraryOrResourceFile;

    /**
     * Fully qualified path to the directory where Java classes or resource files are located.
     * <p/>
     * e.g. src/main/java/com/test/
     *
     * @parameter expression="${libraryOrResourceDirectory}"
     */
    private File libraryOrResourceDirectory;

    /**
     * Library name. If the library name is used, it must be in the same format as in the Robot Framework test data when
     * importing libraries. In this case, the library is searched from PYTHONPATH (and from CLASSPATH, if on Jython).
     * For details about PYTHONPATH see {@link #extraPathDirectories}.
     * <p/>
     * e.g. MyCustomLibrary
     *
     * @parameter expression="${libraryName}"
     */
    private String libraryName;

    /**
     * Additional locations (directories, ZIPs, JARs) where to search test libraries from when they are imported. Maps
     * to Libdoc's --pythonpath option. Otherwise if no locations are declared, the default location is
     * ${project.basedir}/src/test/resources/robotframework/libraries.
     *
     * @parameter
     * @since 1.1
     */
    private File[] extraPathDirectories;

    /**
     * The default location where extra packages will be searched. Effective if extraPathDirectories attribute is not
     * used. Cannot be overridden.
     *
     * @parameter default-value="${project.basedir}/src/test/resources/robotframework/libraries"
     * @required
     * @readonly
     */
    private File defaultExtraPath;

}
