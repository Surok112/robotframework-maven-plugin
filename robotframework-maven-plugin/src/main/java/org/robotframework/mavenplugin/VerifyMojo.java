package org.robotframework.mavenplugin;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @goal verify
 * @phase verify
 * @requiresDependencyResolution test
 */
public class VerifyMojo
    extends AbstractMojoWithLoadedClasspath
{

    /**
     * The directory where the test cases are located.
     *
     * @parameter default-value="${project.basedir}/src/test/resources/robotframework/acceptance"
     */
    private File testCasesDirectory;

    /**
     * Sets the path to the generated XUnit compatible result file, relative to outputDirectory. The file is in xml
     * format. By default, the file name is derived from the testCasesDirectory parameter, replacing blanks in the
     * directory name by underscores.
     *
     * @parameter
     */
    private File xunitFile;

    /**
     * Configures where generated reports are to be placed.
     *
     * @parameter default-value="${project.build.directory}/robotframework-reports"
     */
    private File outputDirectory;

    /**
     * Log failures without failing the build.
     *
     * @parameter default-value="false"
     */
    private boolean isTestFailureIgnore;

    /** {@inheritDoc} */
    @Override
    protected void subclassExecute()
        throws MojoExecutionException, MojoFailureException
    {
        // if ( output == null )
        // {
        // output = new File( outputDirectory, OUTPUT_FILE );
        // }

        if ( xunitFile == null )
        {
            String testCasesFolderName = testCasesDirectory.getName();
            xunitFile = new File( "TEST-" + testCasesFolderName.replace( ' ', '_' ) + ".xml" );
        }

        getLog().debug( "Output directory is " + outputDirectory );

        final int errors;
        final int failures;

        try
        {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xunitResult = documentBuilder.parse( makeAbsolute( outputDirectory, xunitFile ) );

            errors = readIntAttribute( xunitResult, "/testsuite/@errors" );
            failures = readIntAttribute( xunitResult, "/testsuite/@failures" );
            int tests = readIntAttribute( xunitResult, "/testsuite/@tests" );

            System.out.println( "\nTest Results :" );

            if ( failures > 0 || errors > 0 )
            {

                System.out.println("\nFailing acceptance tests:\n");
                NodeList testCases = getFailuresAndErrors( xunitResult );
                for ( int i = 0; i < testCases.getLength(); i++ )
                {
                    Node testCase = testCases.item( i );
                    NamedNodeMap attributes = testCase.getAttributes();
                    System.out.println( "    "
                        + attributes.getNamedItem( "name").getNodeValue() + "(" + attributes.getNamedItem( "classname" ).getNodeValue() + ")" ) ;
                }
            }

            System.out.println( "\nTests run: " + tests + ", Failures: " + failures + ", Errors: " + errors + "\n" );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "failed to verify robotframework acceptance-test results", e );
        }
        if ( errors > 0 || failures > 0 )
        {
            String msg =
                "There are acceptance test failures.\n\nPlease refer to " + outputDirectory.getAbsolutePath()
                    + " for the individual test results.";

            if ( isTestFailureIgnore )
            {
                getLog().error( msg );
            }
            else
            {
                throw new MojoFailureException( msg );
            }
        }

    }

    private NodeList getFailuresAndErrors( Document xunitResult )
        throws XPathExpressionException
    {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String xPathExpression = "/testsuite/testcase[failure or error]";
        NodeList testCases = (NodeList) xPath.evaluate( xPathExpression, xunitResult, XPathConstants.NODESET );
        return testCases;
    }

    private int readIntAttribute( Document xunitResult, String xPathExpression )
        throws XPathExpressionException
    {
        XPath xPath = XPathFactory.newInstance().newXPath();
        return ( (Number) xPath.evaluate( xPathExpression, xunitResult, XPathConstants.NUMBER ) ).intValue();
    }

}
