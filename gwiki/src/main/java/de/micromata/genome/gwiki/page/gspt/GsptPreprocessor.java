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

package de.micromata.genome.gwiki.page.gspt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.LoopTag;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.SimpleTag;
import javax.servlet.jsp.tagext.TagInfo;

import de.micromata.genome.util.types.Converter;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GsptPreprocessor
{
  private Map<String, Class< ? >> tagLibs = new HashMap<String, Class< ? >>();

  private List<Replacer> replList = new ArrayList<Replacer>();

  private List<String> prefixList = new ArrayList<String>();

  private Map<String, Object> context;

  private boolean servletApi24 = false;

  private String fileName;

  // TODO genome, das macht attribute mit mehrzeilen kaputt!
  private boolean emitLineNos = false;

  private final boolean evaluateELViaGroovy;

  private boolean genGsptClass = true;

  public GsptPreprocessor(String fileName, Map<String, Object> context, boolean servletApi24, boolean evaluateELViaGroovy)
  {
    this.fileName = fileName;
    this.context = context;
    this.servletApi24 = servletApi24;
    this.evaluateELViaGroovy = evaluateELViaGroovy;
  }

  public GsptPreprocessor(String fileName, Map<String, Object> context, boolean servletApi24)
  {
    this(fileName, context, servletApi24, false);
  }

  public PageContext getPageContext()
  {
    return (PageContext) context.get("pageContext");
  }

  public void addTagLib(String prefix, TagInfo tagInfo)
  {
    String tagName = ((prefix != null) ? (prefix + ":") : "") + tagInfo.getTagName();

    addTagLib(tagName, tagInfo.getTagClassName());
  }

  public void addTagLib(String tagName, String className)
  {
    try {
      Class< ? > cls = Thread.currentThread().getContextClassLoader().loadClass(className);
      tagLibs.put(tagName, cls);
      if (IterationTag.class.isAssignableFrom(cls) == true || LoopTag.class.isAssignableFrom(cls) == true) {
        replList.add(new BodyTagReplacer(tagName, cls, evaluateELViaGroovy));
        replList.add(new BodyTagEndReplacer(tagName, cls, evaluateELViaGroovy));
      } else if (SimpleTag.class.isAssignableFrom(cls) == true) {
        replList.add(new SimpleSimpleTagReplacer(tagName, cls, evaluateELViaGroovy));
        replList.add(new SimpleSimpleTagEndReplacer(tagName, cls, evaluateELViaGroovy));
      } else {
        replList.add(new SimpleTagReplacer(tagName, cls, evaluateELViaGroovy));
      }

    } catch (Exception ex) {
      /**
       * @logging
       * @reason Eine Taglibklasse kann nicht gefunden werden
       * @action Entwickler kontaktieren
       * 
       */
      throw new RuntimeException("Gspt; Cannot find taglib: " + tagName + "; className: " + className, ex);
    }
  }

  public String processSimple(SimpleReplacer replacer, String content)
  {
    return content.replace(replacer.getStart(), replacer.getReplace());
  }

  static Pattern attrPattern = Pattern.compile("\\s*([\\w-]+)=([\"'?])", Pattern.MULTILINE + Pattern.DOTALL);

  private String parseAttribute(String b, Map<String, String> attrs)
  {
    Matcher m = attrPattern.matcher(b);
    boolean found = m.find();
    if (found == false)
      return null;
    String n = m.group(1);
    String s = m.group(2);
    char qchar = s.charAt(0);
    int start = m.end();
    int end = -1;
    for (int i = start; i < b.length(); ++i) {
      char c = b.charAt(i);
      if (c == '\\') {
        ++i;
        continue;
      }
      if (qchar == c) {
        end = i;
        break;
      }
    }
    if (end == -1)
      return null;
    attrs.put(n, b.substring(start, end));
    return b.substring(end + 1);

  }

  private boolean checkContinuedTag(String s)
  {
    if (s.length() == 0)
      return false;
    return Character.isJavaIdentifierPart(s.charAt(0));
  }

  int skipRtAttributes(String rs)
  {
    int ei = rs.indexOf('>');
    int offset = 0;
    do {

      int rtbegin = rs.indexOf("<%=");
      if (rtbegin == -1 || rtbegin > ei) {
        break;
      }
      int rtend = rs.indexOf("%>");
      rs = rs.substring(rtend + 2);
      offset += rtend + 2;
      ei = rs.indexOf('>');
      if (ei == -1)
        break;
      // offset += tei;
    } while (ei != -1);
    return offset + ei;
  }

  public String process(ReplacerContext ctx, Replacer replacer, String content)
  {
    if (replacer instanceof SimpleReplacer)
      return processSimple((SimpleReplacer) replacer, content);
    String orgContent = content;
    String start = replacer.getStart();
    String end = replacer.getEnd();
    Pattern startP = null;
    Pattern endP = null;
    if (replacer instanceof RegExpReplacer) {
      RegExpReplacer regExpReplacer = (RegExpReplacer) replacer;
      startP = regExpReplacer.getStartPattern();
      endP = regExpReplacer.getEndPattern();
    }
    StringBuilder sb = new StringBuilder();
    do {

      boolean isClosed = false;
      String attrString = null;
      String lrest;
      String rrest;
      int si = -1;
      int ei = -1;
      if (startP != null) {
        Matcher m = startP.matcher(content);
        if (m.matches() == false)
          break;
        lrest = m.group(1);
        si = lrest.length();
        // String st = m.group(2);
        rrest = m.group(3);
        // String ss = content.substring(lrest.length() + st.length());
        m = endP.matcher(rrest);
        if (m.matches() == false)
          break;
        attrString = m.group(1);
        content = m.group(3);
      } else {
        si = content.indexOf(start);
        if (si == -1)
          break;
        lrest = content.substring(0, si);
        String ss = content.substring(si + start.length());
        if (checkContinuedTag(ss) == true) {
          sb.append(content.substring(0, si + start.length()));
          content = ss;
          continue;
        }
        ei = ss.indexOf(end);
        if (ei == -1)
          break;

        if (end.equals(">") == true && ss.length() > 1 && ei >= 1) {
          ei = skipRtAttributes(ss);
          char be = ss.substring(ei - 1, ei).charAt(0);
          if (be == '/')
            isClosed = true;
        }
        attrString = ss.substring(0, ei);
        rrest = content.substring(si + start.length() + ei + end.length());
        content = content.substring(si + start.length() + ei + end.length());
      }
      // String
      Map<String, String> attrs = new HashMap<String, String>();
      do {

        attrString = parseAttribute(attrString, attrs);
        if (attrString == null)
          break;
      } while (true);
      // sb.append(content.substring(0, si));

      String r = replacer.replace(ctx, attrs, isClosed);
      sb.append(lrest).append(r);// .append(rrest);
      // sb.append(r);

    } while (true);
    sb.append(content);
    String ret = sb.toString();

    if (replacer instanceof GWikiIncludeReplacer && ret.equals(orgContent) == false) {
      return process(ctx, replacer, ret);
    }
    return ret;
  }

  private static boolean isInComment(String textToSearchIn, int tagOccurence)
  {
    boolean inComment = false;
    int commentStart = textToSearchIn.indexOf("<%");
    while ((commentStart >= 0) && (commentStart < tagOccurence)) {
      int commentEnd = textToSearchIn.indexOf("%>", commentStart);
      if (commentEnd < 0) {
        break;
      }
      if (commentEnd > tagOccurence) {
        inComment = true;
        break;
      }
      commentStart = textToSearchIn.indexOf("<%", commentEnd);
    }
    return inComment;
  }

  public String process(ReplacerContext ctx, List<Replacer> replacer, String content)
  {
    for (int i = 0; i < replacer.size(); i++) {
      content = process(ctx, replacer.get(i), content);
    }
    boolean withAnalyse = false; // StaticDaoManager.get().isDEV()
    if (withAnalyse == true) {
      for (String prefix : prefixList) {
        int tagOccurence = 0;
        do {
          tagOccurence = content.indexOf("<" + prefix + ":", tagOccurence + 1);
          if ((tagOccurence >= 0) && (isInComment(content, tagOccurence) == false)) {
            Matcher m = Pattern.compile("<" + prefix + ":" + "[\\w-]*").matcher(content.substring(tagOccurence));
            String tagName = (m.find() == true) ? m.group() : ("<" + prefix + ":???");
            // throw new LoggedRuntimeException(LogLevel.Error, GenomeLogCategory.Coding, "unknown tag '" + tagName + " ... />'");
            throw new RuntimeException("Gspt; Unknown tag: " + tagName);
          }
        } while ((tagOccurence >= 0) && (tagOccurence + 1 < content.length()));
      }
    }
    return content;
  }

  public void useStdReplacer(Map<String, Object> context)
  {
    replList.add(new JTagLibReplacer(this));
    replList.add(new TaglibReplacer(this));
    replList.add(new GUrlReplacer());
    // replList.add(new ProcessReplacer());
    replList.add(new ReplacerReplacer(this));
    replList.add(new PageReplacer());
  }

  public void addReplacer(Replacer replacer)
  {
    replList.add(replacer);
  }

  private String addLineNos(String content)
  {
    List<String> lines = Converter.parseStringTokens(content, "\n", true);
    StringBuilder sb = new StringBuilder();
    sb.append("<% pageContext.setFileName(\"" + fileName + "\"); %>");
    int lineNo = 1;
    for (int i = 0; i < lines.size(); ++i) {
      String l = lines.get(i);
      if (l.equals("\n") == true) {
        ++lineNo;
      } else
        sb.append("<% pageContext.setLineNo(" + lineNo + "); %>");
      sb.append(l);
    }
    return sb.toString();
  }

  public String preprocess(ReplacerContext ctx, String content)
  {
    // replList.add(new IncludeReplacer(storage));
    if (emitLineNos == true)
      content = addLineNos(content);
    content = "<%# \n import " + TagSupport.class.getName() + "; %>" + content;
    return process(ctx, replList, content);
  }

  public Map<String, Object> getContext()
  {
    return context;
  }

  public boolean isServletApi24()
  {
    return servletApi24;
  }

  public void setServletApi24(boolean servletApi24)
  {
    this.servletApi24 = servletApi24;
  }

  public boolean isEmitLineNos()
  {
    return emitLineNos;
  }

  public void setEmitLineNos(boolean emitLineNos)
  {
    this.emitLineNos = emitLineNos;
  }

  public String getFileName()
  {
    return fileName;
  }

  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }

  public void addPrefixCheck(String prefix)
  {
    this.prefixList.add(prefix);
  }

  public boolean isGenGsptClass()
  {
    return genGsptClass;
  }

  public void setGenGsptClass(boolean genGsptClass)
  {
    this.genGsptClass = genGsptClass;
  }
}
