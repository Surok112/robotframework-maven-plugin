package com.googlecode;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

public class LibDocMojoTest extends AbstractMojoTestCase
{

  private String exampleLib = "com.googlecode.ExampleLib.html";

  protected void setUp() throws Exception
  {
    super.setUp();
    deleteDocument();
  }

  public void testShouldSucceed() throws Exception
  {
    File pom = getTestFile("src/test/resources/pom-libdoc.xml");

    LibDocMojo mojo = (LibDocMojo) lookupMojo("libdoc", pom);

    mojo.execute();


    assertTrue(new File(exampleLib).exists());

  }

  private void deleteDocument() throws Exception
  {
    File document = new File(exampleLib);

    if(document.exists())
    {
      boolean deleted = document.delete();
      if(!deleted)
      {
        throw new Exception("Cannot delete existing document.");
      }
    }
  }

  protected void tearDown() throws Exception
  {
    super.tearDown();
    deleteDocument();
  }
}
