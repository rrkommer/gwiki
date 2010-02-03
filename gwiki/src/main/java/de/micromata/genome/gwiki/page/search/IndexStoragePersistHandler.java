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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiStorageStoreElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiStorageStoreElementFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiTextContentArtefakt;
import de.micromata.genome.gwiki.utils.AppendableUtils;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.types.Pair;

public class IndexStoragePersistHandler implements GWikiStorageStoreElementFilter// , StoragePersistEventHandler
{
  public static final String TEXTINDEX_PARTNAME = "TextIndex";

  public static final String TEXTEXTRACT_PARTNMAE = "TextExtract";

  public boolean onPersist(GWikiContext wikiContext, GWikiStorage storage, GWikiElement element, Map<String, GWikiArtefakt< ? >> parts)
  {
    if (wikiContext.getBooleanRequestAttribute(GWikiStorage.STORE_NO_INDEX) == true) {
      return true;
    }
    if (createNewIndex(wikiContext, storage, element, parts) == false) {
      return false;
    }
    updateGlobalIndex(wikiContext, storage, element, parts);
    return true;
  }

  protected void updateGlobalIndex(final GWikiContext wikiContext, final GWikiStorage storage, final GWikiElement element,
      Map<String, GWikiArtefakt< ? >> parts)
  {
    if (parts.containsKey(TEXTINDEX_PARTNAME) == false) {
      return;
    }
    final WordIndexTextArtefakt tix = (WordIndexTextArtefakt) parts.get(TEXTINDEX_PARTNAME);
    wikiContext.getWikiWeb().getFilter().runWithoutFilters(new Class< ? >[] { IndexStoragePersistHandler.class},
        new CallableX<Void, RuntimeException>() {

          public Void call() throws RuntimeException
          {
            GlobalIndexFile.updateSegment(storage, element, tix.getStorageData());
            return null;
          }
        });
  }

  public boolean createNewIndex(GWikiContext wikiContext, GWikiStorage storage, GWikiElement element, Map<String, GWikiArtefakt< ? >> parts)
  {
    if (element.getElementInfo().isIndexed() == false) {
      return true;
    }
    if (element.getMetaTemplate() != null && element.getMetaTemplate().isNoSearchIndex() == true) {
      return true;
    }
    boolean hasIndexArtefakt = false;
    StringBuilder sb = new StringBuilder();
    sb.append("<html><body>\n");
    GWikiElement pel = wikiContext.getWikiElement();
    wikiContext.setWikiElement(element);
    for (Map.Entry<String, GWikiArtefakt< ? >> me : parts.entrySet()) {
      if (me.getValue() instanceof GWikiIndexedArtefakt) {
        GWikiIndexedArtefakt ia = (GWikiIndexedArtefakt) me.getValue();
        ia.getPreview(wikiContext, AppendableUtils.create(sb));
        hasIndexArtefakt = true;
      }
    }
    wikiContext.setWikiElement(pel);
    if (hasIndexArtefakt == false) {
      return true;
    }
    sb.append("</body></html>");

    String rt = sb.toString();
    if (StringUtils.isBlank(rt) == true) {
      return true;
    }

    GWikiTextContentArtefakt rta = new GWikiTextContentArtefakt() {
      @Override
      public boolean isNoArchiveData()
      {
        return true;
      }

      @Override
      public String getFileSuffix()
      {
        return ".txt";
      }
    };
    Pair<WordIndexTextArtefakt, String> ret = createIndexFile(wikiContext, storage, element, rt);
    rta.setStorageData(ret.getSecond());
    parts.put(TEXTEXTRACT_PARTNMAE, rta);

    parts.put(TEXTINDEX_PARTNAME, ret.getFirst());
    return true;
  }

  // protected boolean createOldIndex(GWikiContext wikiContext, GWikiStorage storage, GWikiElement element, Map<String, GWikiArtefakt>
  // parts)
  // {
  // if (element.getElementInfo().getProps().getBooleanValue(GWikiPropKeys.NOINDEX) == true) {
  // return true;
  // }
  // if (element.getMetaTemplate() != null && element.getMetaTemplate().isNoSearchIndex() == true) {
  // return true;
  // }
  // CollectTextWordCallback collector = new CollectTextWordCallback();
  // boolean hasIndexArtefakt = false;
  // for (Map.Entry<String, GWikiArtefakt> me : parts.entrySet()) {
  // if (me.getValue() instanceof GWikiIndexedArtefakt) {
  // GWikiIndexedArtefakt ia = (GWikiIndexedArtefakt) me.getValue();
  // ia.collectWords(collector);
  // hasIndexArtefakt = true;
  // }
  // }
  // if (hasIndexArtefakt == false) {
  // return true;
  // }
  // String rt = collector.getBuffer().toString();
  // if (StringUtils.isBlank(rt) == true) {
  // return true;
  // }
  // GWikiTextContentArtefakt rta = new GWikiTextContentArtefakt() {
  // @Override
  // public boolean isNoArchiveData()
  // {
  // return true;
  // }
  // };
  // rta.setStorageData(rt);
  // parts.put("TextExtract", rta);
  // // createIndex(wikiContext, storage, element, parts, rt);
  // return true;
  // }

  private Map<String, Integer> getStopWords(GWikiStorage storage)
  {
    GWikiElement el = GWikiWeb.get().findElement("admin/config/StopWords");
    if (el == null) {
      return null;
    }
    WordIndexTextArtefakt swa = (WordIndexTextArtefakt) el.getMainPart();
    return swa.getStopWords();
  }

  private int getStopWordWeight(GWikiStorage storage, String r)
  {
    Map<String, Integer> s = getStopWords(storage);
    if (s == null)
      return 1;
    Integer ir = s.get(r);
    if (ir == null)
      return 1;
    return s.get(r);
  }

  /**
   * Problem dabei. Bei suche nach NOL findet er auch NOL im teilnamen.
   * 
   * @param words
   */
  protected void compressIndex(Set<String> words)
  {
    if (true)
      return;
    // wird nicht mehr verwendet.
    List<String> allwords = new ArrayList<String>();
    allwords.addAll(words);
    Collections.sort(allwords, new Comparator<String>() {

      public int compare(String o1, String o2)
      {
        return o1.length() - o2.length();
      }
    });
    for (int i = 0; i < allwords.size(); ++i) {
      String w = allwords.get(i);
      for (int j = i + 1; j < allwords.size(); ++j) {
        String ow = allwords.get(j);
        if (ow.indexOf(w) != -1) {
          words.remove(w);
        }
      }
    }
  }

  private int parseNextLevel(String text, int i, Stack<Integer> curLevel)
  {
    char c = text.charAt(i);
    if (c == '/') {
      c = text.charAt(i + 1);
      if (c == '>') {
        if (curLevel.size() > 1)
          curLevel.pop();
        return i + 2;
      } else {
        return i;
      }
    } else {
      StringBuilder sb = new StringBuilder();

      for (; i < text.length(); ++i) {
        c = text.charAt(i);
        if (Character.isDigit(c) == false)
          break;
        sb.append(c);
      }
      curLevel.push(Integer.parseInt(sb.toString()));
      return i;
    }
  }

  private void addWord(GWikiStorage storage, Map<String, Integer> ts, Stack<Integer> curLevel, String word)
  {
    if (word.length() < 2)
      return;
    word = NormalizeUtils.normalize(word);
    int fac = getStopWordWeight(storage, word);
    if (fac == 0)
      return;
    int cl = curLevel.peek().intValue() * fac;
    Integer li = ts.get(word);
    if (li == null) {
      ts.put(word, cl);
    } else {
      ts.put(word, li.intValue() + cl);
    }
  }

  public CreateIndexHtmlFilter parse(String body, final GWikiStorage storage)
  {
    String html = body;
    CreateIndexHtmlFilter nf = new CreateIndexHtmlFilter(1, getStopWords(storage));
    XMLParserConfiguration parser = new HTMLConfiguration();
    parser.setProperty("http://cyberneko.org/html/properties/filters", new XMLDocumentFilter[] { nf});
    XMLInputSource source = new XMLInputSource(null, null, null, new StringReader(html), "UTF-8");
    try {
      parser.parse(source);
      return nf;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  protected Pair<WordIndexTextArtefakt, String> createIndexFile(final GWikiContext wikiContext, final GWikiStorage storage,
      final GWikiElement element, String rawText)
  {
    CreateIndexHtmlFilter filter = parse(rawText, storage);
    String parsedText = filter.getHtml2TextFilter().getResultText().toString();
    final Map<String, Integer> ts = filter.getWordMap();

    WordIndexTextArtefakt rta = new WordIndexTextArtefakt(element.getElementInfo().getId());
    rta.setStopWords(ts);
    return Pair.make(rta, parsedText);
  }

  // protected void createIndex(final GWikiContext wikiContext, final GWikiStorage storage, final GWikiElement element,
  // Map<String, GWikiArtefakt> parts, String rawText)
  // {
  //
  // // List<String> words = Converter.parseStringTokens(rawText, " \t\n.,:!?", false);
  //
  // final Map<String, Integer> ts = new TreeMap<String, Integer>();
  // Stack<Integer> curLevel = new Stack<Integer>();
  // curLevel.push(1);
  // int i = 0;
  // char c1;
  // char c2;
  // char c3;
  // int startWordIndex = 0;
  // int endWordIndex = 0;
  // String word;
  // for (; i < rawText.length(); ++i) {
  // c1 = rawText.charAt(i);
  // switch (c1) {
  // case '<': {
  // endWordIndex = i;
  // if (endWordIndex > startWordIndex) {
  // word = rawText.substring(startWordIndex, endWordIndex);
  // addWord(storage, ts, curLevel, word);
  // }
  // c2 = rawText.charAt(i + 1);
  // if (c2 == '^') {
  // i = parseNextLevel(rawText, i + 2, curLevel);
  // startWordIndex = i;
  // break;
  // }
  // }
  // case '\t':
  // case '\n':
  // case '.':
  // case ',':
  // case '?':
  // case '!':
  // case ':':
  // case ' ':
  // case '-':
  // case '/':
  // case '[':
  // case ']':
  // case '{':
  // case '}':
  // case '(':
  // case ')':
  // case '\'':
  // case '"':
  // if (i > startWordIndex) {
  // word = rawText.substring(startWordIndex, i);
  // addWord(storage, ts, curLevel, word);
  // }
  // startWordIndex = i + 1;
  // break;
  // default:
  // if (Character.isLetterOrDigit(c1) == false) {
  // if (i > startWordIndex) {
  // word = rawText.substring(startWordIndex, i);
  // addWord(storage, ts, curLevel, word);
  // startWordIndex = i + 1;
  // }
  // }
  // break;
  // }
  // }
  // WordIndexTextArtefakt rta = new WordIndexTextArtefakt();
  // rta.setStopWords(ts);
  // parts.put("TextIndex", rta);
  // wikiContext.getWikiWeb().getFilter().runWithoutFilters(new Class< ? >[] { IndexStoragePersistHandler.class},
  // new CallableX<Void, RuntimeException>() {
  //
  // public Void call() throws RuntimeException
  // {
  // GlobalIndexFile.updateSegment(storage, element, WordIndexTextArtefakt.stopWordsToString(ts));
  // return null;
  // }
  // });
  //
  // }

  public Void filter(GWikiFilterChain<Void, GWikiStorageStoreElementFilterEvent, GWikiStorageStoreElementFilter> chain,
      GWikiStorageStoreElementFilterEvent event)
  {
    try {
      onPersist(event.getWikiContext(), event.getWikiContext().getWikiWeb().getStorage(), event.getElement(), event.getParts());
    } catch (Exception ex) {
      GWikiLog.error("Failure to update index: " + ex.getMessage(), ex);
    }
    chain.nextFilter(event);
    return null;
  }
}
