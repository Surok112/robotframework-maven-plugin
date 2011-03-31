package com.googlecode.util;

import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.util.Collection;

public class CompositeWildCardFilenameFilter implements IOFileFilter
{
  private CompositeAndWildCardFilenameFilter compositeAndWildCardFilenameFilter;
  private CompositeOrWildCardFilenameFilter compositeOrWildCardFilenameFilter;

  public CompositeWildCardFilenameFilter(Collection<String> includes, Collection<String> excludes)
  {
    if (isNotEmptyCollection(includes))
    {
      compositeOrWildCardFilenameFilter = new CompositeOrWildCardFilenameFilter(includes);
    }

    if (isNotEmptyCollection(excludes))
    {
      compositeAndWildCardFilenameFilter = new CompositeAndWildCardFilenameFilter(excludes);
    }

  }

  public boolean accept(File file)
  {
    if (compositeAndWildCardFilenameFilter != null)
    {
      if (compositeAndWildCardFilenameFilter.accept(file))
      {
        return false;
      }
    }

    if (compositeOrWildCardFilenameFilter != null)
    {
      return compositeOrWildCardFilenameFilter.accept(file);
    }

    return true;
  }

  public boolean accept(File file, String name)
  {
    if (compositeAndWildCardFilenameFilter != null)
    {
      if (compositeAndWildCardFilenameFilter.accept(file, name))
      {
        return false;
      }
    }

    if (compositeOrWildCardFilenameFilter != null)
    {
      return compositeOrWildCardFilenameFilter.accept(file, name);
    }

    return true;
  }

  private boolean isNotEmptyCollection(Collection<String> includes)
  {
    return includes != null && includes.size() > 0;
  }
}
