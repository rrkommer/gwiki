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
import org.apache.commons.lang.ArrayUtils;
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
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRte;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBr;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBrInLine;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentFixedFont;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHeading;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHr;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentImage;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLi;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentList;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentP;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentTable;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentTextDeco;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiNestableFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiSimpleFragmentVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiTextFormatMacro;
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

  /**
   * Tags, which may not be closed in HTML code.
   */
  private String[] autoCloseTags = new String[] { "hr", "br" };

  private static final String DEFAULT_SPECIAL_CHARACTERS = "*-_~^+{}[]!#|\\";

  /**
   * Character, which has to be escaped.
   */
  private String specialCharacters = DEFAULT_SPECIAL_CHARACTERS;

  private boolean ignoreWsNl = true;

  private ArrayStack<GWikiFragment> autoCloseTagStack = new ArrayStack<GWikiFragment>();

  protected ArrayStack<String> liStack = new ArrayStack<String>();

  public static Map<String, String> DefaultSimpleTextDecoMap = new HashMap<String, String>();

  public static Map<String, String> DefaultWiki2HtmlTextDecoMap = new HashMap<String, String>();

  public static Map<String, GWikiMacroFactory> TextDecoMacroFactories = new HashMap<String, GWikiMacroFactory>();
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
    for (Map.Entry<String, String> me : DefaultSimpleTextDecoMap.entrySet()) {
      DefaultWiki2HtmlTextDecoMap.put(me.getValue(), me.getKey());
      TextDecoMacroFactories.put(me.getValue(), new GWikiMacroClassFactory(GWikiTextFormatMacro.class));
    }
  }

  private Map<String, String> simpleTextDecoMap = DefaultSimpleTextDecoMap;

  private List<Html2WikiTransformer> macroTransformer = new ArrayList<Html2WikiTransformer>();

  /**
   * because character will be intercepted if an entity like &auml; is in the character text, this will be used to
   * collect all characters before parsing it.
   */
  protected StringBuilder collectedText = new StringBuilder();

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
    parser.setProperty("http://cyberneko.org/html/properties/filters", new XMLDocumentFilter[] { this });
    text = StringUtils.defaultString(text);
    XMLInputSource source = new XMLInputSource(null, null, null, new StringReader(text), "UTF-8");
    try {
      parser.parse(source);
      GWikiFragmentChildContainer cont = new GWikiFragmentChildContainer(parseContext.popFragList());
      GWikiFragmentVisitor visitor = createVisitor();
      cont.iterate(visitor);
      return cont.getSource();
      // return nf.resultText.toString();
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  protected GWikiFragmentVisitor createVisitor()
  {
    return new Html2WikiFragmentVisitor();
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

  protected GWikiFragment findFragsInStack(Class<? extends GWikiFragment>... classes)
  {
    for (Class<? extends GWikiFragment> cls : classes) {
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
    if (liStack.isEmpty() == true) {
      return tag;
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
        if (id.startsWith("/") == true) {
          id = id.substring(1);
        }
        GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(id);
        if (ei == null) {
          id = href;
        }
        GWikiFragmentLink link = new GWikiFragmentLink(id);
        if (StringUtils.isNotBlank(attributes.getValue("title")) == true) {
          link.setTitle(attributes.getValue("title"));
        }
        if (StringUtils.isNotBlank(attributes.getValue("target")) == true) {
          link.setWindowTarget(attributes.getValue("target"));
        }
        if (StringUtils.isNotBlank(attributes.getValue("class")) == true) {
          link.setLinkClass(attributes.getValue("class"));
        }
        parseContext.addFragment(link);
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
      if (StringUtils.isNotEmpty(ctxpath) && source.startsWith(ctxpath) == true) {
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
      frag = convertToBodyMacro(element, attributes,
          GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NewLineAfterStart,
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
    GWikiFragment frag = convertToBodyMacro(element, attributes,
        GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NewLineAfterStart,
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

  private boolean isAutoCloseTag(String tagName)
  {
    if (ArrayUtils.contains(autoCloseTags, tagName) == true) {
      return true;
    }
    return false;
  }

  protected boolean handleMacroTransformerBegin(String tagName, XMLAttributes attributes, boolean withBody)
  {
    for (Html2WikiTransformer ma : macroTransformer) {
      if (ma.match(tagName, attributes, withBody) == true) {
        GWikiFragment frag = ma.handleMacroTransformer(tagName, attributes, withBody);
        if (frag != null) {
          if (isAutoCloseTag(tagName) == true) {
            autoCloseTagStack.push(frag);
          }
          parseContext.addFragment(frag);
          parseContext.pushFragList();
        }
        
        return true;
      }
    }
    return false;
  }

  protected boolean handleMacroTransformerEnd(QName element, Augmentations augs)
  {
    String en = element.rawname.toLowerCase();
    GWikiFragment lpf = parseContext.lastParentFrag();
    if (lpf instanceof GWikiMacroFragment) {
      GWikiMacroFragment lpfm = (GWikiMacroFragment) lpf;

      if (lpfm.getMacro() instanceof GWikiMacroRte) {
        GWikiMacroRte mr = (GWikiMacroRte) lpfm.getMacro();
        if (mr.getTransformInfo() != null) {
          String t = collectedText.toString();
          if (mr.getTransformInfo() != null && StringUtils.equals(mr.getTransformInfo().getTagName(), en) == true) {
            List<GWikiFragment> children = parseContext.popFragList();
            mr.getTransformInfo().handleMacroEnd(en, lpfm, children, t);
            collectedText.setLength(0);
            return true;
          }
        }

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
    flushText();
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

  protected boolean handleSpanStart(QName element, XMLAttributes attributes)
  {
    String value = attributes.getValue("style");
    if (StringUtils.equals(value, "font-family: courier new,courier,monospace;") == true
        || StringUtils.equals(value, "font-family: courier new,courier;") == true) {
      parseContext.addFragment(new GWikiFragmentFixedFont(new ArrayList<GWikiFragment>()));
      parseContext.pushFragList();
      return true;
    }
    return false;
  }

  protected boolean handleSpanEnd()
  {
    if (parseContext.getFrags().size() >= 2) {
      List<GWikiFragment> fl = parseContext.getFrags().get(parseContext.getFrags().size() - 2);
      if (fl.size() > 0 && fl.get(fl.size() - 1) instanceof GWikiFragmentFixedFont) {
        GWikiFragmentFixedFont ff = (GWikiFragmentFixedFont) fl.get(fl.size() - 1);
        ff.addChilds(parseContext.popFragList());
        return true;
      }
    }
    return false;
  }

  @Override
  public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException
  {
    flushText();
    // todo <span style="font-family: monospace;">sadf</span> {{}}
    String en = element.rawname.toLowerCase();

    Html2WikiElement el = null;
    if (handleMacroTransformerBegin(en, attributes, true) == true) {
      ; // nothing more
    } else if (en.equals("p") == true) {
      // all p should always be a br, but tinyMCE encodes center images as p style=text-align: center;

    } else if (en.length() == 2 && en.charAt(0) == 'h' && Character.isDigit(en.charAt(1)) == true) {
      parseContext.addFragment(new GWikiFragmentHeading(Integer.parseInt("" + en.charAt(1))));
      parseContext.pushFragList();
    } else if (en.equals("ul") == true || en.equals("ol") == true) {
      parseContext.addFragment(new GWikiFragmentList(getListTag(en, attributes)));
      liStack.push(en);
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
    } else if (en.equals("span") == true && handleSpanStart(element, attributes) == true) {
      // nothing

    } else {
      if (supportedHtmlTags.contains(en) == true) {
        parseContext.addFragment(convertToBodyMacro(element, attributes, 0));
        parseContext.pushFragList();
      }
    }
    
  }

  @SuppressWarnings("deprecation")
  private boolean requireTextDecoMacroSyntax(final GWikiFragmentTextDeco fragDeco)
  {
    fragDeco.iterate(new GWikiSimpleFragmentVisitor()
    {

      @Override
      public void begin(GWikiFragment fragment)
      {
        if (fragDeco.isRequireMacroSyntax() == true) {
          return;
        }
        if (fragment instanceof GWikiFragmentP || fragment instanceof GWikiFragmentBr) {
          fragDeco.setRequireMacroSyntax(true);
        }
      }
    });
    if (fragDeco.isRequireMacroSyntax() == true) {
      return true;
    }
    GWikiFragment lf = parseContext.lastFrag();
    if (lf == null) {
      return false;
    }
    if ((lf instanceof GWikiFragmentText) == false) {
      return false;
    }
    GWikiFragmentText tl = (GWikiFragmentText) lf;
    String source = tl.getSource();
    if (StringUtils.isEmpty(source) == true) {
      return false;
    }
    char lc = source.charAt(source.length() - 1);
    if (Character.isSpace(lc) == false) {
      return true;
    }
    return false;
  }

  @Override
  public void endElement(QName element, Augmentations augs) throws XNIException
  {
    flushText();
    String en = element.rawname.toLowerCase();
    if (handleMacroTransformerEnd(element, augs) == true) {
      super.endElement(element, augs);
      return;
    }
    List<GWikiFragment> frags;

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
      if (liStack.isEmpty() == false && liStack.peek().equals(en) == true) {
        liStack.pop();
      }
      frags = parseContext.popFragList();
      GWikiFragmentList lf = (GWikiFragmentList) parseContext.lastFragment();
      lf.addChilds(frags);
    } else if (en.equals("li") == true) {
      frags = parseContext.popFragList();
      GWikiFragmentLi li = (GWikiFragmentLi) parseContext.lastFragment();
      li.addChilds(frags);
    } else if (isSimpleWordDeco(en, null) == true) {
      frags = parseContext.popFragList();
      GWikiFragmentTextDeco fragDeco = new GWikiFragmentTextDeco(simpleTextDecoMap.get(en).charAt(0), "<" + en + ">",
          "</" + en + ">",
          frags);
      fragDeco.setRequireMacroSyntax(requireTextDecoMacroSyntax(fragDeco));
      parseContext.addFragment(fragDeco);
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
    } else if (en.equals("span") == true && handleSpanEnd() == true) {
      // nothing

    } else if (supportedHtmlTags.contains(en) == true) {
      frags = parseContext.popFragList();
      GWikiMacroFragment maf = (GWikiMacroFragment) parseContext.lastFragment();
      if (maf != null) {
        maf.getAttrs().setChildFragment(new GWikiFragmentChildContainer(frags));
      } else {
        throw new RuntimeException("No fragment set on end tag: " + en);
      }
    } else {

      // GWikiLog.note("Unhandled end tag: " + en);
    }
    super.endElement(element, augs);
  }

  /**
   * Take a string and return escaped wiki text.
   * 
   * @param t string
   * @return escaped version of string. if t is null, returns null.
   */
  public static String escapeWiki(String t)
  {

    return escapeWiki(t, DEFAULT_SPECIAL_CHARACTERS);
  }

  public static String escapeWiki(String t, String specialCharacters)
  {
    if (t == null) {
      return t;
    }
    StringBuilder sb = null;
    boolean insideMacro = false;
    for (int i = 0; i < t.length(); ++i) {
      char c = t.charAt(i);
      if (insideMacro == true && c == '}') {
        insideMacro = false;
        if (sb != null) {
          sb.append(c);
        }
        continue;
      }
      if (insideMacro == false && c == '{') {
        insideMacro = true;
      }
      if (insideMacro == false && specialCharacters.indexOf(c) != -1) {

        if (sb == null) {
          sb = new StringBuilder();
          if (i > 0) {
            sb.append(t.substring(0, i));
          }
        }
        sb.append("\\").append(c);
      } else {
        if (sb != null) {
          sb.append(c);
        }
      }
    }
    if (sb != null) {
      return sb.toString();
    }
    return t;
  }

  protected String escapeText(String t)
  {
    return escapeWiki(t, specialCharacters);
  }

  /**
   * Takes wiki text and returns a escaped String
   * 
   * @param wiki
   * @return null if result is empty
   */
  public static String unescapeWiki(String wiki)
  {
    return unescapeWiki(wiki, DEFAULT_SPECIAL_CHARACTERS);
  }

  /**
   * Takes wiki text and returns a escaped String
   * 
   * @param wiki
   * @return null if result is empty
   */
  public static String unescapeWiki(String wiki, String speacialCharacters)
  {
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < (wiki.length() - 1); i++) {
      char curr = wiki.charAt(i);
      char lookAhead = wiki.charAt(i + 1);

      if (curr == '\\' && speacialCharacters.indexOf(lookAhead) != -1) {
        // ignore '\'
        continue;
      } else {
        result.append(curr);
      }
    }

    // append last
    if (wiki.charAt(wiki.length() - 1) != '\\') {
      result.append(wiki.charAt(wiki.length() - 1));
    }

    if (result.length() > 1) {
      return result.toString();
    }
    return null;
  }

  protected void flushText()
  {
    if (collectedText.length() == 0) {
      return;
    }
    String t = collectedText.toString();
    GWikiFragment lpf = parseContext.lastParentFrag();
    GWikiFragment lf = parseContext.lastFrag();

    if (lpf != null && lpf instanceof GWikiMacroFragment) {
      GWikiMacroFragment gwmf = (GWikiMacroFragment) lpf;
      if (gwmf.getMacro() instanceof GWikiBodyMacro && (gwmf.getMacro() instanceof GWikiBodyEvalMacro) == false) {
        // GWikiBodyMacro bm = (GWikiBodyMacro)gwmf.getMacro();
        // gwmf.getAttrs().setBody(t);
        // just continue
        return;
      }
    }
    collectedText.setLength(0);
    if (t.length() > 0 && Character.isWhitespace(t.charAt(0)) == false) {
      if (lf instanceof GWikiFragmentTextDeco) {
        ((GWikiFragmentTextDeco) lf).setRequireMacroSyntax(true);
      }
    }
    if (ignoreWsNl == true) {
      String s = StringUtils.trim(t);
      if (StringUtils.isBlank(s) || StringUtils.isNewLine(s)) {
        return;
      }
    }
    // int cp = Character.codePointAt(t.toCharArray(), 0);
    if (t.startsWith("<!--") == true) {
      return;
    }
    if (StringUtils.isNewLine(t) == false) {
      parseContext.addTextFragement(escapeText(t));
    }
  }

  @Override
  public void characters(XMLString text, Augmentations augs) throws XNIException
  {
    String t = text.toString();
    if (t.startsWith("<!--") == true) {
      super.characters(text, augs);
      return;
    }
    collectedText.append(t);
    // if (t.length() > 0 && Character.isWhitespace(t.charAt(0)) == false) {
    // GWikiFragment lf = parseContext.lastFrag();
    // if (lf instanceof GWikiFragmentTextDeco) {
    // ((GWikiFragmentTextDeco) lf).setRequireMacroSyntax(true);
    // }
    // }
    // if (ignoreWsNl == true) {
    // String s = StringUtils.trim(t);
    // if (StringUtils.isBlank(s) || StringUtils.isNewLine(s)) {
    // super.characters(text, augs);
    // return;
    // }
    // }
    // // int cp = Character.codePointAt(t.toCharArray(), 0);
    //
    // if (StringUtils.isNewLine(t) == false) {
    // parseContext.addTextFragement(escapeText(t));
    // }

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

  public List<Html2WikiTransformer> getMacroTransformer()
  {
    return macroTransformer;
  }

  public void setMacroTransformer(List<Html2WikiTransformer> macroTransformer)
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

  public String[] getAutoCloseTags()
  {
    return autoCloseTags;
  }

  public void setAutoCloseTags(String[] autoCloseTags)
  {
    this.autoCloseTags = autoCloseTags;
  }

  public ArrayStack<GWikiFragment> getAutoCloseTagStack()
  {
    return autoCloseTagStack;
  }

  public void setAutoCloseTagStack(ArrayStack<GWikiFragment> autoCloseTagStack)
  {
    this.autoCloseTagStack = autoCloseTagStack;
  }

  public String getSpecialCharacters()
  {
    return specialCharacters;
  }

  public void setSpecialCharacters(String specialCharacters)
  {
    this.specialCharacters = specialCharacters;
  }

}
