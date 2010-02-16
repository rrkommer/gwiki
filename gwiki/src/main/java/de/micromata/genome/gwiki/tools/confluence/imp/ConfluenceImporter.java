////////////////////////////////////////////////////////////////////////////
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

package de.micromata.genome.gwiki.tools.confluence.imp;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiBinaryAttachmentArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiDefaultFileNames;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.util.types.Converter;

/**
 * Importer for confluence XML exported spaces.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class ConfluenceImporter implements GWikiPropKeys
{
  private FileSystem fileSystem;

  private Map<String, Page> pageMap = new HashMap<String, Page>();

  private Map<String, BodyContent> bodyContentMap = new HashMap<String, BodyContent>();

  private Map<String, Attachment> attachmentMap = new HashMap<String, Attachment>();

  private GWikiProps defaultProps = new GWikiProps();

  public ConfluenceImporter(FileSystem fileSystem)
  {
    this.fileSystem = fileSystem;
  }

  public void parsePages(Document doc)
  {
    List<Element> pd = doc.selectNodes("//hibernate-generic/object[@class=\"Page\"]");
    for (Element el : pd) {
      Page ent = new Page(el);
      pageMap.put(ent.getId(), ent);
    }
  }

  public void parseBodyContents(Document doc)
  {
    List<Element> pd = doc.selectNodes("//hibernate-generic/object[@class=\"BodyContent\"]");
    for (Element d : pd) {
      BodyContent ent = new BodyContent(d);
      bodyContentMap.put(ent.getId(), ent);
    }
  }

  public void parseAttachments(Document doc)
  {
    List<Element> pd = doc.selectNodes("//hibernate-generic/object[@class=\"Attachment\"]");
    for (Element d : pd) {
      Attachment ent = new Attachment(d);
      attachmentMap.put(ent.getId(), ent);
    }
  }

  public void parseDom()
  {
    try {
      SAXReader reader = new SAXReader();
      byte[] data = fileSystem.readBinaryFile("entities.xml");
      Document document = reader.read(new ByteArrayInputStream(data));
      parsePages(document);
      parseBodyContents(document);
      parseAttachments(document);
    } catch (DocumentException ex) {
      throw new RuntimeException(ex);
    }
  }

  protected String getIdFromPage(GWikiContext wikiContext, Page page, String pathPrefix)
  {
    return pathPrefix + GWikiContext.getPageIdFromTitle(page.getTitle());
  }

  protected String patchLink(GWikiContext wikiContext, String lnk, String pathPrefix)
  {
    if (lnk.startsWith("http:") || lnk.startsWith("https:") == true) {
      return lnk;
    }
    if (lnk.startsWith("#") == true) {
      return lnk;
    }
    return pathPrefix + GWikiContext.getPageIdFromTitle(lnk);
  }

  protected String patchLinkBody(GWikiContext wikiContext, String lnkb, String pathPrefix)
  {
    List<String> parts = Converter.parseStringTokens(lnkb, "|", false);
    if (parts.size() == 1) {
      return patchLink(wikiContext, parts.get(0), pathPrefix);
    }
    if (parts.size() == 2) {
      return parts.get(0) + "|" + patchLink(wikiContext, parts.get(1), pathPrefix);
    }
    return parts.get(0) + "|" + patchLink(wikiContext, parts.get(1), pathPrefix) + parts.get(2);

  }

  protected String patchInternalLinks(GWikiContext wikiContext, String body, String pathPrefix)
  {
    String regexp = "\\[([^\n]+?)\\]";
    Pattern p = Pattern.compile(regexp);
    Matcher m = p.matcher(body);
    StringBuilder sb = new StringBuilder();
    int lastEnd = 0;
    while (m.find() == true) {
      int start = m.start();
      int end = m.end();
      sb.append(body.subSequence(lastEnd, start));
      String linkb = m.group(1);
      lastEnd = end;
      sb.append("[").append(patchLinkBody(wikiContext, linkb, pathPrefix)).append("]");

    }
    sb.append(body.substring(lastEnd));
    return sb.toString();
  }

  protected void mapCommon(GWikiContext wikiContext, ConfluenceElement page, GWikiProps props)
  {
    props.setStringValue(CREATEDBY, page.getCreatedBy());
    props.setStringValue(CREATEDAT, page.getCreatedAt());
    props.setStringValue(MODIFIEDBY, page.getModifiedBy());
    props.setStringValue(MODIFIEDAT, page.getModifiedAt());
  }

  protected GWikiElement createNewPage(GWikiContext wikiContext, Page page, String storePath, String pathPrefix)
  {
    String metaTemplatePageId = GWikiDefaultFileNames.DEFAULT_METATEMPLATE;

    GWikiMetaTemplate metaTemplate = wikiContext.getWikiWeb().findMetaTemplate(metaTemplatePageId);

    GWikiProps props = new GWikiProps();
    props.setStringValue(TYPE, metaTemplate.getElementType());
    props.setStringValue(WIKIMETATEMPLATE, metaTemplatePageId);
    if (StringUtils.isNotEmpty(page.getParent()) == true) {
      Page pent = pageMap.get(page.getParent());
      Page pp = (Page) pent;
      if (pp != null) {
        props.setStringValue(PARENTPAGE, getIdFromPage(wikiContext, pp, storePath));
      }

    }
    props.setStringValue(TITLE, page.getTitle());
    mapCommon(wikiContext, page, props);

    GWikiElementInfo ei = new GWikiElementInfo(props, metaTemplate);
    GWikiElement elementToEdit = wikiContext.getWikiWeb().getStorage().createElement(ei);
    // elementToEdit.setMetaTemplate(metaTemplate);
    Map<String, GWikiArtefakt< ? >> parts = new HashMap<String, GWikiArtefakt< ? >>();
    elementToEdit.collectParts(parts);

    GWikiWikiPageArtefakt wikiArt = (GWikiWikiPageArtefakt) parts.get("MainPage");
    StringBuilder sb = new StringBuilder();
    for (String bcr : page.getContentRefs()) {
      BodyContent bc = bodyContentMap.get(bcr);
      if (bc == null) {
        System.out.println("Has not body: " + page.getTitle());
        continue;
      }
      List<String> bcsl = bc.getBodies();
      for (String c : bcsl) {
        sb.append(bcsl.get(0));
      }

    }
    String body = sb.toString();
    body = patchInternalLinks(wikiContext, body, pathPrefix);
    wikiArt.setStorageData(body);
    elementToEdit.getElementInfo().setId(getIdFromPage(wikiContext, page, storePath));
    wikiContext.pushWikiElement(elementToEdit);
    try {
      wikiArt.compileFragements(wikiContext);
    } catch (Exception ex) {
      System.out.println("Syntax failure in : " + elementToEdit.getElementInfo().getId());
      ex.printStackTrace();
      return null;
    } finally {
      wikiContext.popWikiElement();
    }
    return elementToEdit;
  }

  protected byte[] getAttachmentContent(Attachment page)
  {
    String parent = page.getParent();
    String id = page.getId();
    String version = page.getVersion();
    String fname = "attachments/" + parent + "/" + id + "/" + version;
    if (fileSystem.exists(fname) == false) {
      return null;
    }
    return fileSystem.readBinaryFile(fname);
  }

  protected GWikiElement createNewAttachment(GWikiContext wikiContext, Attachment page, String storePath, String pathPrefix)
  {
    String metaTemplatePageId = GWikiDefaultFileNames.ATTACHMENT_METATEMPLATE;

    GWikiMetaTemplate metaTemplate = wikiContext.getWikiWeb().findMetaTemplate(metaTemplatePageId);

    GWikiProps props = new GWikiProps();
    props.setStringValue(TYPE, metaTemplate.getElementType());
    props.setStringValue(WIKIMETATEMPLATE, metaTemplatePageId);
    if (StringUtils.isNotEmpty(page.getParent()) == true) {
      Page pent = pageMap.get(page.getParent());
      Page pp = (Page) pent;
      if (pp != null) {
        props.setStringValue(PARENTPAGE, getIdFromPage(wikiContext, pp, pathPrefix));
      }

    }
    props.setStringValue(TITLE, page.getFileName());
    mapCommon(wikiContext, page, props);

    GWikiElementInfo ei = new GWikiElementInfo(props, metaTemplate);
    GWikiElement elementToEdit = wikiContext.getWikiWeb().getStorage().createElement(ei);
    // elementToEdit.setMetaTemplate(metaTemplate);
    Map<String, GWikiArtefakt< ? >> parts = new HashMap<String, GWikiArtefakt< ? >>();
    elementToEdit.collectParts(parts);
    GWikiBinaryAttachmentArtefakt attach = (GWikiBinaryAttachmentArtefakt) parts.get("");

    attach.setStorageData(getAttachmentContent(page));
    String id = storePath + GWikiContext.getPageIdFromTitle(page.getFileName());
    ;
    elementToEdit.getElementInfo().setId(id);

    return elementToEdit;
  }

  protected void createPage(GWikiContext wikiContext, Page page, String storePath, String pathPrefix)
  {
    GWikiElement el = createNewPage(wikiContext, page, storePath, pathPrefix);
    if (el == null) {
      return;
    }
    wikiContext.getWikiWeb().getStorage().storeElement(wikiContext, el, true);
  }

  protected void createAttachment(GWikiContext wikiContext, Attachment page, String storePath, String pathPrefix)
  {
    GWikiElement el = createNewAttachment(wikiContext, page, storePath, pathPrefix);
    if (el == null) {
      return;
    }
    try {
      wikiContext.pushWikiElement(el);
      wikiContext.getWikiWeb().getStorage().storeElement(wikiContext, el, true);
    } finally {
      wikiContext.popWikiElement();
    }
  }

  public void doImport(GWikiContext wikiContext, String storePath, String pathPrefix)
  {
    importPages(wikiContext, storePath, pathPrefix);
    importAttachments(wikiContext, storePath, pathPrefix);
  }

  public void importPages(GWikiContext wikiContext, String storePath, String pathPrefix)
  {
    for (Page page : pageMap.values()) {
      if (page.isArchive() == true) {
        continue;
      }
      createPage(wikiContext, page, storePath, pathPrefix);
    }
  }

  public void importAttachments(GWikiContext wikiContext, String storePath, String pathPrefix)
  {
    for (Attachment page : attachmentMap.values()) {
      if (page.isArchive() == true) {
        continue;
      }
      createAttachment(wikiContext, page, storePath, pathPrefix);
    }
  }

  public FileSystem getFileSystem()
  {
    return fileSystem;
  }

  public void setFileSystem(FileSystem fileSystem)
  {
    this.fileSystem = fileSystem;
  }

  public Map<String, Page> getPageMap()
  {
    return pageMap;
  }

  public void setPageMap(Map<String, Page> pageMap)
  {
    this.pageMap = pageMap;
  }

  public Map<String, BodyContent> getBodyContentMap()
  {
    return bodyContentMap;
  }

  public void setBodyContentMap(Map<String, BodyContent> bodyContentMap)
  {
    this.bodyContentMap = bodyContentMap;
  }

  public Map<String, Attachment> getAttachmentMap()
  {
    return attachmentMap;
  }

  public void setAttachmentMap(Map<String, Attachment> attachmentMap)
  {
    this.attachmentMap = attachmentMap;
  }

}
