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

package de.micromata.genome.gwiki.utils.html;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.ArrayStack;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.filters.DefaultFilter;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBr;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBrInLine;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHeading;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHr;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentImage;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLi;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentList;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentP;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentTable;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentTextDeco;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiNestableFragment;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.utils.StringUtils;

/**
 * Filter to transform HTML to Wiki syntax.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class Html2WikiFilter extends DefaultFilter
{

  protected GWikiWikiParserContext parseContext = new GWikiWikiParserContext();

  private Set<String> supportedHtmlTags = new HashSet<String>();

  private boolean ignoreWsNl = true;

  private ArrayStack<GWikiFragment> autoCloseTagStack = new ArrayStack<GWikiFragment>();

  private static Map<String, String> DefaultSimpleTextDecoMap = new HashMap<String, String>();
  static {

    DefaultSimpleTextDecoMap.put("b", "*");
    DefaultSimpleTextDecoMap.put("strong", "*");
    DefaultSimpleTextDecoMap.put("em", "_");
    DefaultSimpleTextDecoMap.put("i", "_");
    DefaultSimpleTextDecoMap.put("del", "-");
    DefaultSimpleTextDecoMap.put("strike", "-");
    DefaultSimpleTextDecoMap.put("sub", "~");
    DefaultSimpleTextDecoMap.put("sup", "^");
    DefaultSimpleTextDecoMap.put("u", "+");
  }

  private Map<String, String> simpleTextDecoMap = DefaultSimpleTextDecoMap;

  private List<Html2WikiTransformInfo> macroTransformer = new ArrayList<Html2WikiTransformInfo>();

  // protected boolean in
  public static String html2Wiki(String text)
  {
    Set<String> s = Collections.emptySet();
    return html2Wiki(text, s);
  }

  public static String html2Wiki(String text, Set<String> htmlMacroTags)
  {
    Html2WikiFilter nf = new Html2WikiFilter();
    nf.getSupportedHtmlTags().addAll(htmlMacroTags);
    return nf.transform(text);
  }

  public String transform(String text)
  {
    parseContext.pushFragList();
    XMLParserConfiguration parser = new HTMLConfiguration();
    parser.setProperty("http://cyberneko.org/html/properties/filters", new XMLDocumentFilter[] { this});
    XMLInputSource source = new XMLInputSource(null, null, null, new StringReader(text), "UTF-8");
    try {
      parser.parse(source);
      GWikiFragmentChildContainer cont = new GWikiFragmentChildContainer(parseContext.popFragList());
      Html2WikiFragmentVisitor visitor = new Html2WikiFragmentVisitor();
      cont.iterate(visitor);
      return cont.getSource();
      // return nf.resultText.toString();
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  protected boolean isSimpleWordDeco(String el, XMLAttributes attributes)
  {
    return simpleTextDecoMap.containsKey(el);
  }

  @SuppressWarnings("unchecked")
  protected <T> T findFragInStack(Class<T> cls)
  {
    for (int i = 0; i < parseContext.stackSize(); ++i) {
      List<GWikiFragment> fl = parseContext.peek(i);
      if (fl.size() > 0) {
        GWikiFragment lr = fl.get(fl.size() - 1);
        if (cls.isAssignableFrom(lr.getClass()) == true) {
          return (T) lr;
        }
      }
    }
    return null;
  }

  protected GWikiFragment findFragsInStack(Class< ? extends GWikiFragment>... classes)
  {
    for (Class< ? extends GWikiFragment> cls : classes) {
      GWikiFragment f = findFragInStack(cls);
      if (f != null) {
        return f;
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  protected boolean needSoftNl()
  {
    return findFragsInStack(GWikiFragmentLi.class, GWikiFragmentTable.class) != null;
  }

  protected GWikiFragment getNlFragement(GWikiFragment defaultFrag)
  {
    if (needSoftNl() == true) {
      return new GWikiFragmentBrInLine();
    }
    return defaultFrag;
  }

  protected String getListTag(String en, XMLAttributes attributes)
  {
    String tag;
    if (en.equals("ol") == true) {
      tag = "#";
    } else if (StringUtils.equals(attributes.getValue("type"), "square") == true) {
      tag = "-";
    } else {
      tag = "*";
    }
    for (int i = 0; i < parseContext.stackSize(); ++i) {
      List<GWikiFragment> fl = parseContext.peek(i);
      if (fl.size() > 0) {
        GWikiFragment lr = fl.get(fl.size() - 1);
        if (lr instanceof GWikiFragmentList) {
          GWikiFragmentList lf = (GWikiFragmentList) lr;
          tag += lf.getListTag();
          break;
        }
      }
    }
    return tag;
  }

  protected MacroAttributes convertToMaAttributes(QName element, XMLAttributes attributes)
  {
    String en = element.rawname.toLowerCase();
    MacroAttributes ma = new MacroAttributes(en);
    for (int i = 0; i < attributes.getLength(); ++i) {
      String k = attributes.getLocalName(i);
      String v = attributes.getValue(i);
      ma.getArgs().setStringValue(k, v);
    }
    return ma;
  }

  protected GWikiFragment convertToBodyMacro(QName element, XMLAttributes attributes, int macroRenderModes)
  {
    MacroAttributes ma = convertToMaAttributes(element, attributes);
    return new GWikiMacroFragment(new GWikiHtmlBodyTagMacro(macroRenderModes), ma);
  }

  protected GWikiFragment convertToEmptyMacro(QName element, XMLAttributes attributes)
  {
    MacroAttributes ma = convertToMaAttributes(element, attributes);
    return new GWikiMacroFragment(new GWikiHtmlTagMacro(), ma);
  }

  protected void parseLink(XMLAttributes attributes)
  {
    // if (StringUtils.isNotEmpty(attributes.getValue("wikitarget")) == true) {
    // parseContext.addFragment(new GWikiFragementLink(attributes.getValue("wikitarget")));
    // return;
    // }
    String href = attributes.getValue("href");
    GWikiContext wikiContext = GWikiContext.getCurrent();
    if (href != null && wikiContext != null) {
      String ctxpath = wikiContext.getRequest().getContextPath();
      if (href.startsWith(ctxpath) == true) {
        String id = href;
        if (ctxpath.length() > 0) {
          id = href.substring(ctxpath.length() + 1);
        }
        GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(id);
        // if (ei != null) {
        // TODO title and other attributes...
        parseContext.addFragment(new GWikiFragmentLink(id));
        return;
        // }
      }
    }
    if (href == null) {
      href = "";
    }
    parseContext.addFragment(new GWikiFragmentLink(href));
  }

  protected void finalizeLink()
  {
    List<GWikiFragment> frags = parseContext.popFragList();
    GWikiFragmentLink lf = (GWikiFragmentLink) parseContext.lastFragment();
    // if (wasForeignLink == true) {
    lf.addChilds(frags);
    // }
  }

  protected void parseImage(XMLAttributes attributes)
  {
    String source = attributes.getValue("src");
    if (source == null) {
      return;
    }
    GWikiContext wikiContext = GWikiContext.getCurrent();
    if (wikiContext != null) {
      String ctxpath = wikiContext.getRequest().getContextPath();
      if (source.startsWith(ctxpath) == true) {
        source = source.substring(ctxpath.length() + 1);
      }
    }
    GWikiFragmentImage image = new GWikiFragmentImage(source);
    if (StringUtils.isNotEmpty(attributes.getValue("alt")) == true) {
      image.setAlt(attributes.getValue("alt"));
    }
    if (StringUtils.isNotEmpty(attributes.getValue("width")) == true) {
      image.setWidth(attributes.getValue("width"));
    }
    if (StringUtils.isNotEmpty(attributes.getValue("height")) == true) {
      image.setHeight(attributes.getValue("height"));
    }
    if (StringUtils.isNotEmpty(attributes.getValue("border")) == true) {
      image.setBorder(attributes.getValue("border"));
    }
    if (StringUtils.isNotEmpty(attributes.getValue("hspace")) == true) {
      image.setHspace(attributes.getValue("hspace"));
    }
    if (StringUtils.isNotEmpty(attributes.getValue("vspace")) == true) {
      image.setVspace(attributes.getValue("vspace"));
    }
    if (StringUtils.isNotEmpty(attributes.getValue("class")) == true) {
      image.setStyleClass(attributes.getValue("class"));
    }
    if (StringUtils.isNotEmpty(attributes.getValue("style")) == true) {
      image.setStyle(attributes.getValue("style"));
    }
    parseContext.addFragment(image);
  }

  protected void createTable(QName element, XMLAttributes attributes)
  {
    GWikiFragment frag;
    if (attributes.getLength() == 0
        || (attributes.getLength() == 1 && StringUtils.equals(attributes.getValue("class"), "gwikiTable") == true)) {
      frag = new GWikiFragmentTable();
    } else {
      frag = convertToBodyMacro(element, attributes, GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NewLineAfterStart,
          GWikiMacroRenderFlags.NewLineBeforeEnd, GWikiMacroRenderFlags.TrimTextContent));
    }
    parseContext.addFragment(frag);
    parseContext.pushFragList();
  }

  protected void endTable()
  {
    List<GWikiFragment> frags = parseContext.popFragList();
    GWikiFragment top = parseContext.lastDefinedFragment();
    if (top instanceof GWikiMacroFragment) {
      GWikiMacroFragment bm = (GWikiMacroFragment) top;
      bm.addChilds(frags);
    } else {
      // nothing
    }
  }

  protected void copyAttributes(MacroAttributes target, XMLAttributes attributes)
  {
    for (int i = 0; i < attributes.getLength(); ++i) {
      target.getArgs().setStringValue(attributes.getQName(i), attributes.getValue(i));
    }
  }

  protected void createTr(QName element, XMLAttributes attributes)
  {
    GWikiFragment top = parseContext.lastDefinedFragment();

    if (top instanceof GWikiFragmentTable) {
      GWikiFragmentTable table = (GWikiFragmentTable) top;
      GWikiFragmentTable.Row row = new GWikiFragmentTable.Row();
      copyAttributes(row.getAttributes(), attributes);
      table.addRow(row);
      parseContext.pushFragList();
      return;
    }
    GWikiFragment frag = convertToBodyMacro(element, attributes, GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NewLineAfterStart,
        GWikiMacroRenderFlags.NewLineBeforeEnd, GWikiMacroRenderFlags.TrimTextContent));
    parseContext.addFragment(frag);
    parseContext.pushFragList();
  }

  protected void endTr()
  {
    List<GWikiFragment> frags = parseContext.popFragList();
    GWikiFragment top = parseContext.lastDefinedFragment();
    if (top instanceof GWikiMacroFragment) {
      GWikiMacroFragment bm = (GWikiMacroFragment) top;
      bm.addChilds(frags);
    } else {
      // nothing
    }
  }

  protected void createThTd(QName element, XMLAttributes attributes)
  {

    String en = element.rawname.toLowerCase();
    GWikiFragment lfrag = parseContext.lastDefinedFragment();
    if (lfrag instanceof GWikiFragmentTable) {
      GWikiFragmentTable table = (GWikiFragmentTable) lfrag;
      GWikiFragmentTable.Cell cell = table.addCell(en);
      copyAttributes(cell.getAttributes(), attributes);
      parseContext.pushFragList();
      return;

    }
    GWikiFragment frag = convertToBodyMacro(element, attributes, 0);
    parseContext.addFragment(frag);
    parseContext.pushFragList();
  }

  protected void endTdTh()
  {
    List<GWikiFragment> frags = parseContext.popFragList();
    GWikiFragment lf = parseContext.lastDefinedFragment();
    if (lf instanceof GWikiFragmentTable) {
      GWikiFragmentTable table = (GWikiFragmentTable) lf;
      table.addCellContent(frags);
    } else if (lf instanceof GWikiMacroFragment) {
      GWikiMacroFragment mf = (GWikiMacroFragment) lf;
      mf.addChilds(frags);
    }
  }

  protected boolean hasPreviousBr()
  {
    // last character field has br
    if (parseContext.lastFragment() instanceof GWikiFragmentBr) {
      return true;
    }
    return false;
  }

  protected boolean handleMacroTransformer(String tagName, XMLAttributes attributes, boolean withBody)
  {
    for (Html2WikiTransformInfo ma : macroTransformer) {
      if (ma.match(tagName, attributes, withBody) == true) {
        GWikiFragment frag = ma.handleMacroTransformer(tagName, attributes, withBody);
        autoCloseTagStack.push(frag);
        parseContext.addFragment(frag);
        parseContext.pushFragList();
        return true;
      }
    }
    return false;
  }

  protected boolean handleAutoCloseTag(String tagName)
  {
    if (autoCloseTagStack.isEmpty() == true) {
      return false;
    }
    if (autoCloseTagStack.peek() != parseContext.lastParentFrag()) {
      return false;
    }
    GWikiFragment pfrag = autoCloseTagStack.pop();
    List<GWikiFragment> cfrags = parseContext.popFragList();
    if (pfrag instanceof GWikiNestableFragment) {
      ((GWikiNestableFragment) pfrag).addChilds(cfrags);
    }
    return true;
  }

  @Override
  public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException
  {
    String en = element.rawname.toLowerCase();
    if (en.equals("br") == true) {
      parseContext.addFragment(getNlFragement(new GWikiFragmentBr()));
    } else if (en.equals("p") == true) {
      parseContext.addFragment(getNlFragement(new GWikiFragmentP()));
    } else if (en.equals("hr") == true) {
      parseContext.addFragment(new GWikiFragmentHr());
    } else if (en.equals("img") == true) {
      parseImage(attributes);
    } else if (en.equals("a") == true) {
      // TODO gwiki anchor
    } else if (supportedHtmlTags.contains(en) == true) {
      parseContext.addFragment(convertToEmptyMacro(element, attributes));
    } else {
      // hmm
    }
    super.emptyElement(element, attributes, augs);
  }

  public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException
  {
    // todo <span style="font-family: monospace;">sadf</span> {{}}
    String en = element.rawname.toLowerCase();

    Html2WikiElement el = null;
    if (handleMacroTransformer(en, attributes, true) == true) {
      ; // nothing more
    } else if (en.equals("p") == true) {
      // all p should always be a br, but tinyMCE encodes center images as p style=text-align: center;

    } else if (en.length() == 2 && en.charAt(0) == 'h' && Character.isDigit(en.charAt(1)) == true) {
      parseContext.addFragment(new GWikiFragmentHeading(Integer.parseInt("" + en.charAt(1))));
      parseContext.pushFragList();
    } else if (en.equals("ul") == true || en.equals("ol")) {
      parseContext.addFragment(new GWikiFragmentList(getListTag(en, attributes)));
      parseContext.pushFragList();
    } else if (en.equals("li") == true) {
      parseContext.addFragment(new GWikiFragmentLi(findFragInStack(GWikiFragmentList.class)));
      parseContext.pushFragList();
    } else if (isSimpleWordDeco(en, attributes) == true) {
      parseContext.pushFragList();
    } else if (en.equals("a") == true) {
      parseLink(attributes);
      parseContext.pushFragList();
    } else if (en.equals("table") == true) {
      createTable(element, attributes);
    } else if (en.equals("tr") == true) {
      createTr(element, attributes);
    } else if (en.equals("th") == true) {
      createThTd(element, attributes);
    } else if (en.equals("td") == true) {
      createThTd(element, attributes);
    } else {
      if (supportedHtmlTags.contains(en) == true) {
        parseContext.addFragment(convertToBodyMacro(element, attributes, 0));
        parseContext.pushFragList();
      }
    }
    super.startElement(element, attributes, augs);
  }

  public void endElement(QName element, Augmentations augs) throws XNIException
  {
    List<GWikiFragment> frags;
    String en = element.rawname.toLowerCase();
    if (handleAutoCloseTag(en) == true) {
      ; // nothing
    } else if (en.length() == 2 && en.charAt(0) == 'h' && Character.isDigit(en.charAt(1)) == true) {
      frags = parseContext.popFragList();
      GWikiFragmentHeading lfh = (GWikiFragmentHeading) parseContext.lastFragment();
      lfh.addChilds(frags);
    } else if (en.equals("p") == true) {
      if (hasPreviousBr() == false) {
        parseContext.addFragment(getNlFragement(new GWikiFragmentP()));
      } else {
        parseContext.addFragment(getNlFragement(new GWikiFragmentBr()));
      }
    } else if (en.equals("ul") == true || en.equals("ol") == true) {
      frags = parseContext.popFragList();
      GWikiFragmentList lf = (GWikiFragmentList) parseContext.lastFragment();
      lf.addChilds(frags);
    } else if (en.equals("li") == true) {
      frags = parseContext.popFragList();
      GWikiFragmentLi li = (GWikiFragmentLi) parseContext.lastFragment();
      li.addChilds(frags);
    } else if (isSimpleWordDeco(en, null) == true) {
      frags = parseContext.popFragList();
      parseContext.addFragment(new GWikiFragmentTextDeco(simpleTextDecoMap.get(en).charAt(0), "<" + en + ">", "</" + en + ">", frags));
    } else if (en.equals("img") == true) {

    } else if (en.equals("a") == true) {
      finalizeLink();

    } else if (en.equals("table") == true) {
      endTable();
    } else if (en.equals("tr") == true) {
      endTr();
    } else if (en.equals("th") == true) {
      endTdTh();
    } else if (en.equals("td") == true) {
      endTdTh();
    } else if (supportedHtmlTags.contains(en) == true) {
      frags = parseContext.popFragList();
      GWikiMacroFragment maf = (GWikiMacroFragment) parseContext.lastFragment();
      maf.getAttrs().setChildFragment(new GWikiFragmentChildContainer(frags));
    }
    super.endElement(element, augs);
  }

  public void characters(XMLString text, Augmentations augs) throws XNIException
  {
    String t = text.toString();
    if (ignoreWsNl == true) {
      String s = StringUtils.trim(t);
      if (StringUtils.isBlank(s) || StringUtils.isNewLine(s)) {
        super.characters(text, augs);
        return;
      }
    }
    int cp = Character.codePointAt(t.toCharArray(), 0);
    if (t.startsWith("<!--") == true) {
      super.characters(text, augs);
      return;
    }
    if (StringUtils.isNewLine(t) == false) {
      parseContext.addTextFragement(t);
    }

    super.characters(text, augs);
  }

  public Set<String> getSupportedHtmlTags()
  {
    return supportedHtmlTags;
  }

  public void setSupportedHtmlTags(Set<String> supportedHtmlTags)
  {
    this.supportedHtmlTags = supportedHtmlTags;
  }

  public List<Html2WikiTransformInfo> getMacroTransformer()
  {
    return macroTransformer;
  }

  public void setMacroTransformer(List<Html2WikiTransformInfo> macroTransformer)
  {
    this.macroTransformer = macroTransformer;
  }

  public Map<String, String> getSimpleTextDecoMap()
  {
    return simpleTextDecoMap;
  }

  public void setSimpleTextDecoMap(Map<String, String> simpleTextDecoMap)
  {
    this.simpleTextDecoMap = simpleTextDecoMap;
  }

}
