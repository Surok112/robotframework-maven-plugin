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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.robotframework.RobotFramework;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs the "jybot" command.
 *
 * @goal jybot
 * @phase integration-test
 * @requiresDependencyResolution test
 */
public class RobotFrameworkMojo extends AbstractMojo
{

  /**
   * Configures where generated reports are to be placed. Defaults to "${project.build.directory}/robot"
   *
   * @parameter default-value="${project.build.directory}/robot"
   * @required
   */
  private File outputDirectory;

  /**
   * The directory where the test cases are located. Defaults to "${project.basedir}/src/main/resources"
   *
   * @parameter default-value="${project.basedir}/src/main/resources"
   */
  private File testCasesDirectory;

  /**
   * @parameter expression="${project.compileClasspathElements}"
   * @required
   * @readonly
   */
  private List<String> classpathElements;


  public void execute() throws MojoExecutionException, MojoFailureException
  {
    loadClassPath();

    String[] runArguments = generateRunArguments();

    int robotRunReturnValue = RobotFramework.run(runArguments);

    if (robotRunReturnValue == 1)
    {
      throw new MojoFailureException("There are failing test cases. Check the logs for details.");
    }
    else if (robotRunReturnValue > 1)
    {
      throw new MojoExecutionException("Failed to execute test cases. Check the logs for details.");
    }


  }

  private void loadClassPath() throws MojoExecutionException
  {
    List<URL> urls = new ArrayList<URL>();

    if (classpathElements != null)
    {
      for (String element : classpathElements)
      {
        File elementFile = new File(element);
        try
        {
          urls.add(elementFile.toURI().toURL());
        }
        catch (MalformedURLException e)
        {
          throw new MojoExecutionException("Classpath loading error: " + element);
        }
      }
    }

    if (urls.size() > 0)
    {
      ClassLoader realm = new URLClassLoader(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
      Thread.currentThread().setContextClassLoader(realm);
    }
  }

  private String[] generateRunArguments()
  {
    ArrayList<String> generatedArguments = new ArrayList<String>();

    if (outputDirectory != null && !outputDirectory.getPath().isEmpty())
    {
      generatedArguments.add("-d");
      generatedArguments.add(outputDirectory.getPath());
    }

    if (testCasesDirectory != null && !testCasesDirectory.getPath().isEmpty())
    {
      generatedArguments.add(testCasesDirectory.getPath());
    }

    return generatedArguments.toArray(new String[generatedArguments.size()]);
  }
}
