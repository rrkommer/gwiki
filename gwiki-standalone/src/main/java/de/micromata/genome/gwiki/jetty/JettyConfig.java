////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

package de.micromata.genome.gwiki.jetty;

import java.util.List;

import org.eclipse.jetty.server.Connector;

public class JettyConfig
{
  private int port = 8081;

  private String contextRoot;

  /**
   * Context path with leading, but no ending /.
   */
  private String contextPath = "/";

  /**
   * add / after path.
   * 
   * Sample / or /gwiki/
   * 
   */
  private String servletPath = "/";

  /**
   * Session timeout in seconds.
   */
  private int sessionTimeout = 60 * 60;

  // for later use init https.
  private List<Connector> connectors;

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

  public String getContextPath()
  {
    return contextPath;
  }

  public void setContextPath(String contextPath)
  {
    this.contextPath = contextPath;
  }

  public String getServletPath()
  {
    return servletPath;
  }

  public void setServletPath(String servletPath)
  {
    this.servletPath = servletPath;
  }

  public int getSessionTimeout()
  {
    return sessionTimeout;
  }

  public void setSessionTimeout(int sessionTimeout)
  {
    this.sessionTimeout = sessionTimeout;
  }

  public List<Connector> getConnectors()
  {
    return connectors;
  }

  public void setConnectors(List<Connector> connectors)
  {
    this.connectors = connectors;
  }

}
