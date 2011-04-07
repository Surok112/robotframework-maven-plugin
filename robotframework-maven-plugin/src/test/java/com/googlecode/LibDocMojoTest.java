package com.googlecode;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class LibDocMojoTest
    extends AbstractMojoTestCase
{

    private final String htmlResourceLibDoc = "target/robot/html_resource.html";

    private final String javaResourceLibDoc = "target/robot/com.googlecode.ExampleLib.html";

    
    protected void setUp()
        throws Exception
    {
        super.setUp();
        deleteDocument( htmlResourceLibDoc );
        deleteDocument( javaResourceLibDoc );
    }

    protected void tearDown()
        throws Exception
    {
        super.tearDown();
        deleteDocument( htmlResourceLibDoc );
        deleteDocument( javaResourceLibDoc );
    }

    public void testLibDocForJavaResource()
        throws Exception
    {
        File pom = getTestFile( "src/test/resources/pom-libdoc.xml" );

        LibDocMojo mojo = (LibDocMojo) lookupMojo( "libdoc", pom );

        mojo.execute();

        assertTrue( new File( javaResourceLibDoc ).exists() );

    }

    public void testLibDocForTxtResource()
        throws Exception
    {
        File pom = getTestFile( "src/test/resources/pom-libdoc-txtfile.xml" );

        LibDocMojo mojo = (LibDocMojo) lookupMojo( "libdoc", pom );

        mojo.execute();

        assertTrue( new File( htmlResourceLibDoc ).exists() );

    }

    public void testLibDocForFolder()
        throws Exception
    {
        File pom = getTestFile( "src/test/resources/pom-libdoc-folder.xml" );

        LibDocMojo mojo = (LibDocMojo) lookupMojo( "libdoc", pom );

        mojo.execute();

        assertTrue( new File( htmlResourceLibDoc ).exists() );

    }

    private void deleteDocument( String libDoc )
        throws Exception
    {
        File document = new File( libDoc );

        if ( document.exists() )
        {
            boolean deleted = document.delete();
            if ( !deleted )
            {
                throw new Exception( "Cannot delete existing document." );
            }
        }
    }

}
