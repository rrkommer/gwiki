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
