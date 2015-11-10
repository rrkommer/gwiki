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

import groovy.text.Template;

import java.io.Serializable;

import javax.servlet.jsp.PageContext;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GenomeJspProcessor implements GWikiJspProcessor
{
  protected void initPageContext(GWikiContext ctx)
  {
    if (ctx.getPageContext() == null) {
      PageContext pageContext = GenomeTemplateUtils.initPageContext(ctx);
      ctx.setPageContext(pageContext);
    }
  }

  public Serializable compile(GWikiContext ctx, String text)
  {
    initPageContext(ctx);
    Template ct = GenomeTemplateUtils.compile(ctx, text);
    return new TemplateHolder(ct);
  }

  public void renderTemplate(GWikiContext ctx, Object template)
  {
    initPageContext(ctx);
    TemplateHolder th = (TemplateHolder) template;
    Template ct = th.getTemplate();
    GenomeTemplateUtils.processPage(ct, ctx, null);
  }

  public PageContext createPageContext(GWikiContext ctx)
  {
    return GenomeTemplateUtils.initPageContext(ctx);
  }

}
