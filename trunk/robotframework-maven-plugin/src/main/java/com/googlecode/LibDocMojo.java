package com.googlecode;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.python.util.PythonInterpreter;

import java.io.File;

/**
 * Runs the "libdoc" command to generate documentation of user libraries.
 * <p/>
 * This script can generate keyword documentation in HTML and XML formats. The
 * former is suitable for humans and the latter for RIDE, RFDoc, and other tools.
 * This script can also upload XML documentation to RFDoc system.
 * <p/>
 * Documentation can be created for both test libraries and resource files. All
 * library and resource file types are supported, and also earlier generated
 * documentation in XML format can be used as input.
 *
 * @goal libdoc
 * @requiresDependencyResolution compile
 */
public class LibDocMojo extends AbstractMojoWithLoadedClasspath
{
  public void subclassExecute() throws MojoExecutionException, MojoFailureException
  {
    try
    {

      PythonInterpreter pythonInterpreter = new PythonInterpreter();

      pythonInterpreter.execfile(getClass().getResourceAsStream("/libdoc.py"));

      pythonInterpreter.set("libraryOrResourceFile", libraryOrResourceFile);
      pythonInterpreter.set("argument", argument);
      pythonInterpreter.set("name", name);
      pythonInterpreter.set("output", output == null ? null : output.getPath());
      pythonInterpreter.set("format", format);
      pythonInterpreter.set("title", title);
      pythonInterpreter.set("styles", styles);

      pythonInterpreter.execfile(getClass().getResourceAsStream("/libdoc_exec.py"));


    }
    catch (Exception e)
    {
      throw new MojoExecutionException("There was an error executing libdoc.py.", e);
    }

  }

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
   * Specifies where to write the generated documentation.
   * If the given path is a directory, the documentation is
   * written there using a file name like '&lt;name&gt;.&lt;format&gt;'.
   * If a file with that name already exists, an index is added after
   * the '<name>' part. If the given path is not a directory, it is used
   * directly and possible existing files are overwritten. The default
   * value for the path is the directory where the script is executed from.
   *
   * @parameter expression="${output}"
   */
  private File output;

  /**
   * Sets the name of the documented library or resource.
   *
   * @parameter expression="${name}"
   */
  private String name;

  /**
   * Sets the title of the generated HTML documentation. Underscores
   * in the given title are automatically converted to spaces.
   *
   * @parameter expression="${title}"
   */
  private String title;


  /**
   * Fully qualified path to the Java class (source code) or the
   * resource file.
   * e.g. src/main/java/com/test/ExampleLib.java
   *
   * @parameter expression="${libraryOrResourceFile}"
   * @required
   */
  private String libraryOrResourceFile;


  /**
   * Overrides the default styles. If the given 'styles'
   * is a path to an existing files, styles will be read
   * from it. If it is string a 'NONE', no styles will be
   * used. Otherwise the given text is used as-is.
   *
   * @parameter expression="${styles}"
   */
  private String styles;

}
