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

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspEngineInfo;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

/**
 * In case the container - like jetty - does not support JspFactory.
 * 
 * Also used for gspt
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class StandaloneJspFactory extends JspFactory
{
  public static final String PAGECONTEXT_KEY = "de.micromata.genome.gspt.pageContext";

  static JspEngineInfo info;

  @Override
  public JspEngineInfo getEngineInfo()
  {
    if (info != null)
      return info;
    info = new JspEngineInfo() {

      @Override
      public String getSpecificationVersion()
      {
        return "2.4";
      }

    };
    return info;
  }

  @Override
  public PageContext getPageContext(Servlet servlet, ServletRequest req, ServletResponse resp, String errorPageURL, boolean needsSession,
      int bufferSize, boolean autoFlush)
  {

    ServletStandalonePageContext pg = new ServletStandalonePageContext();
    try {
      pg.initialize(servlet, req, resp, errorPageURL, needsSession, bufferSize, autoFlush);
    } catch (Exception ex) {
      throw new RuntimeException("Failure to initialize ServletStandalonePageContext: " + ex.getMessage(), ex);
    }
    if (req.getAttribute(PAGECONTEXT_KEY) != null) {
      PageContext ppc = (PageContext) req.getAttribute(PAGECONTEXT_KEY);
      //pg.setWriter(ppc.getOut()); wenn nicht gesetzt, falsche reihenfolge, wenn gesetzt, verschwindet output
    }
    req.setAttribute(PAGECONTEXT_KEY, pg);
    return pg;
  }

  @Override
  public void releasePageContext(PageContext pageContext)
  {
    pageContext.release();

  }

  
  public JspApplicationContext getJspApplicationContext(ServletContext arg0)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
