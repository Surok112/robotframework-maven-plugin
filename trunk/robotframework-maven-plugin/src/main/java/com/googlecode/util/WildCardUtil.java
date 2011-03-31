package com.googlecode.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.Collection;

public class WildCardUtil
{

  public static Collection<File> listFilesThatIncludes(File file, Collection<String> wildcards)
  {
    CompositeOrWildCardFilenameFilter filter = new CompositeOrWildCardFilenameFilter(wildcards);
    return FileUtils.listFiles(file, filter, TrueFileFilter.INSTANCE);
  }

  public static Collection<File> listFiltesThatExcludes(File file, Collection<String> wildcards)
  {
    CompositeAndWildCardFilenameFilter filter = new CompositeAndWildCardFilenameFilter(wildcards);
    return FileUtils.listFiles(file, filter, TrueFileFilter.INSTANCE);
  }
}
