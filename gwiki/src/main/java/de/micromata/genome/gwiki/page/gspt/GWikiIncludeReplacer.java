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

package de.micromata.genome.gwiki.page.gspt;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiTextArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiIncludeReplacer extends RegExpReplacer
{
  private GWikiContext context;

  public GWikiIncludeReplacer(GWikiContext context)
  {
    this();
    this.context = context;
  }

  protected GWikiIncludeReplacer()
  {
    super("(.*?)(<%?@\\s*include\\s+)(.*)", "(.*?)([@%]>)(.*)");
  }

  @Override
  public String getStart()
  {
    return "<@include";
  }

  @Override
  public String getEnd()
  {
    return "@>";
  }

  public String externalInclude(Map<String, String> attr, String fn)
  {
    return "<% wikiContext.include(\"" + fn + "\"); %>\n";
  }

  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String fn = attr.get("file");
    String embedd = attr.get("embedd");
    boolean doEmbedd = StringUtils.equals(embedd, "false") == false;
    if (doEmbedd == false)
      return externalInclude(attr, fn);

    GWikiContext wctx = (GWikiContext) ctx.getAttribute("wikiContext");
    String id = fn;
    if (id.endsWith(".gspt") == true) {
      id = id.substring(0, id.length() - ".gspt".length());
    }
    if (id.startsWith("/") == true)
      id = id.substring(1);
    GWikiElement el = wctx.getWikiWeb().getElement(id);
    GWikiArtefakt< ? > fact = el.getMainPart();
    if (fact instanceof GWikiTextArtefakt) {
      GWikiTextArtefakt< ? > text = (GWikiTextArtefakt< ? >) fact;
      return text.getStorageData();
    } else {
      return "<% wikiContext.includeText('" + id + "'); %>";
    }
  }

  public GWikiContext getContext()
  {
    return context;
  }

  public void setContext(GWikiContext context)
  {
    this.context = context;
  }

}
