package com.googlecode;

import java.io.File;
import java.util.Arrays;

import org.apache.maven.it.Verifier;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class AcceptanceTestAndVerifyMojoIT
    extends AbstractMojoTestCase
{

    public void testAcceptanceTestMojos()
        throws Exception
    {

        File testDir = getTestFile( "src/test/projects/acceptance-and-verify" );

        Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        verifier.deleteDirectory( "target/robotframework-reports" );

        // turns on debugging
        // List<String> cliOptions = new ArrayList<String>();
        // cliOptions.add("-X");
        // verifier.setCliOptions(cliOptions);
        verifier.executeGoals( Arrays.asList( "com.googlecode.robotframework-maven-plugin:robotframework-maven-plugin:acceptance-test",
                                             "com.googlecode.robotframework-maven-plugin:robotframework-maven-plugin:verify" ) );
        // verifier.executeGoal("com.googlecode.robotframework-maven-plugin:robotframework-maven-plugin:verify");

        // writes streams to src/test/projects/oaw-workflow/log.txt
        verifier.displayStreamBuffers();

        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        // files: xunitfile, exeption was thrown
        verifier.assertFilePresent( new File( testDir, "src/main/java/org/test/CustomerImpl.java" ).getAbsolutePath() );
        verifier.assertFilePresent( new File( testDir, "target/robotframework-reports/TEST-robot-fail.xml" ).getAbsolutePath() );
        verifier.assertFilePresent( new File( testDir,
                                              "target/generated-sources/fornax-workflow/org/test/AbstractCustomer.java" ).getAbsolutePath() );

    }

}
