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
 * @author roger
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
