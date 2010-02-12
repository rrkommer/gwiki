/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   25.10.2009
// Copyright Micromata 25.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.io.Serializable;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.page.impl.GWikiEditableArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiTextPageEditorArtefakt;
import de.micromata.genome.util.types.Converter;

/**
 * Artefakt for a spring xml configuration file.
 * 
 * @author roger
 * 
 * @param <T>
 */
public class GWikiXmlConfigArtefakt<T extends Serializable> extends GWikiTextArtefaktBase<T> implements GWikiEditableArtefakt
{

  private static final long serialVersionUID = -6207464844814860485L;

  private BeanFactory beanFactory = null;

  public String getFileSuffix()
  {
    return ".xml";
  }

  @SuppressWarnings("unchecked")
  @Override
  public T getCompiledObject()
  {
    if (beanFactory == null) {
      String sdata = getStorageData();
      Resource resource = new ByteArrayResource(Converter.bytesFromString(sdata));

      XmlBeanFactory bf = new XmlBeanFactory(resource);
      // ist nicht gegen aeltere spring-version kompatibel
      // bf.setBeanClassLoader(Thread.currentThread().getContextClassLoader());
      beanFactory = bf;
    }
    T bean = (T) beanFactory.getBean("config");
    return bean;
  }

  @SuppressWarnings("unchecked")
  public GWikiEditorArtefakt getEditor(GWikiElement elementToEdit, GWikiEditPageActionBean bean, String partKey)
  {
    return new GWikiTextPageEditorArtefakt(elementToEdit, bean, partKey, this);
  }

}
