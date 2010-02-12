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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiDefaultFileNames;
import de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Base implementation for a Wiki Element (page, config, attachment, etc.).
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiAbstractElement implements GWikiElement, GWikiPropKeys
{

  private static final long serialVersionUID = -5188765401655995050L;

  protected GWikiElementInfo elementInfo;

  // protected GWikiMetaTemplate metaTemplate;

  public GWikiAbstractElement(GWikiElementInfo elementInfo)
  {
    this.elementInfo = elementInfo;
  }

  public String toString()
  {
    return elementInfo.toString();
  }

  public GWikiMetaTemplate getMetaTemplate()
  {
    if (elementInfo.getMetaTemplate() != null)
      return elementInfo.getMetaTemplate();
    String templateName = elementInfo.getProps().getStringValue(WIKIMETATEMPLATE);
    if (StringUtils.isBlank(templateName) == true) {
      templateName = GWikiDefaultFileNames.DEFAULT_METATEMPLATE;
    }
    GWikiMetaTemplate metaTemplate = GWikiWeb.get().findMetaTemplate(templateName);
    elementInfo.setMetaTemplate(metaTemplate);
    return metaTemplate;
  }

  public void collectParts(Map<String, GWikiArtefakt< ? >> parts)
  {
    parts.put("Settings", new GWikiSettingsPropsArtefakt(getElementInfo().getProps()));
  }

  public GWikiArtefakt< ? > getPart(String partName)
  {
    Map<String, GWikiArtefakt< ? >> parts = new HashMap<String, GWikiArtefakt< ? >>();
    collectParts(parts);
    return parts.get(partName);
  }

  // protected Map<String, String> parseElementProperties(GWikiContext wikiContext, GWikiEditPropsDescription[] descriptions)
  // {
  // Map<String, String> ret = new HashMap<String, String>();
  // for (GWikiEditPropsDescription pdesc : descriptions) {
  // String v = wikiContext.getRequestParameter("elementProps." + pdesc.getName());
  // if (StringUtils.isNotBlank(v) == true) {
  // ret.put(pdesc.getName(), v);
  // }
  // }
  // return ret;
  // }

  public void saveParts(final GWikiContext ctx, final Map<String, GWikiEditorArtefakt< ? >> editors)
  {
    ctx.runElement(this, new CallableX<Void, RuntimeException>() {

      public Void call() throws RuntimeException
      {
        for (Map.Entry<String, GWikiEditorArtefakt< ? >> me : editors.entrySet()) {
          me.getValue().onSave(ctx);
        }
        // getElementInfo().getProps().getMap().putAll(parseElementProperties(ctx, GWikiEditWikiPropsDescription.values()));
        return null;
      }
    });

  }

  public GWikiElementInfo getElementInfo()
  {
    return elementInfo;
  }

  public void setElementInfo(GWikiElementInfo elementInfo)
  {
    this.elementInfo = elementInfo;
  }

}
