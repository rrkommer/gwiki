package de.micromata.genome.gwiki.page.gspt;

import groovy.lang.Binding;
import groovy.lang.MissingPropertyException;

import java.util.Map;

import javax.servlet.jsp.PageContext;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class PageContextBinding extends Binding
{
  private PageContext pageContext;

  public PageContextBinding(PageContext pageContext, Map<String, Object> vars)
  {
    super(vars);
    this.pageContext = pageContext;
  }

  @Override
  public Object getProperty(String property)
  {
    return super.getProperty(property);
  }

  @Override
  public Object getVariable(String name)
  {
    try {
      Object val = super.getVariable(name);
      return val;
    } catch (MissingPropertyException e) {
      // noting
    }
    Object val = pageContext.findAttribute(name);
    return val;
  }
}
