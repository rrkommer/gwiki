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

package de.micromata.genome.gwiki.controls;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FileSystemUtils;
import de.micromata.genome.gdbfs.RamFileSystem;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHeading;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentImage;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByChildOrderComparator;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByIntPropComparator;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByOrderComparator;
import de.micromata.genome.gwiki.utils.StringUtils;
import de.micromata.genome.util.matcher.EveryMatcher;
import de.micromata.genome.util.runtime.CallableX1;
import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * ActionBean pre alpha implementation of exporting pages in different formats.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiGenDocActionBean extends ActionBeanBase
{
  private static final String WIKIGENDOCBEAN = "WIKIGENDOCBEAN";

  private String format = "DOC";

  private String rootPageId;

  private String docTree;

  private String documentTemplate = "admin/gendoc/DefaultWord";

  @Override
  public Object onInit()
  {
    if (StringUtils.isEmpty(rootPageId) == true) {
      if (StringUtils.isNotBlank(wikiContext.getRequestParameter("refPageId")) == true) {
        rootPageId = wikiContext.getRequestParameter("refPageId");
      } else {
        wikiContext.addSimpleValidationError(wikiContext.getTranslated("gwiki.gen.doc.rootpage.select"));
        return null;
      }
    }
    GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(rootPageId);
    if (ei == null || wikiContext.getWikiWeb().getAuthorization().isAllowToView(wikiContext, ei) == false) {
      wikiContext.addSimpleValidationError(wikiContext.getTranslated("gwiki.gen.doc.rootpage.notvalid"));
      return null;
    }

    return null;
  }

  protected void addImage(FileSystem fs, String imageUrl)
  {
    GWikiElement el = wikiContext.getWikiWeb().findElement(imageUrl);
    if (el == null) {
      GWikiLog.warn("DocExport; Cannot find image: " + imageUrl);
      return;
    }
    GWikiStandaloneContext simContext = new GWikiStandaloneContext(wikiContext);
    el.serve(simContext);
    byte[] data = simContext.getStandaloneResponse().getOutputBytes();
    FileSystem fsw = fs.getFsForWrite(imageUrl);
    String parent = FileNameUtils.getParentDir(imageUrl);
    if (StringUtils.isNotEmpty(parent) == true) {
      if (fsw.existsForWrite(parent) == false) {
        fsw.mkdirs(parent);
      }
    }
    fsw.writeBinaryFile(imageUrl, data, true);
  }

  @SuppressWarnings("unchecked")
  void serveDoc(String htmlData, GWikiStandaloneContext simContext)
  {
    RamFileSystem fs = new RamFileSystem();
    fs.writeTextFile("WikiExport.doc", htmlData, true);
    Set<String> images = (Set<String>) simContext.getRequestAttribute(GWikiFragmentImage.WIKIGENINCLUDEDIMAGES);
    if (images != null) {
      for (String ip : images) {
        addImage(fs, ip);
      }
    }
    wikiContext.getResponse().setHeader("Content-disposition", "attachment; filename=\"WikiExport.zip\"");
    wikiContext.getResponse().setContentType("application/zip");
    try {
      FileSystemUtils.copyToZip(fs.getFileObject(""), new EveryMatcher<String>(),
          wikiContext.getResponse().getOutputStream());
      // wikiContext.flush();
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  void serveHtml(String htmlData, GWikiStandaloneContext simContext)
  {
    wikiContext.getResponse().setHeader("Content-disposition", "attachment; filename=\"WikiExport.html\"");
    wikiContext.getResponse().setContentType("text/html");
    wikiContext.append(htmlData);
    wikiContext.flush();
  }

  protected String patchEntities(String htmlData) throws IOException
  {
    int idx = htmlData.indexOf("<HTML>");
    if (idx == -1) {
      return htmlData;
    }
    htmlData = StringUtils.replace(htmlData, "<BR></BR>", "<BR/>");
    htmlData = StringUtils.replace(htmlData, "<BR>", "<BR/>");
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    StringBuilder sb = new StringBuilder();
    boolean withXml = false;
    boolean xhtmldocType = false;
    if (withXml == true) {
      sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
    }
    if (xhtmldocType == true) {
      sb.append(
          "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
    } else {
      sb.append("<!DOCTYPE HTML  [\n");
      InputStream entis = cl.getResourceAsStream("xhtml-lat1.ent");
      sb.append(new String(IOUtils.toByteArray(entis)));
      entis = cl.getResourceAsStream("xhtml-special.ent");
      sb.append(new String(IOUtils.toByteArray(entis)));
      entis = cl.getResourceAsStream("xhtml-symbol.ent");
      sb.append(new String(IOUtils.toByteArray(entis)));
      sb.append("]\n>\n");
    }
    htmlData = htmlData.substring(idx);
    Pattern p = Pattern.compile("\\</{0,1}(\\w+)");

    htmlData = StringUtils.replace(htmlData, p, 1, new CallableX1<String, String, RuntimeException>()
    {

      @Override
      public String call(String arg1) throws RuntimeException
      {
        return arg1.toLowerCase();
      }
    });

    sb.append(htmlData);
    return sb.toString();

  }

  // does not render a usefull format
  // void servePdf(String htmlData)
  // {
  // try {
  // XMLParserConfiguration parser = new HTMLConfiguration();
  // Purifier purifier = new Purifier();
  // StringWriter sout = new StringWriter();
  // PassthroughHtmlFilter target = new PassthroughHtmlFilter(sout, "UTF-8");
  // purifier.setDocumentHandler(target);
  // parser.setFeature("http://cyberneko.org/html/features/augmentations", true);
  // parser.setProperty("http://cyberneko.org/html/properties/filters", new XMLDocumentFilter[] { purifier, target});
  // XMLInputSource source = new XMLInputSource(null, null, null, new StringReader(htmlData), "UTF-8");
  //
  // try {
  // parser.parse(source);
  // htmlData = sout.getBuffer().toString();
  // } catch (Exception ex) {
  // throw new RuntimeException(ex);
  // }
  // htmlData = patchEntities(htmlData);
  //
  // ByteArrayOutputStream bout = new ByteArrayOutputStream();
  // Document document = new Document();
  //
  // PdfWriter.getInstance(document, bout);
  // // document.open();
  // InputStream is = new ByteArrayInputStream(htmlData.getBytes("UTF-8"));
  // // HtmlParser p = new HtmlParser();
  //
  // // p.go(document, new InputSource(is));
  // StyleSheet st = new StyleSheet();
  //
  // GenPdfDocHtmlParser p = new GenPdfDocHtmlParser();
  // // does not word, the parser ignores this.
  // p.setEntityResolver(new EntityResolver() {
  //
  // public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
  // {
  // return new InputSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(systemId));
  // }
  // });
  // p.go(document, new InputSource(is));
  //
  // boolean serveHtml = false;
  // if (serveHtml == true) {
  // wikiContext.getResponse().setHeader("Content-disposition", "attachment; filename=\"WikiExport.html\"");
  // wikiContext.getResponse().setContentType("text/html");
  // wikiContext.append(htmlData);
  // wikiContext.flush();
  // } else {
  // wikiContext.getResponse().setHeader("Content-disposition", "attachment; filename=\"WikiExport.pdf\"");
  // wikiContext.getResponse().setContentType("application/pdf");
  // byte[] pdfdata = bout.toByteArray();
  // wikiContext.getResponse().setContentLength(pdfdata.length);
  // IOUtils.copy(new ByteArrayInputStream(pdfdata), wikiContext.getResponse().getOutputStream());
  // wikiContext.getResponse().getOutputStream().flush();
  // }
  // } catch (RuntimeException ex) {
  // throw ex;
  // } catch (Exception ex) {
  // throw new RuntimeException(ex);
  // }
  // }

  public Object onExport()
  {
    GWikiElement el = wikiContext.getWikiWeb().findElement(documentTemplate);
    if (el == null) {
      wikiContext.addSimpleValidationError("No valid document template selected.");
      return null;
    }
    GWikiStandaloneContext simContext = new GWikiStandaloneContext(wikiContext);
    simContext.setRequestAttribute(WIKIGENDOCBEAN, this);
    simContext.setRequestAttribute(GWikiFragmentImage.WIKIGENINCLUDEDIMAGES, new HashSet<String>());

    try {
      GWikiContext.setCurrent(simContext);
      simContext.setSkin("docexp");
      simContext.setRenderMode(RenderModes.combine(//
          RenderModes.InMem, //
          RenderModes.NoToc, //
          RenderModes.NoLinks, //
          RenderModes.LocalImageLinks, //
          // RenderModes.GlobalImageLinks, //
          RenderModes.NoPageDecoration//
      ));
      el.prepareHeader(simContext);
      el.serve(simContext);
      simContext.getJspWriter().flush();
      simContext.flush();
      String data = simContext.getJspWriter().getString();
      // if (StringUtils.equals(format, "PDF") == true) {
      // servePdf(data);
      // } else
      if (StringUtils.equals(format, "DOC") == true) {
        serveDoc(data, simContext);
      } else if (StringUtils.equals(format, "HTML") == true) {
        serveHtml(data, simContext);
      } else {
        wikiContext.addSimpleValidationError(translate("gwiki.gen.doc.unknownformat", format));
        return null;
      }
      return noForward();
    } catch (Exception ex) {
      GWikiLog.warn("Failed to generate document: " + ex.getMessage(), ex);
      wikiContext.addSimpleValidationError("Failed to generate document: " + ex.getMessage());
    } finally {
      GWikiContext.setCurrent(wikiContext);
    }
    return null;
  }

  public void renderPages(GWikiContext wikiContext)
  {
    GWikiElement el = wikiContext.getWikiWeb().findElement(rootPageId);
    renderPages(wikiContext, el, 0);

  }

  public void renderPages(GWikiContext wikiContext, GWikiElement el, int offset)
  {
    if (wikiContext.getWikiWeb().getAuthorization().isAllowToView(wikiContext, el.getElementInfo()) == false) {
      return;
    }
    if (offset != 0) {
      wikiContext.append("<h").append(offset).append(">") //
          .append(StringEscapeUtils.escapeHtml(wikiContext.getTranslatedProp(el.getElementInfo().getTitle())))//
          .append("</h").append(offset).append(">\n");
    }
    wikiContext.pushWikiElement(el);
    try {
      el.prepareHeader(wikiContext);
      el.serve(wikiContext);
      wikiContext.flush();
    } finally {
      wikiContext.popWikiElement();
    }
    List<GWikiElementInfo> childs = wikiContext.getElementFinder().getPageDirectPages(el.getElementInfo().getId());
    Collections.sort(childs,
        new GWikiElementByChildOrderComparator(new GWikiElementByOrderComparator(new GWikiElementByIntPropComparator(
            "ORDER", 0))));
    wikiContext.setRequestAttribute(GWikiFragmentHeading.GWIKI_LAST_HEADING_LEVEL, offset);
    for (GWikiElementInfo ei : childs) {
      GWikiElement ce = wikiContext.getWikiWeb().getElement(ei);
      // wikiContext.append("<h1>").append(StringEscapeUtils.escapeHtml(wikiContext.getTranlatedProp(ei.getTitle()))).append("</h1>\n");

      renderPages(wikiContext, ce, offset + 1);
      wikiContext.flush();
    }
  }

  public String getRootPageId()
  {
    return rootPageId;
  }

  public void setRootPageId(String rootPageId)
  {
    this.rootPageId = rootPageId;
  }

  public String getDocTree()
  {
    return docTree;
  }

  public void setDocTree(String docTree)
  {
    this.docTree = docTree;
  }

  public String getDocumentTemplate()
  {
    return documentTemplate;
  }

  public void setDocumentTemplate(String documentTemplate)
  {
    this.documentTemplate = documentTemplate;
  }

  public String getFormat()
  {
    return format;
  }

  public void setFormat(String format)
  {
    this.format = format;
  }
}
