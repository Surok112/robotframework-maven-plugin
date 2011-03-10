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

import org.apache.commons.lang.StringUtils;
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
import java.util.Map;

/**
 * Runs the "jybot" command.
 *
 * @goal jybot
 * @phase integration-test
 * @requiresDependencyResolution test
 */
public class RobotFrameworkMojo extends AbstractMojo
{

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

    addFileToArguments(generatedArguments, outputDirectory, "d");
    addStringToArguments(generatedArguments, name, "N");
    addStringToArguments(generatedArguments, document, "D");
    addStringToArguments(generatedArguments, metadata, "M");
    addStringToArguments(generatedArguments, tag, "G");
    addStringToArguments(generatedArguments, test, "t");
    addStringToArguments(generatedArguments, suite, "s");
    addStringToArguments(generatedArguments, include, "i");
    addStringToArguments(generatedArguments, exclude, "e");
    addStringToArguments(generatedArguments, critical, "c");
    addStringToArguments(generatedArguments, nonCritical, "n");
    addStringToArguments(generatedArguments, runMode, "-runmode");
    addStringToArguments(generatedArguments, variable, "v");
    addStringToArguments(generatedArguments, variableFile, "V");
    addStringToArguments(generatedArguments, splitOutputs, "-splitoutputs");
    addStringToArguments(generatedArguments, logTitle, "-logtitle");
    addStringToArguments(generatedArguments, reportTitle, "-reporttitle");
    addStringToArguments(generatedArguments, reportBackground, "-reportbackground");
    addStringToArguments(generatedArguments, summaryTitle, "-summarytitle");
    addStringToArguments(generatedArguments, logLevel, "L");
    addStringToArguments(generatedArguments, suiteStatLevel, "-suitestatlevel");
    addStringToArguments(generatedArguments, tagStatInclude, "-tagstatinclude");
    addStringToArguments(generatedArguments, tagStatExclude, "-tagstatexclude");
    addStringToArguments(generatedArguments, tagStatCombine, "-tagstatcombine");
    addStringToArguments(generatedArguments, tagDoc, "-tagdoc");
    addStringToArguments(generatedArguments, tagStatLink, "-tagstatlink");
    addStringToArguments(generatedArguments, listener, "-listener");

    addFileToArguments(generatedArguments, output, "o");
    addFileToArguments(generatedArguments, log, "l");
    addFileToArguments(generatedArguments, report, "r");
    addFileToArguments(generatedArguments, summary, "S");
    addFileToArguments(generatedArguments, xunitFile, "x");
    addFileToArguments(generatedArguments, debugFile, "b");
    addFileToArguments(generatedArguments, argumentFile, "A");


    if (timestamOuputs)
    {
      generatedArguments.add("-T");
    }
    if (warnOnSkippedFiles)
    {
      generatedArguments.add("--warnonskippedfiles");
    }

    if (testCasesDirectory != null && !testCasesDirectory.getPath().isEmpty())
    {
      generatedArguments.add(testCasesDirectory.getPath());
    }

    return generatedArguments.toArray(new String[generatedArguments.size()]);
  }

  private void addFileToArguments(List<String> arguments, File file, String flag)
  {
    if (file != null && !file.getPath().isEmpty())
    {
      arguments.add("-" + flag);
      arguments.add(file.getPath());
    }
  }

  private void addStringToArguments(List<String> arguments, String variableToAdd, String flag)
  {
    if (!StringUtils.isEmpty(variableToAdd))
    {
      arguments.add("-" + flag);
      arguments.add(variableToAdd);
    }
  }


  /**
   * @parameter expression="${project.compileClasspathElements}"
   * @required
   * @readonly
   */
  private List<String> classpathElements;

  /**
   * Configures where generated reports are to be placed. Defaults to "${project.build.directory}/robot"
   *
   * @parameter default-value="${project.build.directory}/robot"
   */
  private File outputDirectory;

  /**
   * The directory where the test cases are located. Defaults to "${project.basedir}/src/test/resources/robot"
   *
   * @parameter default-value="${project.basedir}/src/test/resources/robot"
   */
  private File testCasesDirectory;

  /**
   * Sets the name of the top-level test suite.
   *
   * @parameter
   */
  private String name;


  /**
   * Sets the documentation of the top-level test suite.
   *
   * @parameter
   */
  private String document;


  /**
   * Sets free metadata for the top level test suite.
   *
   * @parameter
   */
  private String metadata;


  /**
   * Sets the tag(s) to all executed test cases.
   *
   * @parameter
   */
  private String tag;


  /**
   * Selects the test cases by name.
   *
   * @parameter
   */
  private String test;


  /**
   * Selects the test suites by name.
   *
   * @parameter
   */
  private String suite;


  /**
   * Selects the test cases by tag.
   *
   * @parameter
   */
  private String include;


  /**
   * Selects the test cases by tag.
   *
   * @parameter
   */
  private String exclude;


  /**
   * Tests that have the given tag are considered critical.
   *
   * @parameter
   */
  private String critical;


  /**
   * Tests that have the given tag are not critical.
   *
   * @parameter
   */
  private String nonCritical;


  /**
   * Sets the execution mode for this test run. Valid modes are ContinueOnFailure, ExitOnFailure, SkipTeardownOnExit, DryRun, and Random:<what>.
   *
   * @parameter
   */
  private String runMode;


  /**
   * Sets individual variables.
   *
   * @parameter
   */
  private String variable;


  /**
   * Sets variables using variable files.
   *
   * @parameter
   */
  private String variableFile;

  /**
   * Sets the path to the generated output file.
   *
   * @parameter
   */
  private File output;


  /**
   * Sets the path to the generated log file.
   *
   * @parameter
   */
  private File log;


  /**
   * Sets the path to the generated report file.
   *
   * @parameter
   */
  private File report;


  /**
   * Sets the path to the generated summary file.
   *
   * @parameter
   */
  private File summary;

  /**
   * Sets the path to the generated XUnit compatible result file. New in Robot Framework 2.5.5.
   *
   * @parameter
   */
  private File xunitFile;

  /**
   * A debug file that is written during execution.
   *
   * @parameter
   */
  private File debugFile;


  /**
   * Adds a timestamp to all output files.
   *
   * @parameter
   */
  private boolean timestamOuputs;


  /**
   * Splits output and log files.
   *
   * @parameter
   */
  private String splitOutputs;


  /**
   * Sets a title for the generated test log.
   *
   * @parameter
   */
  private String logTitle;


  /**
   * Sets a title for the generated test report.
   *
   * @parameter
   */
  private String reportTitle;

  /**
   * Sets background colors for the generated report and summary.
   *
   * @parameter
   */
  private String reportBackground;


  /**
   * Sets a title for the generated summary report.
   *
   * @parameter
   */
  private String summaryTitle;

  /**
   * Sets the threshold level for logging.
   *
   * @parameter
   */
  private String logLevel;

  /**
   * Includes only these tags in the Statistics by Tag and Test Details by Tag tables in outputs.
   *
   * @parameter
   */
  private String tagStatInclude;


  /**
   * Excludes these tags from the Statistics by Tag and Test Details by Tag tables in outputs.
   *
   * @parameter
   */
  private String tagStatExclude;


  /**
   * Defines how many levels to show in the Statistics by Suite table in outputs.
   *
   * @parameter
   */
  private String suiteStatLevel;

  /**
   * Creates combined statistics based on tags.
   *
   * @parameter
   */
  private String tagStatCombine;


  /**
   * Adds documentation to the specified tags.
   *
   * @parameter
   */
  private String tagDoc;


  /**
   * Adds external links to the Statistics by Tag table in outputs.
   *
   * @parameter
   */
  private String tagStatLink;


  /**
   * Sets a listener for monitoring test execution.
   *
   * @parameter
   */
  private String listener;  //dont use colon


  /**
   * Show a warning when an invalid file is skipped.
   *
   * @parameter
   */
  private boolean warnOnSkippedFiles;

  /**
   * A text file to read more arguments from.
   *
   * @parameter
   */
  private File argumentFile;
}
