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
package de.micromata.genome.gwiki.plugin.rssfeed_1_0.filter;

import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.Atom.content;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.Atom.entry;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.Atom.feed;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.Atom.link;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.Atom.name;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.Atom.subtitle;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.Atom.summary;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.Atom.updated;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.RSS.author;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.RSS.channel;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.RSS.copyright;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.RSS.description;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.RSS.item;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.RSS.language;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.RSS.link;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.RSS.pubData;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.RSS.rss;
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.RSS.title;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.GWikiContent;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiCollectMacroFragmentVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentP;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;
import de.micromata.genome.util.xml.xmlbuilder.XmlNode;

/**
 * @author Ingo Joseph
 */
public class RssFeedFilter implements GWikiServeElementFilter
{
  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.filter.GWikiFilter#filter(de.micromata .genome.gwiki.model.filter.GWikiFilterChain,
   * de.micromata.genome.gwiki.model.filter.GWikiFilterEvent)
   */
  public Void filter(GWikiFilterChain<Void, GWikiServeElementFilterEvent, GWikiServeElementFilter> chain, GWikiServeElementFilterEvent event)
  {
    GWikiContext wikiContext = event.getWikiContext();
    // RequestParameter
    String feed = wikiContext.getRequestParameter("feed");

    // render holen
    boolean renderChilds = true;
    String renderChildsParam = wikiContext.getRequestParameter("renderChilds");

    // gucken ob null
    if (renderChildsParam == null) {
    } else if ("false".equals(renderChildsParam)) {
      renderChilds = false;
    }
    // gucken ob true -> renderChilds=true

    // nullcheck wenn null nächster filter
    if (feed == null) {
      return chain.nextFilter(event);
    }

    if ("rss".equals(feed) == false && "atom".equals(feed) == false) {
      return chain.nextFilter(event);
    }

    GWikiArtefakt< ? > artefakt = event.getElement().getPart("MainPage");
    GWikiElementInfo elementInfo = event.getElement().getElementInfo();
    if (artefakt instanceof GWikiWikiPageArtefakt) {
      GWikiWikiPageArtefakt wikiPage = (GWikiWikiPageArtefakt) artefakt;
      String wikiPageHtmlContent = getPageContent(wikiContext, wikiPage);

      String title = elementInfo.getTitle();
      String autor = elementInfo.getCreatedBy();

      // DateFormat dfmt = new SimpleDateFormat("EE, d MMM yyyy hh:mm:ss +0100", Locale.ENGLISH);
      String createDate = formatDate(elementInfo.getCreatedAt());
      String modifiedDate = formatDate(elementInfo.getModifiedAt());
      wikiContext.getResponse().setHeader("Content-Type", "application/xml");

      List<GWikiElementInfo> children = null;
      if (renderChilds == true) {
        children = wikiContext.getElementFinder().getAllDirectChilds(elementInfo);
      }

      if ("rss".equals(feed) == true) {
        renderRSS(wikiContext, title, wikiPageHtmlContent, createDate, modifiedDate, autor, children, renderChilds);
      }

      if ("atom".equals(feed) == true) {
        renderAtom(wikiContext, title, wikiPageHtmlContent, createDate, modifiedDate, autor, children, renderChilds);
      }
    }
    return null;
  }

  /**
   * @param wikiContext
   * @param wikiPage
   * @return
   */
  private String getPageContent(GWikiContext wikiContext, GWikiWikiPageArtefakt wikiPage)
  {
    GWikiStandaloneContext standaloneContext = GWikiStandaloneContext.create();
    standaloneContext.setWikiElement(wikiContext.getCurrentElement());
    standaloneContext.setCurrentPart(wikiPage);
    standaloneContext.setRenderMode(RenderModes.combine(RenderModes.NoToc));
    wikiPage.render(standaloneContext);
    standaloneContext.flush();
    String wikiPageHtmlContent = standaloneContext.getOutString();
    return wikiPageHtmlContent;
  }

  private String getPageSummary(GWikiContext wikiContext, GWikiWikiPageArtefakt wikiPage)
  {
    GWikiStandaloneContext standaloneContext = GWikiStandaloneContext.create();
    standaloneContext.setWikiElement(wikiContext.getCurrentElement());
    standaloneContext.setCurrentPart(wikiPage);
    standaloneContext.setRenderMode(RenderModes.combine(RenderModes.NoToc, RenderModes.NoLinks));
    if (wikiPage.compileFragements(wikiContext) == false) {
      return null;
    }
    GWikiContent cont = wikiPage.getCompiledObject();
    GWikiCollectMacroFragmentVisitor col = new GWikiCollectMacroFragmentVisitor("pageintro");
    cont.iterate(col);
    if (col.getFound().isEmpty() == true) {
      return null;
    }

    GWikiMacroFragment mf = (GWikiMacroFragment) col.getFound().get(0);
    // standaloneContext.append("<small><br/>");

    if (mf.getChilds().size() == 1 && mf.getChilds().get(0) instanceof GWikiFragmentP) {
      GWikiFragmentP p = (GWikiFragmentP) mf.getChilds().get(0);
      p.renderChilds(standaloneContext);
    } else {
      mf.renderChilds(standaloneContext);
    }
    // standaloneContext.append("<br/></small><br/>");

    // wikiPage.render(standaloneContext);
    standaloneContext.flush();
    String wikiPageHtmlContent = standaloneContext.getOutString();
    return wikiPageHtmlContent;
  }

  private void renderAtom(GWikiContext wikiContext, String title, String wikiPageHtmlContent, String createDate, String modifiedDate,
      String autor, List<GWikiElementInfo> children, boolean renderChilds)
  {
    XmlElement atom = feed("http://www.w3.org/2005/Atom").nest( //
        author((name(Xml.text(autor)))),//
        (title(Xml.text(title))),//
        (link(wikiContext.getWikiWeb().getWikiConfig().getPublicURL())),//
        (subtitle(Xml.text(""))),//
        (updated(Xml.text(modifiedDate))),//
        (entry((title(Xml.text(title))),//
            (link(wikiContext.getRequest().getRequestURL().toString())),//
            (updated(Xml.text(createDate))),//
            (summary("html").nest(Xml.text(StringUtils.left(wikiPageHtmlContent, 100) + "[...]"))),//
            (content("html").nest(Xml.text(wikiPageHtmlContent))),//
            author((name(Xml.text(autor))))//
        )));

    if (renderChilds == true) {
      atom.getChilds().addAll(renderAtomEntries(wikiContext, children));
    }

    wikiContext.append(atom.toString());
    wikiContext.flush();
  }

  private Collection<XmlNode> renderAtomEntries(GWikiContext wikiContext, List<GWikiElementInfo> children)
  {
    List<XmlNode> childrenNodes = new ArrayList<XmlNode>();
    for (GWikiElementInfo child : children) {

      GWikiElement element = wikiContext.getWikiWeb().findElement(child.getId());
      GWikiArtefakt< ? > artefakt = element.getPart("MainPage");

      if (artefakt instanceof GWikiWikiPageArtefakt) {
        GWikiWikiPageArtefakt wikiPageArtefakt = (GWikiWikiPageArtefakt) artefakt;
        String pageContent = getPageContent(wikiContext, wikiPageArtefakt);

        // DateFormat dfmt = new SimpleDateFormat("EE, d MMM yyyy hh:mm:ss +0100", Locale.ENGLISH);
        String createDate = formatDate(child.getCreatedAt());
        XmlNode c = entry( //
            title(Xml.text(child.getTitle())), //
            (link(wikiContext.localUrl(child.getId()))),//
            (updated(Xml.text(createDate))),// TODO falsches Datum
            (summary("html").nest(//
                Xml.text(StringUtils.left(pageContent, 100) + "[...]"))), (content("html").nest(Xml.text(pageContent))), //
            (author(Xml.text(child.getCreatedBy()))));
        childrenNodes.add(c);
      }
    }
    return childrenNodes;
  }

  private void renderRSS(GWikiContext wikiContext, String title, String wikiPageHtmlContent, String createDate, String modDate,
      String autor, List<GWikiElementInfo> children, boolean renderChilds)
  {
    // GWikiElement el = wikiContext.getCurrentElement();
    // String title="";
    // String author = "";
    // if (el != null) {
    //      
    // }
    XmlElement rss = rss("2.0").nest(channel(( //
        title(Xml.text(title))),//
        (link(Xml.text(wikiContext.getWikiWeb().getWikiConfig().getPublicURL()))),//
        (description(Xml.text("Hier siehst du Feeds von GWiki!"))),// TODO
        (copyright(Xml.text(""))),// TODO
        (language(Xml.text("de-de"))),// TODO
        (pubData(Xml.text(modDate)))),//
        (item((title(Xml.text(title))),//
            (link(Xml.text(wikiContext.getRequest().getRequestURL().toString()))),//
            (pubData(Xml.text(modDate))),//
            (description(Xml.text(wikiPageHtmlContent))),//
            (author(Xml.text(autor)))//
        ))//
        );

    if (renderChilds == true) {
      rss.getChilds().addAll(renderRSSItems(wikiContext, children));
    }

    wikiContext.append(rss.toString());
    wikiContext.flush();
  }

  private Collection<XmlNode> renderRSSItems(GWikiContext wikiContext, List<GWikiElementInfo> children)
  {
    List<XmlNode> childrenNodes = new ArrayList<XmlNode>();
    for (GWikiElementInfo child : children) {
      GWikiElement element = wikiContext.getWikiWeb().findElement(child.getId());
      GWikiArtefakt< ? > artefakt = element.getPart("MainPage");

      if (artefakt instanceof GWikiWikiPageArtefakt) {
        GWikiWikiPageArtefakt wikiPageArtefakt = (GWikiWikiPageArtefakt) artefakt;
        // String pageContent = getPageContent(wikiContext, wikiPageArtefakt);
        String summary = getPageSummary(wikiContext, wikiPageArtefakt);
        String createDate = formatDate(child.getCreatedAt());
        XmlNode c = item(//
            title(Xml.text(child.getTitle())), //
            (link(Xml.text(wikiContext.getWikiWeb().getWikiConfig().getPublicURL() + child.getId()))),//
            (pubData(Xml.text(createDate))),//
            (description(Xml.text(summary))), //
            (author(Xml.text(child.getCreatedBy()))));
        childrenNodes.add(c);
      }
    }
    return childrenNodes;
  }

  public static ThreadLocal<DateFormat> FEED_DATEFORMAT = new ThreadLocal<DateFormat>() {

    @Override
    protected DateFormat initialValue()
    {
      DateFormat dfmt = new SimpleDateFormat("EE, d MMM yyyy hh:mm:ss +0100", Locale.ENGLISH);
      return dfmt;
    }

  };

  public static String formatDate(Date date)
  {
    if (date == null) {
      return "";
    }
    return FEED_DATEFORMAT.get().format(date);
  }
}