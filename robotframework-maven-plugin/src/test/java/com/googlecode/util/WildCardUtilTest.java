package com.googlecode.util;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class WildCardUtilTest extends TestCase
{

  public void testShouldReturnAllResources() throws IOException
  {
    File file = new File(new File(".").getCanonicalPath() + "/src/test/resources/files");
    Collection<File> files = WildCardUtil.listFilesThatIncludes(file, new ArrayList<String>() {
      {
        add("*esources*.*");
      }
    });

    assertEquals(2, files.size());
  }

  public void testShouldNotReturnTestFiles() throws IOException
  {
    File file = new File(new File(".").getCanonicalPath() + "/src/test/resources/files");
    Collection<File> files = WildCardUtil.listFiltesThatExcludes(file, new ArrayList<String>() {
      {
        add("*est*.txt");
      }
    });
    assertEquals(2, files.size());
  }

  public void testShouldReturnAllResourcesAndJava() throws IOException
  {
    File file = new File(new File(".").getCanonicalPath() + "/src/test/resources/files");
    Collection<File> files = WildCardUtil.listFilesThatIncludes(file, new ArrayList<String>() {
      {
        add("*esources*.*");
        add("*.java");
      }
    });

    assertEquals(3, files.size());
  }
}
