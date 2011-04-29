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

import com.googlecode.util.WildCardUtil;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResource;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

/**
 * Runs the "libdoc" command to generate documentation of user libraries.
 * <p/>
 * This script can generate keyword documentation in HTML and XML formats. The former is suitable for humans and the
 * latter for RIDE, RFDoc, and other tools. This script can also upload XML documentation to RFDoc system.
 * <p/>
 * Documentation can be created for both test libraries and resource files. All library and resource file types are
 * supported, and also earlier generated documentation in XML format can be used as input.
 *
 * @goal libdoc
 * @requiresDependencyResolution test
 */
public class LibDocMojo
    extends AbstractMojoWithLoadedClasspath
{
    protected void subclassExecute()
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            checkIfOutputDirectoryExists();

            if ( libraryOrResourceDirectory != null )
            {
                Iterator<PlexusIoFileResource> fileResources =
                    extractFilesFromLibraryOrResourceDirectory( libraryOrResourceDirectory );
                while ( fileResources.hasNext() )
                {
                    runLibDoc( fileResources.next().getFile() );
                }

            }
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }

        if ( libraryOrResourceFile != null )
        {
            runLibDoc( libraryOrResourceFile );
        }

    }

    private void runLibDoc( File libraryOrResourceFile )
        throws MojoExecutionException
    {
        try
        {
            PythonInterpreter pythonInterpreter = new PythonInterpreter();

            pythonInterpreter.execfile( getClass().getResourceAsStream( "/libdoc.py" ) );

            pythonInterpreter.set( "libraryOrResourceFile", extractPath( libraryOrResourceFile ) );
            pythonInterpreter.set( "argument", argument );
            pythonInterpreter.set( "name", name );
            pythonInterpreter.set( "output", extractPath( output ) );
            pythonInterpreter.set( "format", format );
            pythonInterpreter.set( "title", title );
            pythonInterpreter.set( "styles", styles );

            pythonInterpreter.execfile( getClass().getResourceAsStream( "/libdoc_exec.py" ) );

        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "There was an error executing libdoc.py.", e );
        }
    }

    private String extractPath( File file )
    {
        return file == null ? null : file.getPath();
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
        throws IOException
    {
        if ( directoryToRecurse == null || !directoryToRecurse.exists() )
        {
            return Collections.<PlexusIoFileResource>emptyList().iterator();
        }

        WildCardUtil wildCardUtil = new WildCardUtil();
        final Iterator<PlexusIoFileResource> ret;
        if ( includes != null || excludes != null )
        {
            ret = wildCardUtil.listFiles( directoryToRecurse, includes, excludes );
        }
        else
        {
            final String[] defaultIncludes =
                { "**/*.txt", "**/*.java", "**/*.html", "**/*.htm", "**/*.xhtml", "**/*.tsv", "**/*.rst", "**/*.rest",
                    "**/*.py", };
            ret = wildCardUtil.listFiles( directoryToRecurse, defaultIncludes, excludes );
        }
        return ret;
    }

    /**
     * List of files to include, in conjunction with libraryOrResourceDirectory. Specified as fileset patterns which are
     * relative to the input directory whose contents is being packaged into the JAR.
     *
     * @parameter
     */
    private String[] includes;

    /**
     * List of files to exclude, in conjunction with libraryOrResourceDirectory. Specified as fileset patterns which are
     * relative to the input directory whose contents is being packaged into the JAR.
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
     * index is added after the '<name>' part. If the given path is not a directory, it is used directly and possible
     * existing files are overwritten. The default value for the path is the directory where the script is executed
     * from.
     *
     * @parameter expression="${output}" default-value="${project.build.directory}/robot"
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
     * Fully qualified path to the directory where the Java classes or resource files are located.
     * <p/>
     * e.g. src/main/java/com/test/
     *
     * @parameter expression="${libraryOrResourceDirectory}"
     */
    private File libraryOrResourceDirectory;

}
