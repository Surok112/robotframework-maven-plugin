package com.googlecode.util;

import junit.framework.TestCase;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WildCardUtilTest
    extends TestCase
{

    private final File baseDir = new File( "src/test/resources/files" );

    private final WildCardUtil wildCardUtil = new WildCardUtil();

    private final String[] excludes = new String[0];

    private final String[] includes = new String[0];

    public void testShouldReturnAllResources()
        throws IOException
    {
        List<String> expectedFiles = Arrays.asList( "resourcesA.txt", "resources.txt" );
        String[] includes = new String[]{ "**/*esources*.*" };

        doTestWildCardUtil( expectedFiles, includes, excludes );
    }

    public void testShouldReturnAllResourcesAndJava()
        throws IOException
    {
        List<String> expectedFiles = Arrays.asList( "resourcesA.txt", "resources.txt", "test.java" );
        String[] includes = new String[]{ "**/*esources*.*", "**/*.java" };

        doTestWildCardUtil( expectedFiles, includes, excludes );
    }

    public void testShouldNotReturnTestFiles()
        throws IOException
    {
        List<String> expectedFiles = Arrays.asList( "resourcesA.txt", "test.java", "resources.txt" );
        String[] excludes = new String[]{ "**/*est*.txt" };

        doTestWildCardUtil( expectedFiles, includes, excludes );
    }

    public void testShouldReturnAllRealTests()
        throws IOException
    {
        List<String> expectedFiles = Arrays.asList( "test.txt", "test2.txt" );
        String[] includes = new String[]{ "**/*est*.*" };
        String[] excludes = new String[]{ "**/*.java" };

        doTestWildCardUtil( expectedFiles, includes, excludes );
    }

    public void testShouldReturnPossibleRobotFiles()
        throws IOException
    {
        List<String> expectedFiles =
            Arrays.asList( "resourcesA.txt", "test.java", "resources.txt", "test2.txt", "test.txt" );

        doTestWildCardUtil( expectedFiles, includes, excludes );
    }

    private void doTestWildCardUtil( List<String> expectedFiles, String[] includes, String[] excludes )
        throws IOException
    {
        Iterator<PlexusIoFileResource> iterator = wildCardUtil.listFiles( baseDir, includes, excludes );

        int found = 0;
        while ( iterator.hasNext() )
        {
            PlexusIoFileResource fileResource = iterator.next();
            found++;
            assertTrue( "unexpected file: " + fileResource.getFile().getName(),
                        expectedFiles.contains( fileResource.getFile().getName() ) );
        }
        assertEquals( expectedFiles.size(), found );
    }
}
