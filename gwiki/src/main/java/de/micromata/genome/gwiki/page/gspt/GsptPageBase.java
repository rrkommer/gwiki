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

package de.micromata.genome.gwiki.page.gspt;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.jsp.PageContext;

/**
 * Used to be instantiated as groovy class
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GsptPageBase extends HttpServlet
{

  private static final long serialVersionUID = -4243700848626967736L;

  private ServletConfig servletConfig;

  public GsptPageBase()
  {
  }

  public void service(PageContext pageContext)
  {
    _gsptService(pageContext);
  }

  public abstract void _gsptService(PageContext pageContext);

  public ServletConfig getServletConfig()
  {
    return servletConfig;
  }

  public void setServletConfig(ServletConfig servletConfig)
  {
    this.servletConfig = servletConfig;
  }
}
