package com.googlecode.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class FileUtil
{
  public static Collection<String> getAllValidTestFiles(File file)
  {
    if (file == null || !file.isDirectory())
    {
      return Collections.emptyList();
    }

    Collection<File> subdirectories = FileUtils.listFiles(file, new SuffixFileFilter(new String[]
                                                                                         {".xhtml", ".htm", ".html",
                                                                                             ".tsv", ".txt", ".rst",
                                                                                             ".rest"}),
                                                          TrueFileFilter.INSTANCE);

    Collection<String> directoryPaths = new ArrayList<String>();

    for (File subdir : subdirectories)
    {
      directoryPaths.add(subdir.getPath());
    }

    return directoryPaths;

  }
}
