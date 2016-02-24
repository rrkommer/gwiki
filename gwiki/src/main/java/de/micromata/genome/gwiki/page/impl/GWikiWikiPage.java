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

import java.util.HashMap;
import java.util.Map;

import de.micromata.genome.gwiki.model.GWikiAbstractElement;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
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

  /**
   * request parameter and attribute for render part.
   */
  public static final String REQUESTATTR_GWIKIPART = "gwikipart";

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
    GWikiMetaTemplate metaTemplate = getMetaTemplate();
    if (metaTemplate == null) {
      GWikiLog.warn("Wiki element without meta template: page="
          + getElementInfo().getId()
          + "; metaTemplate:"
          + getElementInfo().getProps().getStringValue(WIKIMETATEMPLATE));
      return;
    }
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
    if (ctx.getCurrentElement() == null) {
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
    if (ctx.getRequestAttribute(REQUESTATTR_GWIKIPART) == null) {
      String gwikipart = ctx.getRequestParameter(REQUESTATTR_GWIKIPART);
      if (gwikipart != null) {
        ctx.setRequestAttribute(REQUESTATTR_GWIKIPART, gwikipart);
      }
    }
    renderParts(ctx);
  }

  public void prepareHeader(GWikiContext wikiContext)
  {
    for (GWikiArtefakt< ? > art : parts.values()) {
      if (art instanceof GWikiExecutableArtefakt< ? >) {
        ((GWikiExecutableArtefakt< ? >) art).prepareHeader(wikiContext);
      }
    }
  }

  protected boolean renderParts(final GWikiContext ctx)
  {
    return ctx.runWithParts(parts, new CallableX<Boolean, RuntimeException>() {

      public Boolean call() throws RuntimeException
      {
        String partName = (String) ctx.getRequestAttribute(REQUESTATTR_GWIKIPART);
        if (partName == null) {
          partName = "Controler";
        }
        GWikiExecutableArtefakt< ? > exec = (GWikiExecutableArtefakt< ? >) parts.get(partName);
        if (exec == null) {
          exec = (GWikiExecutableArtefakt< ? >) getPart(partName);
          if (exec == null) {
            exec = (GWikiExecutableArtefakt< ? >) parts.get("");
          }
        }
        final GWikiExecutableArtefakt< ? > fexec = exec;
        if (fexec == null) {
          GWikiLog.warn("Controler artefakt is not an executable. pageId=" + ctx.getCurrentElement().getElementInfo().getId());
          return false;
        }
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
