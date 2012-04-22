package com.googlecode;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

public class LibDocMojoTestsAreBroken
    extends AbstractMojoTestCase
{

    private final String htmlResourceLibDoc = "target/robotframework/html_resource.html";

    private final String javalibLibDoc = "target/robotframework/com.googlecode.ExampleLib.html";

    private final String mylibLibDoc = "target/robotframework/mylib.html";

    private final String mypackageMylibLibDoc = "target/robotframework/mypackage.mylib.html";

    protected void setUp()
        throws Exception
    {
        super.setUp();
        deleteDocument( htmlResourceLibDoc );
        deleteDocument( javalibLibDoc );
        deleteDocument( mylibLibDoc );
        deleteDocument( mypackageMylibLibDoc );
    }

    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }

    public void testLibDocForJavaResource()
        throws Exception
    {
        File pom = getTestFile( "src/test/resources/pom-libdoc.xml" );

        LibDocMojo mojo = (LibDocMojo) lookupMojo( "libdoc", pom );

        mojo.execute();

        assertTrue( javalibLibDoc + " not found", new File( javalibLibDoc ).exists() );

    }

    public void testLibDocForTxtResource()
        throws Exception
    {
        File pom = getTestFile( "src/test/resources/pom-libdoc-txtfile.xml" );

        LibDocMojo mojo = (LibDocMojo) lookupMojo( "libdoc", pom );

        mojo.execute();

        assertTrue( htmlResourceLibDoc + " not found", new File( htmlResourceLibDoc ).exists() );

    }

    public void testLibDocForFolderWithIncludes()
        throws Exception
    {
        File pom = getTestFile( "src/test/resources/pom-libdoc-folder-with-include.xml" );

        LibDocMojo mojo = (LibDocMojo) lookupMojo( "libdoc", pom );

        mojo.execute();

        assertTrue( htmlResourceLibDoc + " not found", new File( htmlResourceLibDoc ).exists() );

    }

    public void testLibDocForFolderOnly()
        throws Exception
    {
        File pom = getTestFile( "src/test/resources/pom-libdoc-folder-only.xml" );

        LibDocMojo mojo = (LibDocMojo) lookupMojo( "libdoc", pom );

        mojo.execute();

        assertTrue( htmlResourceLibDoc + " not found", new File( htmlResourceLibDoc ).exists() );

    }

    public void testLibDocForLibraryNamePython()
        throws Exception
    {
        File pom = getTestFile( "src/test/resources/pom-libdoc-libraryname-python.xml" );

        LibDocMojo mojo = (LibDocMojo) lookupMojo( "libdoc", pom );

        mojo.execute();

        assertTrue( mylibLibDoc + " not found", new File( mylibLibDoc ).exists() );

    }

    public void testLibDocForLibraryNamePythonWithPackage()
    throws Exception
{
    File pom = getTestFile( "src/test/resources/pom-libdoc-libraryname-python-subpackage.xml" );

    LibDocMojo mojo = (LibDocMojo) lookupMojo( "libdoc", pom );

    mojo.execute();

    assertTrue( mypackageMylibLibDoc + " not found", new File( mypackageMylibLibDoc ).exists() );

}

    public void testLibDocForLibraryNameJava()
        throws Exception
    {
        File pom = getTestFile( "src/test/resources/pom-libdoc-libraryname-java.xml" );

        LibDocMojo mojo = (LibDocMojo) lookupMojo( "libdoc", pom );

        mojo.execute();

        assertTrue( javalibLibDoc + " not found", new File( javalibLibDoc ).exists() );

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
