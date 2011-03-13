package com.googlecode;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

public class RobotFrameworkMojoTest extends AbstractMojoTestCase
{

  public void testShouldSucceed() throws Exception
  {
    File pom = getTestFile("src/test/resources/pom-success.xml");

    RobotFrameworkMojo mojo = (RobotFrameworkMojo) lookupMojo("jybot", pom);

    try
    {
      mojo.execute();
    }
    catch (MojoFailureException e)
    {
      assertTrue(false);
      return;
    }

    assertTrue(true);

  }

  public void testShouldFail() throws Exception
  {
    File pom = getTestFile("src/test/resources/pom-fail.xml");

    RobotFrameworkMojo mojo = (RobotFrameworkMojo) lookupMojo("jybot", pom);

    try
    {
      mojo.execute();
    }
    catch (MojoFailureException e)
    {
      assertTrue(true);
      return;
    }

    assertFalse(true);

  }

  public void testShouldHaveErrors() throws Exception
  {
    File pom = getTestFile("src/test/resources/pom-error.xml");

    RobotFrameworkMojo mojo = (RobotFrameworkMojo) lookupMojo("jybot", pom);

    try
    {
      mojo.execute();
    }
    catch (MojoExecutionException e)
    {
      assertTrue(true);
      return;
    }

    assertTrue(false);

  }

  protected void setUp() throws Exception
  {
    super.setUp();
  }

  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
}
