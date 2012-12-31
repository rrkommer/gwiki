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

package de.micromata.genome.gwiki.page.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiArtefaktBase;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBean;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanUtils;
import de.micromata.genome.gwiki.utils.ClassUtils;

/**
 * Base class to implement a ActionBean as GWikiArtefakt.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiActionBeanArtefakt extends GWikiArtefaktBase<Class< ? extends ActionBean>> implements
    GWikiExecutableArtefakt<Class< ? extends ActionBean>>
{

  private static final long serialVersionUID = -3934990772359212231L;

  private String beanClassName;

  private Class< ? extends ActionBean> beanClass;

  private GWikiExecutableArtefakt< ? > forward;

  @Override
  public void collectParts(Map<String, GWikiArtefakt< ? >> map)
  {
    super.collectParts(map);
    if (forward != null) {
      map.put("Template", forward);
      forward.collectParts(map);
    }
  }

  public void prepareHeader(GWikiContext wikiContext)
  {

  }

  @SuppressWarnings("unchecked")
  protected Class< ? extends ActionBean> getActionBeanClass(GWikiContext ctx)
  {
    if (beanClass != null) {
      return beanClass;
    }
    if (StringUtils.isBlank(beanClassName) == true) {
      beanClassName = ctx.getCurrentElement().getElementInfo().getProps().getStringValue(GWikiPropKeys.WIKICONTROLERCLASS);

    }
    if (StringUtils.isBlank(beanClassName) == true) {
      return null;
      // throw new RuntimeException("No bean class defined");
    }
    try {
      beanClass = (Class< ? extends ActionBean>) ClassUtils.classForName(beanClassName);
      return beanClass;
    } catch (Throwable ex) {
      throw new RuntimeException("Failed to create ActionBean: " + beanClassName + ": " + ex.getMessage(), ex);
    }
  }

  public ActionBean getActionBean(GWikiContext ctx)
  {
    try {
      Class< ? extends ActionBean> cls = getActionBeanClass(ctx);
      if (cls == null)
        return null;
      return cls.newInstance();
    } catch (Throwable ex) {
      throw new RuntimeException("Failed to intializable ActionBean: " + getActionBeanClass(ctx).getName() + ": " + ex.getMessage(), ex);
    }
  }

  public boolean renderWithParts(GWikiContext ctx)
  {
    ActionBean bean = getActionBean(ctx);
    if (bean != null) {
      bean.setWikiContext(ctx);
      if (ActionBeanUtils.perform(bean) == false) {
        return false;
      }
    }
    if (forward != null) {
      return forward.render(ctx);
    }
    return true;
  }

  public Class< ? extends ActionBean> getBeanClass()
  {
    return beanClass;
  }

  public void setBeanClass(Class< ? extends ActionBean> beanClass)
  {
    this.beanClass = beanClass;
  }

  public String getBeanClassName()
  {
    return beanClassName;
  }

  public void setBeanClassName(String beanClassName)
  {
    this.beanClassName = beanClassName;
  }

  public GWikiExecutableArtefakt< ? > getForward()
  {
    return forward;
  }

  public void setForward(GWikiExecutableArtefakt< ? > forward)
  {
    this.forward = forward;
  }

}
