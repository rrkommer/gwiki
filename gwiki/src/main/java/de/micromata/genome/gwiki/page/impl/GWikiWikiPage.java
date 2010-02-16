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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiAbstractElement;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.runtime.CallableX;

/**
 * GWikiElement for a standard Wiki Page.
 * 
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWikiPage extends GWikiAbstractElement implements GWikiPropKeys
{

  private static final long serialVersionUID = 8856713275974466731L;

  protected Map<String, GWikiArtefakt< ? >> parts = new HashMap<String, GWikiArtefakt< ? >>();

  public GWikiWikiPage(GWikiElementInfo ei)
  {
    super(ei);
    initParts();
  }

  public GWikiWikiPage(GWikiWikiPage other)
  {
    super(new GWikiElementInfo(other.getElementInfo()));
    initParts();
  }

  protected void initParts()
  {
    String mt = getElementInfo().getProps().getStringValue(WIKIMETATEMPLATE);
    if (StringUtils.isEmpty(mt) == true) {
      mt = GWikiDefaultFileNames.DEFAULT_METATEMPLATE;
    }
    GWikiMetaTemplate metaTemplate = GWikiWeb.get().findMetaTemplate(mt);
    for (Map.Entry<String, GWikiArtefakt< ? >> me : metaTemplate.getParts().entrySet()) {
      GWikiArtefakt< ? > art = me.getValue();
      parts.put(me.getKey(), art);
    }
  }

  public void collectParts(Map<String, GWikiArtefakt< ? >> map)
  {
    for (Map.Entry<String, GWikiArtefakt< ? >> me : parts.entrySet()) {
      map.put(me.getKey(), me.getValue());
      me.getValue().collectParts(map);
    }
    super.collectParts(map);
  }

  public GWikiArtefakt< ? > getMainPart()
  {
    GWikiArtefakt< ? > ret = parts.get("");
    if (ret != null)
      return ret;
    return parts.get("Controler");
  }

  public void serve(GWikiContext ctx)
  {
    if (ctx.getWikiElement() == null) {
      ctx.setWikiElement(this);
    }
    String parentWikiID = (String) ctx.getRequestAttribute(WIKIPAGEID);
    if (parentWikiID != null) {
      ctx.setRequestAttribute(PARENTPAGE, parentWikiID);
    }
    ctx.setRequestAttribute(WIKIPAGEID, getElementInfo().getId());
    ctx.setRequestAttribute(WIKIPAGE, getElementInfo());

    if (parentWikiID != null) {
      ctx.setRequestAttribute(WIKIPAGEID, parentWikiID);
      ctx.setRequestAttribute(PARENTPAGE, null);
    }
    renderParts(ctx);
  }

  protected boolean renderParts(final GWikiContext ctx)
  {
    for (GWikiArtefakt< ? > art : parts.values()) {
      if (art instanceof GWikiExecutableArtefakt< ? >) {
        ((GWikiExecutableArtefakt< ? >) art).prepareHeader(ctx);
      }
    }
    return ctx.runWithParts(parts, new CallableX<Boolean, RuntimeException>() {

      public Boolean call() throws RuntimeException
      {

        GWikiExecutableArtefakt< ? > exec = (GWikiExecutableArtefakt< ? >) parts.get("Controler");
        if (exec == null) {
          exec = (GWikiExecutableArtefakt< ? >) parts.get("");
        }
        final GWikiExecutableArtefakt< ? > fexec = exec;
        return ctx.runWithArtefakt(exec, new CallableX<Boolean, RuntimeException>() {

          public Boolean call() throws RuntimeException
          {
            return fexec.render(ctx);
          }
        });
      }
    });

  }
}
