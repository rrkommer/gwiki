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

package de.micromata.genome.gwiki.page.search.expr;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FsFileObject;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.GlobalWordIndexTextArtefakt;
import de.micromata.genome.gwiki.page.search.NormalizeUtils;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.gwiki.spi.storage.GWikiFileStorage;
import de.micromata.genome.util.types.Converter;
import de.micromata.genome.util.types.Pair;

/**
 * Utils for searching.
 * 
 * @author roger
 * 
 */
public class SearchUtils
{
  public static String createLinkExpression(String text, boolean includeNoIndex, String pageType)
  {
    List<String> tokens = Converter.parseStringTokens(text, " \t", false);
    // String queryexpr = "prop:NOINDEX != true and (prop:PAGEID ~ \"" + q + "\" or prop:TITLE ~ \"" + q + "\")";
    // if (tokens.size() > 1) {
    StringBuilder sb = new StringBuilder();
    // sb.append("prop:NOINDEX != true and (");
    boolean first = true;
    for (String tk : tokens) {
      if (first == false) {
        sb.append(" or ");
      }
      first = false;
      sb.append("(").append("prop:PAGEID ~ " + escapeSearchLiteral(tk) + " or prop:TITLE ~ " + escapeSearchLiteral(tk) + ")");
    }
    String queryexpr = sb.toString();
    if (includeNoIndex == false) {
      if (StringUtils.isEmpty(queryexpr) == false) {
        queryexpr = "prop:NOINDEX != true and (" + queryexpr + ")";
      } else {
        queryexpr = "prop:NOINDEX != true";
      }
    }
    if (StringUtils.isNotBlank(pageType) == true) {
      if (StringUtils.isEmpty(queryexpr) == false) {
        queryexpr = "prop:TYPE = " + pageType + " and (" + queryexpr + ")";
      } else {
        queryexpr = "prop:TYPE = " + pageType;
      }
    }
    // System.out.println(text + ": " + queryexpr);
    return queryexpr;
  }

  public static String escapeSearchLiteral(String text)
  {

    if (StringUtils.contains(text, "\"") == false) {
      return "\"" + text + "\"";
    }
    if (text.length() == 1) {
      return text;
    }
    if (text.charAt(0) == '"' && text.charAt(text.length() - 1) == '"') {
      return text;
    }
    return "\"" + text + "\"";
  }

  public static String unescapeSearchLiteral(String text)
  {
    String ret = StringUtils.replace(text, "\\\"", "\"");
    ret = StringUtils.replace(ret, "\\\\", "\\");
    return ret;
  }

  public static String readFileContent(GWikiContext ctx, String indexFile)
  {
    GWikiFileStorage gstore = (GWikiFileStorage) ctx.getWikiWeb().getStorage();
    FsObject obj = gstore.getStorage().getFileObject(indexFile);
    if (obj == null)
      return null;
    if ((obj instanceof FsFileObject) == false)
      return null;
    FsFileObject file = (FsFileObject) obj;
    String content = file.readString();
    return content;
  }

  public static String getTextSampleNew(GWikiContext ctx, SearchResult res, List<String> words, String pageId)
  {
    int maxForward = 200;
    int maxBackwards = 200;
    String indexFile = pageId + "TextPreview.html";
    String rawText = readFileContent(ctx, indexFile);
    if (rawText == null) {
      return null;
    }
    int idx = -1;

    for (String sex : words) {
      String nsex = sex.toUpperCase();
      String nt = rawText.toUpperCase();
      idx = nt.indexOf(nsex);
      if (idx != -1)
        break;
    }
    if (idx == -1) {
      return null;
    }
    int startIdx = 0;
    if (idx > maxBackwards) {
      startIdx = idx - maxBackwards;
    }
    int endIdx = idx + maxForward;
    if (rawText.length() - 1 < idx + maxForward) {
      endIdx = rawText.length() - 1;
    }
    String sample = rawText.substring(startIdx, endIdx);
    return sample;
  }

  public static String getTextSample(GWikiContext ctx, SearchResult res, List<String> words, String pageId)
  {
    int maxForward = 200;
    int maxBackwards = 200;
    String indexFile = pageId + "TextExtract.txt";
    String rawText = readFileContent(ctx, indexFile);
    if (rawText == null) {
      return null;
    }
    int idx = -1;
    for (String sex : words) {
      String nsex = sex.toUpperCase();
      String nt = rawText.toUpperCase();
      idx = nt.indexOf(nsex);
      if (idx != -1)
        break;
    }
    if (idx == -1) {
      return null;
    }
    int startIdx = 0;
    if (idx > maxBackwards) {
      startIdx = idx - maxBackwards;
    }
    int endIdx = idx + maxForward;
    if (rawText.length() - 1 < idx + maxForward) {
      endIdx = rawText.length() - 1;
    }
    String sample = rawText.substring(startIdx, endIdx);
    return sample;
  }

  static Pattern pattern = Pattern.compile("(.*?)<\\^([0-9]+)>(.*?)(</\\^>)(.*)");

  private static enum State
  {
    LookForStart, LookForEnd,
  }

  private static char nextChar(String text, int idx)
  {
    if (idx >= text.length())
      return 0;
    return text.charAt(idx);
  }

  private static Pair<Integer, Integer> readEmp(String text, int idx)
  {
    StringBuilder sb = new StringBuilder();
    for (; idx < text.length(); ++idx) {
      char c = text.charAt(idx);
      if (Character.isDigit(c) == false) {
        break;
      }
      sb.append(c);
    }
    if (sb.length() == 0)
      return Pair.make(1, idx + 1);
    return Pair.make(Integer.parseInt(sb.toString()), idx);
  }

  private static String enrichFoundWord(String text, String w)
  {
    StringBuilder sb = new StringBuilder();

    int i;
    for (i = 0; i < text.length(); ++i) {
      if (StringUtils.startsWithIgnoreCase(text.substring(i), w) == true) {
        String bt = text.substring(i, i + w.length());
        sb.append(text.subSequence(0, i));
        if (text.length() > i + w.length()) {
          text = text.substring(i + w.length());
        } else {
          text = "";
        }
        i = 0;
        sb.append("<b><strong><big>").append(bt).append("</big></strong></b>");
      }
    }
    sb.append(text);
    return sb.toString();
  }

  private static String enrich(String text, List<String> words)
  {
    text = StringEscapeUtils.escapeHtml(text);
    text = StringUtils.replace(text, "\n", "<br/>\n");
    for (String w : words) {
      text = enrichFoundWord(text, w);
      // text = StringUtils.replace(text, w, "<b><strong><big>" + w + "</big></strong></b>");
    }
    return text;
  }

  @Deprecated
  public static String sampleToHtmlNew(String text, List<String> words)
  {
    String ap = Pattern.quote("<!--KW:XXX-->");
    String ep = Pattern.quote("<!--KW-->");
    // TODO gwiki geht nicht mit umlauten, da words normalisiert sind.
    // StringBuilder sb = new StringBuilder();
    for (String w : words) {
      String nw = NormalizeUtils.normalize(w);
      String app = StringUtils.replace(ap, "XXX", nw);
      String reg = app + "(.+?)" + ep;
      Pattern p = Pattern.compile(reg);
      Matcher m = p.matcher(text);
      while (m.find() == true) {
        int start = m.start(1);
        int end = m.end(1);
        String t = m.group(1);
        text = text.substring(0, start) + "<b><strong><big>" + t + "</big></strong></b>" + text.substring(end);
      }
      // text = StringUtils.replace(text, w, "<b><strong><big>" + w + "</big></strong></b>");
    }
    return text;
  }

  public static String sampleToHtml(String text, List<String> words)
  {
    return sampleToHtml(text, words, "<b><strong><big>", "</big></strong></b>");
  }

  public static String sampleToHtml(String text, List<String> words, String before, String after)
  {
    // String ntext = NormalizeUtils.normalize(text);
    String[] tks = StringUtils.splitByCharacterTypeCamelCase(text);
    List<String> nswords = new ArrayList<String>();
    for (String w : words) {
      nswords.add(NormalizeUtils.normalize(w));
    }
    StringBuilder sb = new StringBuilder();
    nextToken: for (String s : tks) {

      String ns = NormalizeUtils.normalize(s);
      for (String w : nswords) {
        if (ns.contains(w) == true) {
          sb.append(before).append(StringEscapeUtils.escapeHtml(s)).append(after);
          continue nextToken;
        }
      }
      sb.append(StringEscapeUtils.escapeHtml(s));
    }
    return sb.toString();
  }

  public static String sampleToHtmlOld(String text, List<String> words)
  {

    StringBuilder sb = new StringBuilder();
    State state = State.LookForStart;
    int lastemp = 1;
    int lastStartWordIdx = 0;
    int i;
    for (i = 0; i < text.length(); ++i) {
      char c = text.charAt(i);
      if (c != '<')
        continue;
      char c1 = nextChar(text, i + 1);
      char c2 = nextChar(text, i + 2);

      if (c1 == '/' && c2 == '^') {
        if (state == State.LookForEnd) {
          if (i > lastStartWordIdx) {
            String lb = text.substring(lastStartWordIdx, i);
            sb.append(enrich(lb, words));
          }
          if (lastemp > 1) {
            sb.append("</em>");
          }
        }
        i += 3;
        lastStartWordIdx = i + 1;
        state = State.LookForStart;
      } else if (c1 == '^') {
        if (i > lastStartWordIdx - 1) {
          String lb = text.substring(lastStartWordIdx, i);
          sb.append(enrich(lb, words));
        }
        Pair<Integer, Integer> p = readEmp(text, i + 2);
        lastemp = p.getFirst();
        i = p.getSecond() + 1;
        lastStartWordIdx = i;
        state = State.LookForEnd;
        if (lastemp > 1) {
          sb.append("<em>");
        }
      }
    }
    if (i - 1 > lastStartWordIdx) {
      sb.append(enrich(text.substring(lastStartWordIdx, i - 1), words));
    }
    String ret = sb.toString();
    ret = StringUtils.replace(ret, "<br/>\n<br/>\n", "<br/>\n");
    return ret;
  }

  public static SearchResult findResultFallback(GWikiContext ctx, SearchQuery query, SearchResult sr, String normExpress)
  {
    String pageId = sr.getPageId();
    String nt = NormalizeUtils.normalize(pageId);
    if (StringUtils.contains(nt, normExpress) == true) {
      return new SearchResult(sr, 20);
    }
    GWikiElementInfo eli = ctx.getWikiWeb().findElementInfo(pageId);
    if (eli == null) {
      return null;
    }
    nt = NormalizeUtils.normalize(eli.getTitle());
    if (StringUtils.contains(nt, normExpress) == true) {
      return new SearchResult(sr, 30);
    }
    return null;
  }

  public static SearchResult findResult(GWikiContext ctx, SearchQuery query, SearchResult sr)
  {
    GlobalWordIndexTextArtefakt art = query.getGlobalIndex(ctx);
    if (art == null) {
      return findResult(ctx, query, sr.getPageId());
    }
    String normExpress = NormalizeUtils.normalize(query.getSearchExpression());
    if (StringUtils.isEmpty(normExpress) == true) {
      return null;
    }
    int w = art.getFoundIndexWeight(ctx, sr, normExpress);
    if (w == -1 && query.isFallBackIfNoIndex() == true) {
      return findResultFallback(ctx, query, sr, normExpress);
    }
    if (w == 0) {
      return null;
    }
    return new SearchResult(sr, w);
  }

  public static SearchResult findResult(GWikiContext ctx, SearchQuery query, String pageId)
  {

    String indexFile = pageId + "TextIndex.txt";
    String content = readFileContent(ctx, indexFile);
    if (content == null)
      return null;
    String normExpress = NormalizeUtils.normalize(query.getSearchExpression());

    if (content.indexOf(normExpress) == -1)
      return null;
    SearchResult sr = new SearchResult(ctx.getWikiWeb().findElementInfo(pageId));
    // if (query.isWithSampleText() == true) {
    // getTextSample(ctx, sr, query.getSearchExpression(), pageId);
    // }
    return sr;
  }
}
