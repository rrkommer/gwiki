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

package de.micromata.genome.gwiki.page.impl;

import java.util.Map;
import java.util.Set;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiI18NArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * For Internationalization.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiI18nElement extends GWikiWikiPage
{

  private static final long serialVersionUID = 5117850385113074134L;

  public GWikiI18nElement(GWikiElementInfo elementInfo)
  {
    super(elementInfo);
  }

  public boolean hasKey(String lang, String key)
  {
    GWikiArtefakt<?> f = parts.get(lang);
    if (f == null || (f instanceof GWikiI18NArtefakt) == false) {
      return false;
    }
    GWikiI18NArtefakt ia = (GWikiI18NArtefakt) f;
    return ia.getCompiledObject().containsKey(key);
  }
  
  public Set<String> getKeys(String lang) {
    GWikiArtefakt<?> f = parts.get(lang);
    if (f == null || (f instanceof GWikiI18NArtefakt) == false) {
      return null;
    }
    
    GWikiI18NArtefakt ia = (GWikiI18NArtefakt) f;
    return ia.getCompiledObject().keySet();
  }

  /**
   * 
   * @param lang
   * @param key
   * @return null if not found
   */
  public String getMessage(String lang, String key)
  {
    GWikiArtefakt<?> f = parts.get(lang);
    if (f == null || (f instanceof GWikiI18NArtefakt) == false) {
      return null;
    }
    GWikiI18NArtefakt ia = (GWikiI18NArtefakt) f;
    return ia.getCompiledObject().get(key);
  }

  public GWikiArtefakt<?> getMainPart()
  {
    return null;
  }

  public void serve(GWikiContext ctx)
  {

  }

  @Override
  public void saveParts(GWikiContext ctx, Map<String, GWikiEditorArtefakt<?>> editors)
  {
    super.saveParts(ctx, editors);
  }

}
