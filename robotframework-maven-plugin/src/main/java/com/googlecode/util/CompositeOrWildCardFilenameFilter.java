package com.googlecode.util;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

public class CompositeOrWildCardFilenameFilter implements IOFileFilter
{
  Collection<WildcardFileFilter> filters;

  public CompositeOrWildCardFilenameFilter(Collection<String> wildCards)
  {
    filters = new HashSet<WildcardFileFilter>();

    for (String wildcard : wildCards)
    {
      filters.add(new WildcardFileFilter(wildcard));
    }
  }

  public boolean accept(File file)
  {
    for (WildcardFileFilter filter : filters)
    {
      if (filter.accept(file))
      {
        return true;
      }
    }

    return false;
  }


  public boolean accept(File file, String name)
  {
    for (WildcardFileFilter filter : filters)
    {
      if (filter.accept(file, name))
      {
        return true;
      }
    }

    return false;
  }

}
