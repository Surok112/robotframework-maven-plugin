package com.googlecode.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.Collection;

public class WildCardUtil
{
  private CompositeWildCardFilenameFilter filter;

  public WildCardUtil(Collection<String> includes, Collection<String> excludes)
  {
    filter = new CompositeWildCardFilenameFilter(includes, excludes);

  }

  public Collection<File> listFiles(File file)
  {
    return FileUtils.listFiles(file, filter, TrueFileFilter.INSTANCE);
  }

}
