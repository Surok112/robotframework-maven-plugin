package com.googlecode.util;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

public class CompositeAndWildCardFilenameFilter implements IOFileFilter
{
  Collection<WildcardFileFilter> filters;

  public CompositeAndWildCardFilenameFilter(Collection<String> wildcards)
  {
    filters = new HashSet<WildcardFileFilter>();

    for (String wildcard : wildcards)
    {
      filters.add(new WildcardFileFilter(wildcard));
    }
  }

  public boolean accept(File file)
  {
    for (WildcardFileFilter filter : filters)
    {
      if (!filter.accept(file))
      {
        return false;
      }
    }

    return true;
  }


  public boolean accept(File file, String name)
  {
    for (WildcardFileFilter filter : filters)
    {
      if (!filter.accept(file, name))
      {
        return false;
      }
    }

    return true;
  }

}
