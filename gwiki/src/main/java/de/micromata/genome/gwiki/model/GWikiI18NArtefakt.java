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

package de.micromata.genome.gwiki.model;

import java.util.Map;
import java.util.Properties;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiEditableArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiTextPageEditorArtefakt;
import de.micromata.genome.gwiki.utils.PropUtils;

/**
 * Artefakt holding I18N-Properties.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiI18NArtefakt extends GWikiTextArtefaktBase<GWikiI18nMap> implements GWikiEditableArtefakt
{

  private static final long serialVersionUID = 1333234153013724487L;

  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    return false;
  }

  public String getFileSuffix()
  {
    return ".properties";
  }

  public GWikiEditorArtefakt< ? > getEditor(GWikiElement elementToEdit, GWikiEditPageActionBean bean, String partKey)
  {
    return new GWikiTextPageEditorArtefakt(elementToEdit, bean, partKey, this);
  }

  @SuppressWarnings({ "unchecked", "rawtypes"})
  @Override
  public GWikiI18nMap getCompiledObject()
  {
    if (super.getCompiledObject() != null) {
      return super.getCompiledObject();
    }
    String data = getStorageData();
    Properties props = PropUtils.toProperties(data);
    GWikiI18nMap nm = new GWikiI18nMap();
    nm.putAll((Map) props);
    setCompiledObject(nm);
    return nm;
  }

}
