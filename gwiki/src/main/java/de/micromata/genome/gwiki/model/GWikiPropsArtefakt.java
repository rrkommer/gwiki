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

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiEditableArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiPropsDescriptor;
import de.micromata.genome.gwiki.page.impl.GWikiPropsEditorArtefakt;

/**
 * Artefakt hold a GWikiProps.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPropsArtefakt extends GWikiPersistArtefaktBase<GWikiProps> implements GWikiEditableArtefakt
{
  private static final long serialVersionUID = -7444414246579867245L;

  private GWikiPropsDescriptor propDescriptor = null;

  public GWikiPropsArtefakt()
  {
    this(new GWikiProps());
  }

  public GWikiPropsArtefakt(GWikiProps props)
  {
    setCompiledObject(props);
  }

  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    ctx.append("Properties cannot be viewed");
    return true;
  }

  public String getFileSuffix()
  {
    return ".properties";
  }

  @SuppressWarnings("unchecked")
  public GWikiEditorArtefakt getEditor(GWikiElement elementToEdit, GWikiEditPageActionBean bean, String partKey)
  {
    return new GWikiPropsEditorArtefakt(elementToEdit, bean, partKey, this, propDescriptor);
  }

  public Map<String, String> getStorageData()
  {
    return getCompiledObject().getMap();
  }

  public void setStorageData(Map<String, String> map)
  {
    setCompiledObject(new GWikiProps(map));
  }

  public GWikiPropsDescriptor getPropDescriptor()
  {
    return propDescriptor;
  }

  public void setPropDescriptor(GWikiPropsDescriptor propDescriptor)
  {
    this.propDescriptor = propDescriptor;
  }

}
