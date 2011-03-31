package com.googlecode;

/*
 * Copyright 2011 Michael Mallete
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
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
 * @requiresDependencyResolution test
 */
public class LibDocMojo extends AbstractMojoWithLoadedClasspath
{
  protected void subclassExecute() throws MojoExecutionException, MojoFailureException
  {
    try
    {
      checkIfOutputDirectoryExists();
    }
    catch (IOException e)
    {
      throw new MojoExecutionException(e.getMessage(), e);
    }

    if (libraryOrResourceDirectory != null)
    {
      for (File currentFile : extractFilesFromLibraryOrResourceDirectory(libraryOrResourceDirectory))
      {
        runLibDoc(currentFile);
      }

    }

    if (libraryOrResourceFile != null)
    {
      runLibDoc(libraryOrResourceFile);
    }

  }

  private void runLibDoc(File libraryOrResourceFile) throws MojoExecutionException
  {
    try
    {
      PythonInterpreter pythonInterpreter = new PythonInterpreter();

      pythonInterpreter.execfile(getClass().getResourceAsStream("/libdoc.py"));

      pythonInterpreter.set("libraryOrResourceFile", extractPath(libraryOrResourceFile));
      pythonInterpreter.set("argument", argument);
      pythonInterpreter.set("name", name);
      pythonInterpreter.set("output", extractPath(output));
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

  private String extractPath(File file)
  {
    return file == null ? null : file.getPath();
  }

  private void checkIfOutputDirectoryExists() throws IOException
  {
    if (output == null)
    {
      return;
    }

    if (!output.exists())
    {
      if (!output.mkdirs())
      {
        throw new IOException("Target output directory cannot be created.");
      }
    }
  }

  private Collection<File> extractFilesFromLibraryOrResourceDirectory(File directoryToRecurse)
  {
    if (directoryToRecurse == null || !directoryToRecurse.exists())
    {
      return null;
    }

    if(includes != null || excludes != null)
    {
      WildCardUtil wildCardUtil = new WildCardUtil(includes, excludes);
      return wildCardUtil.listFiles(directoryToRecurse);
    }

    File[] qualifiedFiles = directoryToRecurse.listFiles(new FilenameFilter()
    {
      public boolean accept(File file, String name)
      {
        return name.toLowerCase().endsWith("txt") || name.toLowerCase().endsWith("java")
            || name.toLowerCase().endsWith("html") || name.toLowerCase().endsWith("htm")
            || name.toLowerCase().endsWith("xhtml") || name.toLowerCase().endsWith("tsv")
            || name.toLowerCase().endsWith("rst") || name.toLowerCase().endsWith("rest")
            || name.toLowerCase().endsWith("py");
      }
    });

    return Arrays.asList(qualifiedFiles);
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
   * Sets the title of the generated HTML documentation. Underscores
   * in the given title are automatically converted to spaces.
   *
   * @parameter expression="${title}"
   */
  private String title;


  /**
   * Overrides the default styles. If the given 'styles'
   * is a path to an existing files, styles will be read
   * from it. If it is string a 'NONE', no styles will be
   * used. Otherwise the given text is used as-is.
   *
   * @parameter expression="${styles}"
   */
  private String styles;


  /**
   * Fully qualified path to the Java class (source code) or the
   * resource file.
   * <p/>
   * e.g. src/main/java/com/test/ExampleLib.java
   *
   * @parameter expression="${libraryOrResourceFile}"
   */
  private File libraryOrResourceFile;


  /**
   * Fully qualified path to the directory where the Java classes
   * or resource files are located.
   * <p/>
   * e.g. src/main/java/com/test/
   *
   * @parameter expression="${libraryOrResourceDirectory}"
   */
  private File libraryOrResourceDirectory;

  /**
   * Use in conjuction with "libraryOrResourceDirectory." The directory is recursively searched
   * for files matching these values. The wildcards "*" can be used to signify multiple possible values
   * while "?" can be used to signify single characters. e.g. *esources?.* will be able to process
   * thisisaresources2.txt, resources1.html, etc
   *
   * @parameter
   */
  private List<String> includes;


  /**
   * Use in conjuction with "libraryOrResourceDirectory." The directory is recursively searched
   * and files matching these values are excluded. The wildcards "*" can be used to signify multiple possible values
   * while "?" can be used to signify single characters. e.g. *test?.* will be able to process
   * thisisatest2.txt, test1.html, etc
   *
   *
   * @parameter
   */
  private List<String> excludes;

}
