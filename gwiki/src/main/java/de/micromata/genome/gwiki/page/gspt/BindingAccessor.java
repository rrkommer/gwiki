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

import groovy.lang.Binding;
import groovy.lang.MissingPropertyException;

import java.util.Map;

import javax.servlet.jsp.el.VariableResolver;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

/**
 * Ueber diese Klasse fasst der JSP-PageContext den Groovy-Context an.
 * 
 * @author jens@micromata.de
 */
@SuppressWarnings("deprecation")
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
