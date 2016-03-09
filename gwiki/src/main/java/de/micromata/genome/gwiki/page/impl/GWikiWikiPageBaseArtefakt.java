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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiTextArtefaktBase;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiWikiPageCompileFilter;
import de.micromata.genome.gwiki.model.filter.GWikiWikiPageCompileFilterEvent;
import de.micromata.genome.gwiki.model.filter.GWikiWikiPageRenderFilter;
import de.micromata.genome.gwiki.model.filter.GWikiWikiPageRenderFilterEvent;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiSimpleFragmentVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParser;
import de.micromata.genome.gwiki.utils.AppendableI;
import de.micromata.genome.logging.LoggingServiceManager;
import de.micromata.genome.util.runtime.CallableX;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWikiPageBaseArtefakt extends GWikiTextArtefaktBase<GWikiContent>
{

  private static final long serialVersionUID = 1484439053723512619L;

  private List<GWikiFragment> prepareHeaderFragments = null;

  @Override
  public String getFileSuffix()
  {
    return ".gwiki";
  }

  public GWikiEditorArtefakt<?> getEditor(GWikiElement elementToEdit, GWikiEditPageActionBean bean, String partName)
  {
    //    return new GWikiWikiPageEditorArtefakt(elementToEdit, bean, partName, this);
    return new GWikiWikiPageEditorArtefakt2(elementToEdit, bean, partName, this);
  }

  @Override
  public void prepareHeader(GWikiContext wikiContext)
  {
    if (compileFragements(wikiContext) == false || prepareHeaderFragments == null) {
      return;
    }
    for (GWikiFragment frag : prepareHeaderFragments) {
      frag.prepareHeader(wikiContext);
    }
  }

  public void getPreview(GWikiContext ctx, final AppendableI sb)
  {

    compileFragements(ctx);
    String contextPath = ctx.getRequest().getContextPath();
    String servletPath = ctx.getRequest().getServletPath();
    GWikiStandaloneContext swc = new GWikiStandaloneContext(ctx.getWikiWeb(), ctx.getServlet(), contextPath,
        servletPath);
    swc.setWikiElement(ctx.getCurrentElement());
    GWikiContent wkk = getCompiledObject();
    if (wkk == null) {
      return;
    }
    int rm = ctx.getRenderMode();
    try {
      GWikiContext.setCurrent(swc);
      swc.setRenderMode(RenderModes.ForIndex.getFlag());
      wkk.render(swc);
      sb.append(swc.getJspWriter().getString());
    } finally {
      GWikiContext.setCurrent(ctx);
      ctx.setRenderMode(rm);
    }

  }

  @Override
  public void setCompiledObject(GWikiContent compiledObject)
  {
    super.setCompiledObject(compiledObject);
  }

  public boolean compileFragements(GWikiContext wikiContext)
  {
    if (getCompiledObject() != null) {
      return true;
    }
    if (wikiContext == null) {
      wikiContext = GWikiContext.getCurrent();
    }
    final GWikiContext wctx = wikiContext;
    long start = System.currentTimeMillis();
    wikiContext.getWikiWeb().getFilter()
        .compileWikiWikiPage(wikiContext, wikiContext.getCurrentElement(), this, new GWikiWikiPageCompileFilter()
        {

          @Override
          public Void filter(GWikiFilterChain<Void, GWikiWikiPageCompileFilterEvent, GWikiWikiPageCompileFilter> chain,
              GWikiWikiPageCompileFilterEvent event)
          {
            if (StringUtils.isEmpty(getStorageData()) == true) {
              setCompiledObject(new GWikiContent(new ArrayList<GWikiFragment>()));
              return null;
            }
            GWikiWikiParser wkparse = new GWikiWikiParser();
            setCompiledObject(wkparse.parse(wctx, getStorageData()));
            return null;
          }
        });
    LoggingServiceManager.get().getStatsDAO().addPerformance(GWikiLogCategory.Wiki, "GWikiParse.parse",
        System.currentTimeMillis() - start, 0);
    if (getCompiledObject() == null) {
      return false;
    }
    ArrayList<GWikiFragment> tprepareHeaderFragments = new ArrayList<GWikiFragment>();
    getCompiledObject().iterate(new GWikiSimpleFragmentVisitor()
    {

      @Override
      public void begin(GWikiFragment fragment)
      {
        if (fragment.requirePrepareHeader(wctx) == true) {
          tprepareHeaderFragments.add(fragment);
        }
      }
    });
    if (tprepareHeaderFragments.isEmpty() == true) {
      prepareHeaderFragments = null;
    } else {
      prepareHeaderFragments = tprepareHeaderFragments;
    }
    return getCompiledObject() != null;
  }

  public boolean renderChunk(final GWikiContext ctx, String chunkName)
  {
    ctx.setRequestAttribute("gwikichunk", chunkName);
    return ctx.runWithParts(parts, new CallableX<Boolean, RuntimeException>()
    {
      @Override
      public Boolean call()
      {
        return renderWithParts(ctx);
      }
    });
  }

  @Override
  public boolean renderWithParts(final GWikiContext ctx)
  {

    if (compileFragements(ctx) == false) {
      return true;
    }

    return ctx.runWithArtefakt(this, new CallableX<Boolean, RuntimeException>()
    {

      @Override
      public Boolean call() throws RuntimeException
      {
        return ctx.getWikiWeb().getFilter().renderWikiWikiPage(ctx, GWikiWikiPageBaseArtefakt.this,
            new GWikiWikiPageRenderFilter()
        {

          @Override
          public Boolean filter(
              GWikiFilterChain<Boolean, GWikiWikiPageRenderFilterEvent, GWikiWikiPageRenderFilter> chain,
              GWikiWikiPageRenderFilterEvent event)
          {
            return event.getWikiPageArtefakt().getCompiledObject().render(event.getWikiContext());
          }
        });

      }
    });
  }
}
