package com.googlecode.util;

import org.codehaus.plexus.components.io.resources.PlexusIoFileResource;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResourceCollection;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

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
        @SuppressWarnings( "unchecked" ) Iterator<PlexusIoFileResource> resources = collection.getResources();
        return resources;

    }

}
