package com.googlecode.util;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class FileUtilTest extends TestCase
{
    public void testShouldBeEmpty() throws IOException
    {
      Collection<String> subdir = FileUtil.getAllValidTestFiles(new File("nada"));
      assertEquals(0, subdir.size());

      File file = new File(new File("").getCanonicalPath() + "/src/test/resources/files/dir2");
      subdir = FileUtil.getAllValidTestFiles(file);

      assertEquals(0, subdir.size());
    }

    public void testShouldListAll() throws IOException
    {

      File file = new File(new File("").getCanonicalPath() + "/src/test/resources/files/");
      Collection<String> subdir  = FileUtil.getAllValidTestFiles(file);

      assertEquals(3, subdir.size());
    }

}
