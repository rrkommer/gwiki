/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    jens@micromata.de
// Created   19.08.2008
// Copyright Micromata 19.08.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import groovy.lang.Binding;
import groovy.lang.MissingPropertyException;

import java.util.Map;

import javax.servlet.jsp.el.VariableResolver;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

/**
 * Über diese Klasse fasst der JSP-PageContext den Groovy-Context an.
 * 
 * @author jens@micromata.de
 */
public class BindingAccessor implements VariableResolver
{
  // TODO pop logging verwenden
  private static final Logger log = Logger.getLogger(BindingAccessor.class);

  private final Binding binding;

  /**
   * @param binding groovy-binding
   */
  public BindingAccessor(final Binding binding)
  {
    this.binding = binding;
  }

  public Binding getBinding()
  {
    return binding;
  }

  public Object resolveVariable(String key)
  {
    try {
      return binding.getVariable(key);
    } catch (MissingPropertyException e) {
      return null;
    }
  }

  public boolean isVariableDefined(String key)
  {
    return binding.getVariables().containsKey(key);
  }

  @SuppressWarnings("unchecked")
  public Map< ? super String, ? > getVarMap()
  {
    return binding.getVariables();
  }

  public void setBeanVar(String property, Object value)
  {
    int firstDotPos = property.indexOf('.');
    if (firstDotPos < 1) {
      binding.setVariable(property, value);
      return;
    }
    Object obj = binding.getVariable(property.substring(0, firstDotPos));
    if (obj == null) {
      binding.setVariable(property, value);
      return;
    }
    final String propertyName = property.substring(firstDotPos + 1);
    try {
      BeanUtils.setProperty(obj, propertyName, value);
    } catch (Exception ex) {
      log.warn("Exception encountered while setting '" + propertyName + "' on instance of " + obj.getClass().getName() + ": " + ex, ex);
      throw new RuntimeException(ex);
    }
  }
}
