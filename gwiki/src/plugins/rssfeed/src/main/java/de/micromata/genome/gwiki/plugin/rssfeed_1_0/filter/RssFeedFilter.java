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
import static de.micromata.genome.gwiki.plugin.rssfeed_1_0.RSS.xmlHeader;
import groovyjarjarcommonscli.ParseException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;

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

/**
 * @author ingojoseph
 * 
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
    // nullcheck wenn null nächster filter
    if (feed == null) {
      return chain.nextFilter(event);
    }
    // vergleich auf "rss"
    if ("rss".equals(feed) == false) {
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
      String link = elementInfo.getId();
      String datum = elementInfo.getCreatedAt().toString();
      String autor = elementInfo.getCreatedBy();

      DateFormat dfmt = new SimpleDateFormat("EE, d MMM yyyy hh:mm:ss +0100", Locale.ENGLISH);
      datum = dfmt.format(elementInfo.getCreatedAt());

      wikiContext.getResponse().setHeader("Content-Type", "application/xml");

      XmlElement RSS = rss("2.0").nest(channel((title(Xml.text("GWiki RSS-Feeds"))),//
          (link(Xml.text("http://localhost:8081/"))),//
          (description(Xml.text("Hier siehst du Feeds von GWiki!"))),//
          (copyright(Xml.text("ingo"))),//
          (language(Xml.text("de-de"))),//
          (pubData(Xml.text(dfmt.format(new java.util.Date())))),//
          (item((title(Xml.text(title))),//
              (link(Xml.text(wikiContext.getRequest().getRequestURL().toString()))),//
              (pubData(Xml.text(datum))),//
              (description(Xml.text(wikiPageHtmlContent))),//
              (author(Xml.text(autor)))//
          ))//
          ));

      wikiContext.append(RSS.toString());
      wikiContext.flush();
    }
    return null;
  }
}