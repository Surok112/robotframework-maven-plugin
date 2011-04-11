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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.robotframework.RobotFramework;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Runs the Robot tests. Behaves like invoking the "jybot" command.
 * <p/>
 * Robot Framework tests cases are created in files and directories, and they are executed by configuring the path to
 * the file or directory in question to the testCasesDirectory configuration. The given file or directory creates the
 * top-level tests suites, which gets its name, unless overridden with the "name" option, from the file or directory
 * name.
 *
 * @goal run
 * @phase integration-test
 * @requiresDependencyResolution test
 */
public class RobotFrameworkMojo
    extends AbstractMojoWithLoadedClasspath
{

    protected void subclassExecute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( skipTests || skipITs || skipATs || skip )
        {
            getLog().info( "RobotFramework tests are skipped." );
            return;
        }
        String[] runArguments = generateRunArguments();

        int robotRunReturnValue = RobotFramework.run( runArguments );

        if ( robotRunReturnValue == 1 )
        {
            throw new MojoFailureException(
                "There are failing RobotFramework test cases. Check the logs for details." );
        }
        else if ( robotRunReturnValue > 1 )
        {
            throw new MojoExecutionException(
                "Failed to execute RobotFramework test cases. Check the logs for details." );
        }

    }

    private String[] generateRunArguments()
    {
        ArrayList<String> generatedArguments = new ArrayList<String>();

        addFileToArguments( generatedArguments, outputDirectory, "d" );
        addFileToArguments( generatedArguments, output, "o" );
        addFileToArguments( generatedArguments, log, "l" );
        addFileToArguments( generatedArguments, report, "r" );
        addFileToArguments( generatedArguments, summary, "S" );
        addFileToArguments( generatedArguments, xunitFile, "x" );
        addFileToArguments( generatedArguments, debugFile, "b" );
        addFileToArguments( generatedArguments, argumentFile, "A" );

        addStringToArguments( generatedArguments, name, "N" );
        addStringToArguments( generatedArguments, document, "D" );
        addStringToArguments( generatedArguments, runMode, "-runmode" );
        addStringToArguments( generatedArguments, splitOutputs, "-splitoutputs" );
        addStringToArguments( generatedArguments, logTitle, "-logtitle" );
        addStringToArguments( generatedArguments, reportTitle, "-reporttitle" );
        addStringToArguments( generatedArguments, reportBackground, "-reportbackground" );
        addStringToArguments( generatedArguments, summaryTitle, "-summarytitle" );
        addStringToArguments( generatedArguments, logLevel, "L" );
        addStringToArguments( generatedArguments, suiteStatLevel, "-suitestatlevel" );
        addStringToArguments( generatedArguments, monitorColors, "-monitorcolors" );

        addListToArguments( generatedArguments, metadata, "M" );
        addListToArguments( generatedArguments, tags, "G" );
        addListToArguments( generatedArguments, tests, "t" );
        addListToArguments( generatedArguments, suites, "s" );
        addListToArguments( generatedArguments, includes, "i" );
        addListToArguments( generatedArguments, excludes, "e" );
        addListToArguments( generatedArguments, criticalTags, "c" );
        addListToArguments( generatedArguments, nonCriticalTags, "n" );
        addListToArguments( generatedArguments, variables, "v" );
        addListToArguments( generatedArguments, variableFiles, "V" );
        addListToArguments( generatedArguments, tagStatIncludes, "-tagstatinclude" );
        addListToArguments( generatedArguments, tagStatExcludes, "-tagstatexclude" );
        addListToArguments( generatedArguments, combinedTagStats, "-tagstatcombine" );
        addListToArguments( generatedArguments, tagDocs, "-tagdoc" );
        addListToArguments( generatedArguments, tagStatLinks, "-tagstatlink" );
        addListToArguments( generatedArguments, listeners, "-listeners" );

        if ( extraPathDirectories == null )
        {
            addFileToArguments( generatedArguments, defaultExtraPath, "P" );
        }
        else
        {
            addFileListToArguments( generatedArguments, Arrays.asList( extraPathDirectories), "P" );
        }

//        addStringToArguments( generatedArguments, pythonPath, "P" );
//        addFileToArguments( generatedArguments, extraTestLibraries, "P" );

        if ( timestampOutputs )
        {
            generatedArguments.add( "-T" );
        }
        if ( warnOnSkippedFiles )
        {
            generatedArguments.add( "--warnonskippedfiles" );
        }

        generatedArguments.add( testCasesDirectory.getPath() );

        return generatedArguments.toArray( new String[generatedArguments.size()] );
    }

    /**
     * Configures where generated reports are to be placed.
     *
     * @parameter default-value="${project.build.directory}/robotframework"
     */
    private File outputDirectory;

    /**
     * The directory where the tests cases are located.
     *
     * @parameter default-value="${project.basedir}/src/test/resources/robot/tests"
     */
    private File testCasesDirectory;

    /**
     * Sets the name of the top-level tests suites.
     *
     * @parameter
     */
    private String name;

    /**
     * Sets the documentation of the top-level tests suites.
     *
     * @parameter
     */
    private String document;

    /**
     * Sets free metadata for the top level tests suites.
     *
     * @parameter
     */
    private List<String> metadata;

    /**
     * Sets the tags(s) to all executed tests cases.
     *
     * @parameter
     */
    private List<String> tags;

    /**
     * Selects the tests cases by name.
     *
     * @parameter
     */
    private List<String> tests;

    /**
     * Selects the tests suites by name.
     *
     * @parameter
     */
    private List<String> suites;

    /**
     * Selects the tests cases by tags.
     *
     * @parameter
     */
    private List<String> includes;

    /**
     * Selects the tests cases by tags.
     *
     * @parameter
     */
    private List<String> excludes;

    /**
     * Tests that have the given tags are considered critical.
     *
     * @parameter
     */
    private List<String> criticalTags;

    /**
     * Tests that have the given tags are not critical.
     *
     * @parameter
     */
    private List<String> nonCriticalTags;

    /**
     * Sets the execution mode for this tests run. Valid modes are ContinueOnFailure, ExitOnFailure, SkipTeardownOnExit,
     * DryRun, and Random:&lt;what&gt;.
     *
     * @parameter
     */
    private String runMode;

    /**
     * Sets individual variables. Use the format "name:value"
     *
     * @parameter
     */
    private List<String> variables;

    /**
     * Sets variables using variables files. Use the format "path:args"
     *
     * @parameter
     */
    private List<String> variableFiles;

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
    private boolean timestampOutputs;

    /**
     * Splits output and log files.
     *
     * @parameter
     */
    private String splitOutputs;

    /**
     * Sets a title for the generated tests log.
     *
     * @parameter
     */
    private String logTitle;

    /**
     * Sets a title for the generated tests report.
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
    private List<String> tagStatIncludes;

    /**
     * Excludes these tags from the Statistics by Tag and Test Details by Tag tables in outputs.
     *
     * @parameter
     */
    private List<String> tagStatExcludes;

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
    private List<String> combinedTagStats;

    /**
     * Adds documentation to the specified tags.
     *
     * @parameter
     */
    private List<String> tagDocs;

    /**
     * Adds external links to the Statistics by Tag table in outputs. Use the format "pattern:link:title"
     *
     * @parameter
     */
    private List<String> tagStatLinks;

    /**
     * Sets a listeners for monitoring tests execution. Use the format "ListenerWithArgs:arg1:arg2" or simply
     * "ListenerWithoutArgs"
     *
     * @parameter
     */
    private List<String> listeners;

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

//    /**
//     * Additional locations (directories, ZIPs, JARs) where to search test libraries from when they are imported, e.g.
//     * ${project.basedir}/src/test/resources/robot/libraries. Multiple paths can be given by separating them with a
//     * colon (':').
//     *
//     * @parameter default-value="${project.basedir}/src/test/resources/robot/libraries"
//     * @since 1.1
//     */
//    private String pythonPath;
//
//    /**
//     * Additional locations where to search tests libraries from when they are imported. A path to where your extra
//     * tests libraries are located. e.g. ${project.basedir}/src/test/resources/robot/libraries
//     *
//     * @parameter default-value="${project.basedir}/src/test/resources/robot/libraries"
//     * @since 1.1
//     * @deprecated use pythonPath instead
//     */
//    private File extraTestLibraries;


    /**
     * Additional locations (directories, ZIPs, JARs) where to search test libraries from when they are imported.
     * Otherwise if none is declared, the default location is ${project.basedir}/src/test/resources/robot/libraries.
     *
     * @parameter
     * @since 1.1
     */
    private File[] extraPathDirectories;

    /**
     * The default location where extra packages will be searched. Effective if extraPath attribute is not used. Cannot
     * be overridden.
     *
     * @parameter default-value="${project.basedir}/src/test/resources/robot/libraries"
     * @required
     * @readonly
     */
    private File defaultExtraPath;

    /**
     * Using ANSI colors in console. Normally colors work in unixes but not in Windows. Default is 'on'.
     * <ul>
     * <li>'on' - use colors in unixes but not in Windows</li>
     * <li>'off' - never use colors</li>
     * <li>'force' - always use colors (also in Windows)</li>
     * </ul>
     *
     * @parameter
     * @since 1.1
     */
    private String monitorColors;

    /**
     * Skip tests.
     *
     * @parameter expression="${skipTests}"
     * @since 1.1
     */
    private boolean skipTests;

    /**
     * Skip acceptance tests executed by this plugin.
     *
     * @parameter expression="${skipATs}"
     * @since 1.1
     */
    private boolean skipATs;

    /**
     * Skip acceptance tests executed by this plugin together with other integration tests, e.g. tests run by the
     * maven-failsafe-plugin.
     *
     * @parameter expression="${skipATs}"
     * @since 1.1
     */
    private boolean skipITs;

    /**
     * Skip tests, bound to -Dmaven.test.skip, which suppresses test compilation as well.
     *
     * @parameter default-value="false" expression="${maven.test.skip}"
     */
    private boolean skip;

}