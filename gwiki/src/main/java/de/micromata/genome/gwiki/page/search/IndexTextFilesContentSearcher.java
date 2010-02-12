/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   27.10.2009
// Copyright Micromata 27.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.micromata.genome.gdbfs.FsFileObject;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.spi.storage.GWikiFileStorage;
import de.micromata.genome.util.matcher.AndMatcher;
import de.micromata.genome.util.matcher.EqualsMatcher;
import de.micromata.genome.util.matcher.LeftRightMatcherBase;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.OrMatcher;
import de.micromata.genome.util.matcher.string.StringMatcherBase;
import de.micromata.genome.util.matcher.string.StringPatternMatcherBase;
import de.micromata.genome.util.types.Pair;

/**
 * ContentSearch using index text files.
 * 
 * @author roger
 * 
 */
public class IndexTextFilesContentSearcher implements ContentSearcher
{

  public Collection<String> getSearchMacros()
  {
    return Collections.emptyList();
  }

  public String readFileContent(GWikiContext ctx, String indexFile)
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

  protected void getTextSample(GWikiContext ctx, SearchResult res, String searchExpression, String pageId)
  {
    String indexFile = pageId + "TextExtract.txt";
    String rawText = readFileContent(ctx, indexFile);
    if (rawText == null) {
      return;
    }
    String nsex = searchExpression.toUpperCase();
    String nt = rawText.toUpperCase();
    int idx = nt.indexOf(nsex);
    if (idx == -1) {
      return;
    }
    int startIdx = 0;
    if (idx > 100) {
      startIdx = idx - 100;
    }
    int endIdx = idx + 100;
    if (rawText.length() - 1 < idx + 100) {
      endIdx = rawText.length() - 1;
    }
    String sample = rawText.substring(startIdx, endIdx);
    res.setTextExerpt(sample);
  }

  protected SearchResult findResult(GWikiContext ctx, String searchExpression, String pageId)
  {

    String indexFile = pageId + "TextIndex.txt";
    String content = readFileContent(ctx, indexFile);
    if (content == null)
      return null;
    String normExpress = NormalizeUtils.normalize(searchExpression);

    if (content.indexOf(normExpress) == -1)
      return null;
    SearchResult sr = new SearchResult(ctx.getWikiWeb().findElementInfo(pageId));
    getTextSample(ctx, sr, searchExpression, pageId);
    return sr;
  }

  public List<SearchResult> search(GWikiContext ctx, String searchExpression, int maxCount)
  {
    SearchMatcherFactory fac = new SearchMatcherFactory();
    Matcher<String> matcher = fac.createMatcher(searchExpression);
    return search(ctx, matcher, maxCount);
  }

  private static class SearchResultIdComparator implements Comparator<SearchResult>
  {

    public int compare(SearchResult o1, SearchResult o2)
    {
      return o1.getPageId().compareTo(o2.getPageId());
    }
  }

  private Collection<SearchResult> or(Collection<SearchResult> first, Collection<SearchResult> second)
  {
    Map<String, SearchResult> ret = new TreeMap<String, SearchResult>();
    for (SearchResult sr : first) {
      ret.put(sr.getPageId(), sr);
    }
    for (SearchResult sr : second) {
      SearchResult or = ret.get(sr.getPageId());
      if (or != null) {
        or.setRelevance(or.getRelevance() + sr.getRelevance() + 50);
      } else {
        ret.put(sr.getPageId(), sr);
      }
    }
    // Set<SearchResult> set = new TreeSet<SearchResult>(new SearchResultIdComparator());
    // ret.
    return ret.values();
  }

  private Collection<SearchResult> and(Collection<SearchResult> first, Collection<SearchResult> second)
  {
    Set<SearchResult> ret = new TreeSet<SearchResult>(new SearchResultIdComparator());
    ret.addAll(first);
    ret.retainAll(second);
    return ret;
  }

  private int getWeightFromLine(String content, int idx)
  {
    while (idx >= 0) {
      if (content.charAt(idx) == '|')
        break;
      --idx;
    }
    int eidx = idx;
    while (idx >= 0) {
      if (content.charAt(idx) == '\n') {
        break;
      }
      --idx;
    }
    String pt = content.substring(idx + 1, eidx);
    try {
      return Integer.parseInt(pt);
    } catch (NumberFormatException ex) {
      return 1;
    }
  }

  private String getFullWordFromLine(String content, int idx)
  {
    int i = idx;
    while (i >= 0) {
      if (content.charAt(i) == '|') {
        ++i;
        break;
      }
      --i;
    }
    int sidx = i;
    i = idx;
    while (i < content.length()) {
      if (content.charAt(i) == '\n') {
        break;
      }
      ++i;
    }
    int eidx = i;
    if (sidx < eidx) {
      return content.substring(sidx, eidx);
    }
    return "";
  }

  private Pair<String, Integer> findInIdxText(String content, Matcher<String> matcher)
  {
    String pt;
    if (matcher instanceof StringPatternMatcherBase) {
      pt = ((StringPatternMatcherBase) matcher).getPattern();
    } else if (matcher instanceof EqualsMatcher) {
      pt = ((EqualsMatcher<String>) matcher).getOther();
    } else {
      throw new RuntimeException("Unsupported Matcher");
    }
    String normpt = NormalizeUtils.normalize(pt);
    String fqnpt = "|" + normpt + "\n";
    int idx = content.indexOf(fqnpt);
    boolean exactMatch = false;
    if (idx != -1) {
      exactMatch = true;
    } else {
      idx = content.indexOf(normpt);
      if (idx == -1) {
        return null;
      }
    }
    int hit = 0;
    if (exactMatch == true)
      hit = 3;
    int weight = getWeightFromLine(content, idx);
    if (exactMatch == true) {
      return Pair.make(normpt, hit * weight);
    }
    return Pair.make(normpt, hit * weight);
  }

  private Pair<String, Integer> findInIdxText(String content, StringMatcherBase<String> matcher)
  {
    if (matcher instanceof StringPatternMatcherBase) {
      return findInIdxText(content, (StringPatternMatcherBase) matcher);
    }
    throw new RuntimeException("not supported Matcher Type: " + matcher.getClass().getName());
  }

  private int getFoundIndexWeightOld(GWikiContext ctx, SearchResult res, Matcher<String> matcher)
  {
    String indexFile = res.getPageId() + "TextIndex.txt";
    String content = readFileContent(ctx, indexFile);
    if (content == null)
      return 0;
    Pair<String, Integer> p = findInIdxText(content, matcher);
    if (p == null)
      return 0;

    return p.getSecond();
  }

  private int getFoundIndexWeight(GWikiContext ctx, SearchResult res, Matcher<String> matcher)
  {
    GWikiElement gf = ctx.getWikiWeb().findElement(GlobalIndexFile.GLOBAL_INDEX_PAGEID);
    if (gf == null) {
      return getFoundIndexWeightOld(ctx, res, matcher);
    }
    GlobalWordIndexTextArtefakt gfa = (GlobalWordIndexTextArtefakt) gf.getMainPart();
    String pt;
    if (matcher instanceof StringPatternMatcherBase) {
      pt = ((StringPatternMatcherBase) matcher).getPattern();
    } else if (matcher instanceof EqualsMatcher) {
      pt = ((EqualsMatcher<String>) matcher).getOther();
    } else {
      throw new RuntimeException("Unsupported Matcher");
    }
    String normpt = NormalizeUtils.normalize(pt);
    return gfa.getFoundIndexWeight(ctx, res, normpt);
  }

  private Collection<SearchResult> filterByContent(GWikiContext ctx, Collection<SearchResult> infos, Matcher<String> matcher)
  {
    List<SearchResult> ret = new ArrayList<SearchResult>();
    for (SearchResult sr : infos) {
      int w = getFoundIndexWeight(ctx, sr, matcher);
      if (w == 0) {
        continue;
      }
      ret.add(new SearchResult(sr, sr.getRelevance() + w));
    }
    return ret;
  }

  private Collection<SearchResult> filter(GWikiContext ctx, Collection<SearchResult> infos, Matcher<String> matcher)
  {
    if (matcher instanceof AndMatcher) {
      AndMatcher<String> am = (AndMatcher<String>) matcher;
      Collection<SearchResult> lc = filter(ctx, infos, am.getLeftMatcher());
      Collection<SearchResult> rc = filter(ctx, infos, am.getRightMatcher());
      return and(lc, rc);
    } else if (matcher instanceof OrMatcher) {
      OrMatcher<String> am = (OrMatcher<String>) matcher;
      Collection<SearchResult> lc = filter(ctx, infos, am.getLeftMatcher());
      Collection<SearchResult> rc = filter(ctx, infos, am.getRightMatcher());
      return or(lc, rc);
    } else if (matcher instanceof StringMatcherBase || matcher instanceof EqualsMatcher) {
      return filterByContent(ctx, infos, matcher);
    } else {
      throw new RuntimeException("Matcher not supported for Search: " + matcher.getClass());
    }
  }

  public static class SearchResultByRelevanceComparator implements Comparator<SearchResult>
  {
    public int compare(SearchResult o1, SearchResult o2)
    {
      return o2.getRelevance() - o1.getRelevance();
    }
  }

  private String getSampleByMatcher(Matcher<String> matcher)
  {
    if (matcher instanceof StringPatternMatcherBase) {
      return ((StringPatternMatcherBase) matcher).getPattern();
    }
    if (matcher instanceof LeftRightMatcherBase) {
      LeftRightMatcherBase<String> mm = (LeftRightMatcherBase<String>) matcher;
      return getSampleByMatcher(mm.getLeftMatcher());
    }
    return "";
  }

  public List<SearchResult> search(GWikiContext ctx, Matcher<String> matcher, int maxCount)
  {
    List<SearchResult> ret = new ArrayList<SearchResult>();
    Collection<GWikiElementInfo> allPis = ctx.getWikiWeb().getPageInfos().values();
    List<SearchResult> alls = new ArrayList<SearchResult>();
    for (GWikiElementInfo ei : allPis) {
      alls.add(new SearchResult(ei));
    }
    ret.addAll(filter(ctx, alls, matcher));
    Collections.sort(ret, new SearchResultByRelevanceComparator());
    String sampleText = getSampleByMatcher(matcher);
    for (int i = 0; i < maxCount && i < ret.size(); ++i) {
      getTextSample(ctx, ret.get(i), sampleText, ret.get(i).getPageId());
    }
    return ret;
  }

  public void addElement(GWikiElement el)
  {

  }

  public void removeElement(GWikiElementInfo ei)
  {

  }

  public void replaceElement(GWikiElement el)
  {

  }

  public QueryResult search(GWikiContext ctx, SearchQuery query)
  {
    List<SearchResult> lres = search(ctx, query.getSearchExpression(), query.getMaxCount());
    return new QueryResult(lres, lres.size());
  }

  public void rebuildIndex(GWikiContext wikiContext, String pageId)
  {
    throw new RuntimeException("rebuildIndex not supported");
  }
}
