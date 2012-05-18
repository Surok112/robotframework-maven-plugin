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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.robotframework.RobotFramework;

/**
 * Runs the "libdoc" command to generate documentation of user libraries.
 * <p/>
 * Uses the libdoc bundled in Robot Framework jar distribution. For more help, run
 * <code>java -jar robotframework.jar libdoc --help</code>.
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
            documentLibraryOrResourceFile();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Failed to execute libdoc script", e );
        }
    }

    private void documentLibraryOrResourceFile()
        throws MojoExecutionException, IOException
    {
        if ( libraryOrResourceFile != null )
        {
            File libOrResource = new File(libraryOrResourceFile);
            if ( libOrResource.exists() )
            {
                runLibDoc( libOrResource.getAbsolutePath() );
            } else 
            {
                runLibDoc( libraryOrResourceFile );
            }
            
        }
    }

    public void runLibDoc( String libraryOrResource )
        throws IOException
    {
        checkIfOutputDirectoryExists();

        String[] runArguments = generateRunArguments( libraryOrResource );
        
        RobotFramework.run(runArguments);
    }


    private String[] generateRunArguments( String libraryOrResource )
    {
        // options must come before arguments, see arguments below
        ArrayList<String> generatedArguments = new ArrayList<String>();
        generatedArguments.add("libdoc");
        addNonEmptyStringToArguments( generatedArguments, name, "--name" );
        addNonEmptyStringToArguments( generatedArguments, format, "--format" );
        addFileListToArguments( generatedArguments, getExtraPathDirectoriesWithDefault(), "--pythonpath" );

        // arguments:
        generatedArguments.add( libraryOrResource );
        generatedArguments.add( outputFile.toString() );
        System.out.println(generatedArguments);
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


    private String format;

    /**
     * Specifies where to write the generated documentation. If the given path is a directory, the documentation is
     * written there using a file name like '&lt;name&gt;.&lt;format&gt;'. If a file with that name already exists, an
     * index is added after the '&lt;name&gt;' part. If the given path is not a directory, it is used directly and
     * possible existing files are overwritten. The default value for the path is the directory where the script is
     * executed from.
     *
     * @parameter expression="${output}" default-value="${project.build.directory}/robotframework-reports"
     */
    private File output;
    
    
    
    private File outputFile;

    /**
     * Sets the name of the documented library or resource.
     *
     * @parameter expression="${name}"
     */
    private String name;

    /**
     * Name or path of the documented library or resource file.
     * <p>
     * Name must be in the same format as when used in Robot Framework test data, for example
     * <code>BuiltIn</code> or <code>com.acme.FooLibrary</code>. When name is used, the
     * library is imported the same as when running the tests. Use {@link #extraPathDirectories}
     * to set PYTHONPATH/CLASSPATH accordingly.
     * <p>
     * Paths are considered relative to the location of <code>pom.xml</code> and
     * must point to a valid Python/Java source file or a resource file. For example
     * <code>src/main/java/com/test/ExampleLib.java</code>
     *
     * @parameter expression="${libraryOrResourceFile}"
     */
    private String libraryOrResourceFile;

    /**
     * Fully qualified path to the directory where Java classes or resource files are located.
     * <p/>
     * e.g. src/main/java/com/test/
     *
     * @parameter expression="${extraPathDirectories}"
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
