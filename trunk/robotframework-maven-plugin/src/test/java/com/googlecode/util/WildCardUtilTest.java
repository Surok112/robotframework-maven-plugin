package com.googlecode.util;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class WildCardUtilTest extends TestCase
{

  public void testShouldReturnAllResources() throws IOException
  {
    WildCardUtil wildCardUtil = new WildCardUtil(new ArrayList<String>()
    {
      {
        add("*esources*.*");
      }
    }, Collections.<String>emptyList());


    File file = new File(new File(".").getCanonicalPath() + "/src/test/resources/files");


    Collection<File> files = wildCardUtil.listFiles(file);

    assertEquals(2, files.size());
  }

  public void testShouldNotReturnTestFiles() throws IOException
  {
    WildCardUtil wildCardUtil = new WildCardUtil(Collections.<String>emptyList(), new ArrayList<String>()
    {
      {
        add("*est*.txt");
      }
    });


    File file = new File(new File(".").getCanonicalPath() + "/src/test/resources/files");

    Collection<File> files = wildCardUtil.listFiles(file);

    assertEquals(3, files.size());
  }

  public void testShouldReturnAllResourcesAndJava() throws IOException
  {
    WildCardUtil wildCardUtil = new WildCardUtil(new ArrayList<String>()
    {
      {
        add("*esources*.*");
        add("*.java");
      }
    }, Collections.<String>emptyList());


    File file = new File(new File(".").getCanonicalPath() + "/src/test/resources/files");


    Collection<File> files = wildCardUtil.listFiles(file);

    assertEquals(3, files.size());
  }

  public void testShouldReturnAllRealTests() throws IOException
  {
    WildCardUtil wildCardUtil = new WildCardUtil(new ArrayList<String>()
    {
      {
        add("*est*.*");

      }
    }, new ArrayList<String>()
    {
      {
        add("*.java");
      }
    });


    File file = new File(new File(".").getCanonicalPath() + "/src/test/resources/files");

    Collection<File> files = wildCardUtil.listFiles(file);


    assertEquals(2, files.size());
  }
}
