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
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiDefaultFileNames;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiCreatePageMacro extends GWikiMacroBean implements GWikiPropKeys
{
  private String metaTemplate;

  private String parent;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean#renderImpl(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (StringUtils.isNotBlank(metaTemplate) == true) {
      metaTemplate = GWikiDefaultFileNames.DEFAULT_METATEMPLATE;
    }
    GWikiMetaTemplate mt = ctx.getWikiWeb().findMetaTemplate(metaTemplate);
    GWikiProps props = new GWikiProps();

    if (metaTemplate != null) {
      props.setStringValue(TYPE, mt.getElementType());
    }
    props.setStringValue(WIKIMETATEMPLATE, metaTemplate);
    props.setStringValue(PARENTPAGE, parent);
    props.setStringValue(TITLE, "");

    GWikiElementInfo ei = new GWikiElementInfo(props, mt);

    if (ctx.getWikiWeb().getAuthorization().isAllowToCreate(ctx, ei) == false) {
      return true;
    }
    return true;
  }

  public String getParent()
  {
    return parent;
  }

  public void setParent(String parent)
  {
    this.parent = parent;
  }

}
