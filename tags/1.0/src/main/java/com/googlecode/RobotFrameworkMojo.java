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
import org.robotframework.RobotFramework;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs the "jybot" command.
 * <p/>
 * Robot Framework test cases are created in files and directories, and
 * they are executed by giving the path to the file or directory in question
 * to the "jybot" command. The path can be absolute or, more commonly,
 * relative to the directory where tests are executed from. The given file or
 * directory creates the top-level test suite, which gets its name, unless
 * overridden with the "name" option, from the file or directory name.
 *
 * @goal jybot
 * @phase integration-test
 * @requiresDependencyResolution test
 */
public class RobotFrameworkMojo extends AbstractMojoWithLoadedClasspath
{

  public void subclassExecute() throws MojoExecutionException, MojoFailureException
  {
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

  private String[] generateRunArguments()
  {
    ArrayList<String> generatedArguments = new ArrayList<String>();

    addFileToArguments(generatedArguments, outputDirectory, "d");
    addFileToArguments(generatedArguments, output, "o");
    addFileToArguments(generatedArguments, log, "l");
    addFileToArguments(generatedArguments, report, "r");
    addFileToArguments(generatedArguments, summary, "S");
    addFileToArguments(generatedArguments, xunitFile, "x");
    addFileToArguments(generatedArguments, debugFile, "b");
    addFileToArguments(generatedArguments, argumentFile, "A");

    addStringToArguments(generatedArguments, name, "N");
    addStringToArguments(generatedArguments, document, "D");
    addStringToArguments(generatedArguments, runMode, "-runmode");
    addStringToArguments(generatedArguments, splitOutputs, "-splitoutputs");
    addStringToArguments(generatedArguments, logTitle, "-logtitle");
    addStringToArguments(generatedArguments, reportTitle, "-reporttitle");
    addStringToArguments(generatedArguments, reportBackground, "-reportbackground");
    addStringToArguments(generatedArguments, summaryTitle, "-summarytitle");
    addStringToArguments(generatedArguments, logLevel, "L");
    addStringToArguments(generatedArguments, suiteStatLevel, "-suitestatlevel");

    addListToArguments(generatedArguments, metadata, "M");
    addListToArguments(generatedArguments, tag, "G");
    addListToArguments(generatedArguments, test, "t");
    addListToArguments(generatedArguments, suite, "s");
    addListToArguments(generatedArguments, include, "i");
    addListToArguments(generatedArguments, exclude, "e");
    addListToArguments(generatedArguments, critical, "c");
    addListToArguments(generatedArguments, nonCritical, "n");
    addListToArguments(generatedArguments, variable, "v");
    addListToArguments(generatedArguments, variableFile, "V");
    addListToArguments(generatedArguments, tagStatInclude, "-tagstatinclude");
    addListToArguments(generatedArguments, tagStatExclude, "-tagstatexclude");
    addListToArguments(generatedArguments, tagStatCombine, "-tagstatcombine");
    addListToArguments(generatedArguments, tagDoc, "-tagdoc");
    addListToArguments(generatedArguments, tagStatLink, "-tagstatlink");
    addListToArguments(generatedArguments, listener, "-listener");

    if (timestampOuputs)
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


  /**
   * Configures where generated reports are to be placed.
   *
   * @parameter default-value="${project.build.directory}/robot"
   */
  private File outputDirectory;

  /**
   * The directory where the test cases are located.
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
  private List<String> metadata;


  /**
   * Sets the tag(s) to all executed test cases.
   *
   * @parameter
   */
  private List<String> tag;


  /**
   * Selects the test cases by name.
   *
   * @parameter
   */
  private List<String> test;


  /**
   * Selects the test suites by name.
   *
   * @parameter
   */
  private List<String> suite;


  /**
   * Selects the test cases by tag.
   *
   * @parameter
   */
  private List<String> include;


  /**
   * Selects the test cases by tag.
   *
   * @parameter
   */
  private List<String> exclude;


  /**
   * Tests that have the given tag are considered critical.
   *
   * @parameter
   */
  private List<String> critical;


  /**
   * Tests that have the given tag are not critical.
   *
   * @parameter
   */
  private List<String> nonCritical;


  /**
   * Sets the execution mode for this test run. Valid modes are ContinueOnFailure, ExitOnFailure, SkipTeardownOnExit, DryRun, and Random:&lt;what&gt;.
   *
   * @parameter
   */
  private String runMode;


  /**
   * Sets individual variables. Use the format "name:value"
   *
   * @parameter
   */
  private List<String> variable;


  /**
   * Sets variables using variable files. Use the format "path:args"
   *
   * @parameter
   */
  private List<String> variableFile;

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
  private boolean timestampOuputs;


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
  private List<String> tagStatInclude;


  /**
   * Excludes these tags from the Statistics by Tag and Test Details by Tag tables in outputs.
   *
   * @parameter
   */
  private List<String> tagStatExclude;


  /**
   * Defines how many levels to show in the Statistics by Suite table in outputs.
   *
   * @parameter
   */
  private String suiteStatLevel;

  /**
   * Creates combined statistics based on tags. Use the format "tags:title"
   *
   * @parameter
   */
  private List<String> tagStatCombine;


  /**
   * Adds documentation to the specified tags.
   *
   * @parameter
   */
  private List<String> tagDoc;


  /**
   * Adds external links to the Statistics by Tag table in outputs. Use the format "pattern:link:title"
   *
   * @parameter
   */
  private List<String> tagStatLink;


  /**
   * Sets a listener for monitoring test execution. Use the format "ListenerWithArgs:arg1:arg2" or simply "ListenerWithoutArgs"
   *
   * @parameter
   */
  private List<String> listener;


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
