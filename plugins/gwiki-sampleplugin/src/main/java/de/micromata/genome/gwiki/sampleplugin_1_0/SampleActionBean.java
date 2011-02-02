package de.micromata.genome.gwiki.sampleplugin_1_0;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * Sample ActionBean.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SampleActionBean extends ActionBeanBase
{
  private String message;

  private String name;

  public Object onInit()
  {
    return null;
  }

  public Object onSayHello()
  {
    if (StringUtils.isBlank(name) == true) {
      wikiContext.addSimpleValidationError("name is empty");
      return null;
    }
    message = "Hello to " + name;
    return null;
  }

  public String getMessage()
  {
    return message;
  }

  public void setMessage(String message)
  {
    this.message = message;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }
}
