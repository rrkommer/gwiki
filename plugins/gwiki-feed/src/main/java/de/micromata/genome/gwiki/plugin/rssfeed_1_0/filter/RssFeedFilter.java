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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
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
   * @see de.micromata.genome.gwiki.model.filter.GWikiFilter#filter(de.micromata.genome.gwiki.model.filter.GWikiFilterChain,
   * de.micromata.genome.gwiki.model.filter.GWikiFilterEvent)
   */
  public Void filter(GWikiFilterChain<Void, GWikiServeElementFilterEvent, GWikiServeElementFilter> chain, GWikiServeElementFilterEvent event)
  {
    GWikiContext wikiContext = event.getWikiContext();
    // RequestParameter
    String feed = wikiContext.getRequestParameter("feed");

    // render holen
    boolean renderChilds = false;
    String renderChildsParam = wikiContext.getRequestParameter("renderChilds");

    // gucken ob null
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
      GWikiStandaloneContext standaloneContext = GWikiStandaloneContext.create();
      standaloneContext.setWikiElement(wikiContext.getCurrentElement());
      standaloneContext.setCurrentPart(wikiPage);
      standaloneContext.setRenderMode(RenderModes.combine(RenderModes.NoToc));
      wikiPage.render(standaloneContext);
      standaloneContext.flush();

      String title = elementInfo.getTitle();
      String wikiPageHtmlContent = standaloneContext.getOutString();
      String autor = elementInfo.getCreatedBy();

      DateFormat dfmt = new SimpleDateFormat("EE, d MMM yyyy hh:mm:ss +0100", Locale.ENGLISH);
      String createDate = dfmt.format(elementInfo.getCreatedAt());
      String currentDate = dfmt.format(new java.util.Date());
      wikiContext.getResponse().setHeader("Content-Type", "application/xml");

      List<GWikiElementInfo> children = null;
      if (renderChilds == true) {
        children = wikiContext.getElementFinder().getAllDirectChilds(elementInfo);
      }
      
      if ("rss".equals(feed)) {
        renderRSS(wikiContext, title, wikiPageHtmlContent, createDate, currentDate, autor, children, renderChilds);
      }

      if ("atom".equals(feed)) {
        renderAtom(wikiContext, title, wikiPageHtmlContent, createDate, currentDate, autor, children, renderChilds);
      }
    }
    return null;
  }

  private void renderAtom(GWikiContext wikiContext, String title, String wikiPageHtmlContent, String createDate, String currentDate,
      String autor, List<GWikiElementInfo> children, boolean renderChilds)
  {
    XmlElement atom = feed("http://www.w3.org/2005/Atom").nest(author((name(Xml.text("Ingo Joseph")))),//
        (title(Xml.text("GWiki Atom-Feeds"))),//
        (link("http://localhost:8081/")),//
        (subtitle(Xml.text("Hier siehst du Feeds von GWiki!"))),//
        (updated(Xml.text(currentDate))),//
        (entry((title(Xml.text(title))),//
            (link(wikiContext.getRequest().getRequestURL().toString())),//
            (updated(Xml.text(createDate))),//
            (summary("html").nest(Xml.text(StringUtils.left(wikiPageHtmlContent, 100) + "[...]"))),//
            (content("html").nest(Xml.text(wikiPageHtmlContent))),//
            author((name(Xml.text(autor))))//
        )));
    
    if (renderChilds == true) {
      atom.getChilds().addAll(renderAtomEntries(children));
    }
    
    wikiContext.append(atom.toString());
    wikiContext.flush();
  }

  private void renderRSS(GWikiContext wikiContext, String title, String wikiPageHtmlContent, String createDate, String currentDate,
      String autor, List<GWikiElementInfo> children, boolean renderChilds)
  {
    XmlElement rss = rss("2.0").nest(channel((title(Xml.text("GWiki RSS-Feeds"))),//
        (link(Xml.text("http://localhost:8081/"))),//
        (description(Xml.text("Hier siehst du Feeds von GWiki!"))),//
        (copyright(Xml.text("ingo"))),//
        (language(Xml.text("de-de"))),//
        (pubData(Xml.text(currentDate)))),//
        (item((title(Xml.text(title))),//
            (link(Xml.text(wikiContext.getRequest().getRequestURL().toString()))),//
            (pubData(Xml.text(createDate))),//
            (description(Xml.text(wikiPageHtmlContent))),//
            (author(Xml.text(autor)))//
        ))//
        );

    if (renderChilds == true) {
      rss.getChilds().addAll(renderRSSItems(children));
    }

    wikiContext.append(rss.toString());
    wikiContext.flush();
  }
  
  private Collection<XmlNode> renderAtomEntries(List<GWikiElementInfo> children)
  {
    List<XmlNode> childrenNodes = new ArrayList<XmlNode>();
    for (GWikiElementInfo child : children) {
      // TODO restlive attribute zu entry hinzufügen
      XmlNode c = entry(title(Xml.text(child.getTitle())));
      childrenNodes.add(c);
    }
    return childrenNodes;
  }

  // TODO restlichee attribute zu entry hinzufügen
  private Collection<XmlNode> renderRSSItems(List<GWikiElementInfo> children)
  {
    List<XmlNode> childrenNodes = new ArrayList<XmlNode>();
    for (GWikiElementInfo child : children) {
      XmlNode c = item(title(Xml.text(child.getTitle())));
      childrenNodes.add(c);
    }
    return childrenNodes;
  }
}