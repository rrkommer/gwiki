//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.page.impl.wiki.rte.els;

import java.util.Base64;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiBinaryAttachmentArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiFileAttachment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentImage;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;
import de.micromata.genome.logging.GLog;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class RteImageDomElementListener implements DomElementListener
{

  @Override
  public boolean listen(DomElementEvent event)
  {
    String pageId = event.getAttr("data-pageid");
    if (StringUtils.isBlank(pageId) == true) {
      pageId = event.getAttr("data-wiki-url");
    }
    pageId = handleDataImage(event, pageId);
    GWikiFragmentImage image = parseImage(event, pageId);

    String styleClass = image.getStyleClass();
    styleClass = StringUtils.remove(styleClass, "weditimg");
    if (StringUtils.isBlank(styleClass) == true) {
      styleClass = "";
    }
    image.setStyleClass(styleClass);
    event.getParseContext().addFragment(image);
    return false;

  }

  public String getAutoImgPageId(DomElementEvent event, String curPageId, String suffix)
  {
    for (int i = 0; i < 100; ++i) {
      String nid = curPageId + "_Img" + i;
      if (StringUtils.isNotBlank(suffix) == true) {
        nid += "." + suffix;
      }
      if (event.getWikiContext().getWikiWeb().findElementInfo(nid) == null) {
        return nid;
      }
    }
    return null;
  }

  protected String handleDataImage(DomElementEvent event, String pageId)
  {
    if (StringUtils.isBlank(pageId) == true) {
      return pageId;
    }
    String src = event.getAttr("src");
    // handle data:image/png;base64,iVBORw...
    if (StringUtils.startsWith(src, "data:image") == false) {
      return pageId;
    }
    int edix = src.indexOf(';');
    if (edix == -1) {
      return pageId;
    }
    String mime = src.substring(5, edix);
    int suffixidx = mime.indexOf('/');
    String suffix = null;
    if (suffixidx != -1) {
      suffix = mime.substring(suffixidx + 1);
    }
    String rest = src.substring(edix + 1);
    if (rest.startsWith("base64,") == false) {
      return pageId;
    }
    rest = rest.substring("base64,".length());
    byte[] data;
    try {
      data = Base64.getDecoder().decode(rest);
    } catch (IllegalArgumentException ex) {
      GLog.warn(GWikiLogCategory.Wiki, "Cannot decode image base64");
      return pageId;
    }
    String cpid = event.getParseContext().getCurrentPageId();
    if (src.equals(pageId) == true) {

      if (cpid == null) {
        GLog.warn(GWikiLogCategory.Wiki, "Non editPageId set. Cannot convert base64 image");
        return pageId;
      }
      pageId = getAutoImgPageId(event, cpid, suffix);
      if (pageId == null) {
        GLog.warn(GWikiLogCategory.Wiki, "Cannot create new image page id for base64image");
        return pageId;
      }
    }
    GWikiElement imageElement = event.getWikiContext().getWikiWeb().findElement(pageId);
    if ((imageElement instanceof GWikiFileAttachment) == false) {
      if (imageElement != null) {
        GLog.warn(GWikiLogCategory.Wiki, "Cannot overwrite non attachment with image: " + pageId);
        return pageId;
      }
      String metaTemplateId = "admin/templates/FileWikiPageMetaTemplate";
      imageElement = GWikiWebUtils.createNewElement(event.getWikiContext(), pageId, metaTemplateId, null);
      imageElement.getElementInfo().getProps().setStringValue(GWikiPropKeys.PARENTPAGE, cpid);
    }
    GWikiFileAttachment fat = (GWikiFileAttachment) imageElement;

    GWikiBinaryAttachmentArtefakt mpart = (GWikiBinaryAttachmentArtefakt) imageElement.getMainPart();
    mpart.setStorageData(data);
    event.getWikiContext().getWikiWeb().saveElement(event.getWikiContext(), fat, false);

    return pageId;
  }

  protected GWikiFragmentImage parseImage(DomElementEvent event, String pageId)
  {
    String source;
    if (StringUtils.isBlank(pageId) == true) {
      source = event.getAttr("data-wiki-url");
      if (StringUtils.isBlank(source) == true) {
        source = event.getAttr("src");
        GWikiContext wikiContext = GWikiContext.getCurrent();
        if (wikiContext != null && source != null) {
          String ctxpath = wikiContext.getRequest().getContextPath();
          if (StringUtils.isNotEmpty(ctxpath) && source.startsWith(ctxpath) == true) {
            source = source.substring(ctxpath.length() + 1);
          }
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
