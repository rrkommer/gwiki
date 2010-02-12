/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   25.10.2009
// Copyright Micromata 25.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiTextArtefaktBase;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanUtils;
import de.micromata.genome.gwiki.utils.ClassUtils;

/**
 * An artefakt implementing a ActionBean via groovy script.
 * 
 * @author roger
 * 
 */
public class GWikiScriptControlerArtefakt extends GWikiTextArtefaktBase<Class< ? extends ActionBeanBase>> implements
    GWikiExecutableArtefakt<Class< ? extends ActionBeanBase>>, GWikiEditableArtefakt
{

  private static final long serialVersionUID = 1185666527076725648L;

  private Class< ? extends ActionBeanBase> beanClass;

  public String getFileSuffix()
  {
    return ".groovy";
  }

  protected boolean inputForward(GWikiContext ctx)
  {
    GWikiExecutableArtefakt< ? > forward = (GWikiExecutableArtefakt< ? >) getParts().get("InputForward");
    if (forward == null) {
      return true;
    }
    return forward.render(ctx);
  }

  public boolean renderWithParts(GWikiContext ctx)
  {
    ActionBeanBase bean = getActionBean(ctx);
    if (bean == null) {
      bean = getActionBeanFromSettings(ctx);
      if (bean == null) {
        return inputForward(ctx);
      }
    }
    bean.setWikiContext(ctx);
    if (ActionBeanUtils.perform(bean) == true) {
      return inputForward(ctx);
    } else {
      return false;
    }
  }

  @SuppressWarnings("unchecked")
  protected Class< ? extends ActionBeanBase> getActionBeanClassFromSettings(GWikiContext ctx)
  {
    if (beanClass != null) {
      return beanClass;
    }

    String beanClassName = ctx.getWikiElement().getElementInfo().getProps().getStringValue(GWikiPropKeys.WIKICONTROLERCLASS);
    if (StringUtils.isBlank(beanClassName) == true) {
      return null;
    }
    try {
      beanClass = (Class< ? extends ActionBeanBase>) Class.forName(beanClassName);
      return beanClass;
    } catch (Throwable ex) {
      throw new RuntimeException("Failed to create ActionBean: " + beanClassName + ": " + ex.getMessage(), ex);
    }
  }

  protected ActionBeanBase getActionBeanFromSettings(GWikiContext ctx)
  {
    Class< ? extends ActionBeanBase> cls = getActionBeanClassFromSettings(ctx);
    if (cls == null) {
      return null;
    }
    return ClassUtils.createDefaultInstance(cls);
  }

  protected ActionBeanBase getActionBean(GWikiContext ctx)
  {
    Class< ? extends ActionBeanBase> beanClass = getActionBeanClass(ctx);
    if (beanClass == null) {

      return null;

    }
    try {
      GroovyObject groovyObject = (GroovyObject) getCompiledObject().newInstance();
      ActionBeanBase abean = (ActionBeanBase) groovyObject;
      return abean;
    } catch (Throwable ex) {
      throw new RuntimeException("Cannot Compile ActionBean groovy", ex);
    }
  }

  @SuppressWarnings("unchecked")
  public Class< ? extends ActionBeanBase> getActionBeanClass(GWikiContext ctx)
  {
    if (getCompiledObject() != null)
      return getCompiledObject();
    if (StringUtils.isBlank(getStorageData()) == true) {
      return null;
    }
    GroovyClassLoader loader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
    setCompiledObject(loader.parseClass(getStorageData(), "Controler.groovy"));
    return getCompiledObject();
  }

  public GWikiEditorArtefakt< ? > getEditor(GWikiElement elementToEdit, GWikiEditPageActionBean bean, String partKey)
  {
    return new GWikiScriptControlerEditorArtefakt(elementToEdit, bean, partKey, this);
  }

}
