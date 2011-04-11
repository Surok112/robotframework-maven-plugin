package com.googlecode;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

public class RobotFrameworkMojoTest
    extends AbstractMojoTestCase
{

    public void setup()
    {
    }

    public void testShouldSucceed()
        throws Exception
    {
        File pom = getTestFile( "src/test/resources/pom-success.xml" );
        RobotFrameworkMojo mojo = (RobotFrameworkMojo) lookupMojo( "run", pom );
        mojo.execute();
    }

    public void testShouldBeSkipped()
        throws Exception
    {
        File pom = getTestFile( "src/test/resources/pom-skip.xml" );
        RobotFrameworkMojo mojo = (RobotFrameworkMojo) lookupMojo( "run", pom );
        mojo.execute();
    }

    public void testShouldFail()
        throws Exception
    {
        File pom = getTestFile( "src/test/resources/pom-fail.xml" );
        RobotFrameworkMojo mojo = (RobotFrameworkMojo) lookupMojo( "run", pom );
        try
        {
            mojo.execute();
            fail( "robot tests should have failed" );
        }
        catch ( MojoFailureException e )
        {
            // expected
        }
    }

    public void testHsqlShouldPass()
        throws Exception
    {
        File pom = getTestFile( "src/test/resources/pom-hsqldb.xml" );
        RobotFrameworkMojo mojo = (RobotFrameworkMojo) lookupMojo( "run", pom );
        mojo.execute();
    }

    public void testShouldHaveErrors()
        throws Exception
    {

        File pom = getTestFile( "src/test/resources/pom-error.xml" );
        RobotFrameworkMojo mojo = (RobotFrameworkMojo) lookupMojo( "run", pom );
        try
        {
            mojo.execute();
            fail( "robot tests should have errors" );
        }
        catch ( MojoExecutionException e )
        {
            // expected
        }

    }

}
