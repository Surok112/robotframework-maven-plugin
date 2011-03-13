package com.googlecode;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMojoWithLoadedClasspath extends AbstractMojo
{

  /**
   * @parameter expression="${project.compileClasspathElements}"
   * @required
   * @readonly
   */
  private List<String> classpathElements;

  public void execute() throws MojoExecutionException, MojoFailureException
  {
    loadClassPath();
    subclassExecute();
  }

  protected abstract void subclassExecute() throws MojoExecutionException, MojoFailureException;

  private void loadClassPath() throws MojoExecutionException
  {
    List<URL> urls = new ArrayList<URL>();

    if (classpathElements != null)
    {
      for (String element : classpathElements)
      {
        File elementFile = new File(element);
        try
        {
          urls.add(elementFile.toURI().toURL());
        }
        catch (MalformedURLException e)
        {
          throw new MojoExecutionException("Classpath loading error: " + element);
        }
      }
    }

    if (urls.size() > 0)
    {
      ClassLoader realm = new URLClassLoader(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
      Thread.currentThread().setContextClassLoader(realm);
    }
  }

  protected void addFileToArguments(List<String> arguments, File file, String flag)
  {
    if (file != null && !file.getPath().isEmpty())
    {
      arguments.add("-" + flag);
      arguments.add(file.getPath());
    }
  }

  protected void addStringToArguments(List<String> arguments, String variableToAdd, String flag)
  {
    if (!StringUtils.isEmpty(variableToAdd))
    {
      arguments.add("-" + flag);
      arguments.add(variableToAdd);
    }
  }

  protected void addListToArguments(List<String> arguments, List<String> variablesToAdd, String flag)
  {
    if (variablesToAdd == null)
    {
      return;
    }

    for (String variableToAdd : variablesToAdd)
    {
      if (!StringUtils.isEmpty(variableToAdd))
      {
        arguments.add("-" + flag);
        arguments.add(variableToAdd);
      }
    }
  }
}
