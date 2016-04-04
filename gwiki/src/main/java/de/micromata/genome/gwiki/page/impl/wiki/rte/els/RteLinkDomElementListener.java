package de.micromata.genome.gwiki.page.impl.wiki.rte.els;

import java.util.List;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;
import de.micromata.genome.gwiki.utils.StringUtils;

public class RteLinkDomElementListener implements DomElementListener
{

  @Override
  public boolean listen(DomElementEvent event)
  {
    GWikiWikiParserContext parseContext = event.getParseContext();
    GWikiFragmentLink link = parseLink(event);
    List<GWikiFragment> frags = event.walkCollectChilds();
    link.addChilds(frags);
    parseContext.addFragment(link);
    return false;
  }

  protected GWikiFragmentLink parseLink(DomElementEvent event)
  {
    // if (StringUtils.isNotEmpty(attributes.getValue("wikitarget")) == true) {
    // parseContext.addFragment(new GWikiFragementLink(attributes.getValue("wikitarget")));
    // return;
    // }
    String href = getAttribute(event, "href", "url");
    GWikiContext wikiContext = GWikiContext.getCurrent();
    String tat = getAttribute(event, "title");
    String title = tat;
    String id = href;
    if (href != null && wikiContext != null) {
      String ctxpath = wikiContext.getRequest().getContextPath();
      if (href.startsWith(ctxpath) == true) {
        if (ctxpath.length() > 0) {
          id = href.substring(ctxpath.length() + 1);
        }
        if (id.startsWith("/") == true) {
          id = id.substring(1);
        }
        GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(id);
        if (ei == null) {
          id = href;
        } else {
          String origtitle = ei.getTitle();
          if (StringUtils.equals(origtitle, title) == true) {
            title = null;
          }
        }
      }
    }
    if (id == null) {
      id = "";
    }
    GWikiFragmentLink link = new GWikiFragmentLink(id);

    if (StringUtils.isNotBlank(title) == true) {
      link.setTitle(title);
    }
    tat = getAttribute(event, "target", "windowTarget");
    if (StringUtils.isNotBlank(tat) == true) {
      link.setWindowTarget(tat);
    }
    tat = getAttribute(event, "class");
    if (StringUtils.isNotBlank(tat) == true) {
      link.setLinkClass(tat);
    }
    return link;
  }

  public static String getAttribute(DomElementEvent event, String nativeKey)
  {
    return getAttribute(event, nativeKey, nativeKey);
  }

  public static String getAttribute(DomElementEvent event, String nativeKey, String dataKey)
  {
    String ret = event.getAttr("data-wiki-" + dataKey);
    if (StringUtils.isEmpty(ret) == false) {
      return ret;
    }
    return event.getAttr(nativeKey);
  }
}
