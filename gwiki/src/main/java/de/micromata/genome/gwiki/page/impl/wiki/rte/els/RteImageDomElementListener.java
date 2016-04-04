package de.micromata.genome.gwiki.page.impl.wiki.rte.els;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentImage;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;

public class RteImageDomElementListener implements DomElementListener
{

  @Override
  public boolean listen(DomElementEvent event)
  {
    String pageId = event.getAttr("data-pageid");

    GWikiFragmentImage image = parseImage(event, pageId);

    String styleClass = image.getStyleClass();
    styleClass = StringUtils.remove(styleClass, "weditimg");
    if (StringUtils.isBlank(styleClass) == true) {
      styleClass = "";
    }
    image.setStyleClass(styleClass);
    event.getParseContext().addFragment(image);
    return true;

  }

  protected GWikiFragmentImage parseImage(DomElementEvent event, String pageId)
  {
    String source;
    if (pageId == null) {
      source = event.getAttr("src");

      GWikiContext wikiContext = GWikiContext.getCurrent();
      if (wikiContext != null && source != null) {
        String ctxpath = wikiContext.getRequest().getContextPath();
        if (StringUtils.isNotEmpty(ctxpath) && source.startsWith(ctxpath) == true) {
          source = source.substring(ctxpath.length() + 1);
        }
      }
    } else {
      source = pageId;
    }
    GWikiFragmentImage image = new GWikiFragmentImage(source);
    String tat = event.getAttr("alt");
    if (StringUtils.isNotEmpty(tat) == true) {
      image.setAlt(tat);
    }
    tat = event.getAttr("width");
    if (StringUtils.isNotEmpty(event.getAttr("width")) == true) {
      image.setWidth(tat);
    }
    tat = event.getAttr("height");
    if (StringUtils.isNotEmpty(tat) == true) {
      image.setHeight(tat);
    }
    tat = event.getAttr("border");
    if (StringUtils.isNotEmpty(tat) == true) {
      image.setBorder(tat);
    }
    tat = event.getAttr("hspace");
    if (StringUtils.isNotEmpty(tat) == true) {
      image.setHspace(tat);
    }
    tat = event.getAttr("vspace");
    if (StringUtils.isNotEmpty(tat) == true) {
      image.setVspace(tat);
    }
    tat = event.getAttr("class");
    if (StringUtils.isNotEmpty(tat) == true) {
      image.setStyleClass(tat);
    }
    tat = event.getAttr("style");
    if (StringUtils.isNotEmpty(tat) == true) {
      image.setStyle(tat);
    }
    return image;
  }
}
