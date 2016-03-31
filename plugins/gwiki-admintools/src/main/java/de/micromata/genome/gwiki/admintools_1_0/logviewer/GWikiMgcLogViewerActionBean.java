package de.micromata.genome.gwiki.admintools_1_0.logviewer;

import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.logging.loghtmlwindow.LogHtmlWindowServlet;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiMgcLogViewerActionBean extends ActionBeanBase
{

  @Override
  public Object onInit()
  {

    return super.onInit();
  }

  public String getJavaScript()
  {
    return LogHtmlWindowServlet.getJsContent();
  }

  public String getStandardLogForm()
  {
    return LogHtmlWindowServlet.getGLogHtmlForm();
  }

  public String getCssContent()
  {
    return LogHtmlWindowServlet.getCssContent();
  }
}
