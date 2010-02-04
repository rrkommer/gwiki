package de.micromata.genome.test.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.stripes.mock.MockServletContext;

public class SimServletContext extends MockServletContext
{
  private String webDir;

  private File webDirFile;

  public SimServletContext(String contextName, String webDir)
  {
    super(contextName);
    this.webDir = webDir;
    this.webDirFile = new File(webDir);
    if (webDirFile.exists() == false) {
      throw new RuntimeException("SimServletContext; local web directory does not exists");
    }
  }

  public String getWebDir()
  {
    return webDir;
  }

  public void setWebDir(String webDir)
  {
    this.webDir = webDir;
  }

  @Override
  public InputStream getResourceAsStream(String name)
  {
    File f = new File(webDirFile, name);
    if (f.exists() == false) {
      return super.getResourceAsStream(name);
    }
    try {
      return new FileInputStream(f);
    } catch (IOException ex) {
      return super.getResourceAsStream(name);
    }
  }
}
