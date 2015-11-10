////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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
 * @author Roger Rene Kommer (r.kommer@micromata.de)
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
    @SuppressWarnings("unused")
    T comp = super.getCompiledObject();
    // No store of compiled object, because some needs NO singleton!
    // if (comp != null) {
    // return comp;
    // }
    if (beanFactory == null) {
      String sdata = getStorageData();
      Resource resource = new ByteArrayResource(Converter.bytesFromString(sdata));

      XmlBeanFactory bf = new XmlBeanFactory(resource);
      // ist nicht gegen aeltere spring-version kompatibel
      // bf.setBeanClassLoader(Thread.currentThread().getContextClassLoader());
      beanFactory = bf;
    }
    T bean = (T) beanFactory.getBean("config");
    // NO see above: setCompiledObject(bean);
    return bean;
  }

  public GWikiEditorArtefakt< ? > getEditor(GWikiElement elementToEdit, GWikiEditPageActionBean bean, String partKey)
  {
    return new GWikiTextPageEditorArtefakt(elementToEdit, bean, partKey, this);
  }

}
