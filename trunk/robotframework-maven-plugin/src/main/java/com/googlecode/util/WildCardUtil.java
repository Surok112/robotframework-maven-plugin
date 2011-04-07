package com.googlecode.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResource;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResourceCollection;

public class WildCardUtil
{

    public Iterator<PlexusIoFileResource> listFiles( File baseDir, String[] includes, String[] excludes )
        throws IOException
    {
        PlexusIoFileResourceCollection collection = new PlexusIoFileResourceCollection();
        collection.setBaseDir( baseDir );
        collection.setIncludes( includes );
        collection.setExcludes( excludes );
        collection.setUsingDefaultExcludes( true );
        collection.setIncludingEmptyDirectories( false );
        @SuppressWarnings( "unchecked" )
        Iterator<PlexusIoFileResource> resources = collection.getResources();
        return resources;

    }

}
