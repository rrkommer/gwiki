/////////////////////////////////////////////////////////////////////////////
//
// Project   genome-gwiki-standalone
//
// Author    roger@micromata.de
// Created   06.11.2009
// Copyright Micromata 06.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.jetty;

public class JettyConfig
{
  private int port = 8081;

  private String contextRoot;

  public int getPort()
  {
    return port;
  }

  public void setPort(int port)
  {
    this.port = port;
  }

  public String getContextRoot()
  {
    return contextRoot;
  }

  public void setContextRoot(String contextRoot)
  {
    this.contextRoot = contextRoot;
  }

}
