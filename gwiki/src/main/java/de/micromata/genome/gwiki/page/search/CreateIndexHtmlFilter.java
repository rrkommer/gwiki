/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   29.10.2009
// Copyright Micromata 29.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.filters.DefaultFilter;

import de.micromata.genome.gwiki.utils.html.CombinedHtmlFilter;
import de.micromata.genome.gwiki.utils.html.Html2TextFilter;

/**
 * Filter to create index.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class CreateIndexHtmlFilter extends CombinedHtmlFilter
{
  private Stack<Integer> curLevel;

  private StringBuilder textBuffer = new StringBuilder();

  private Map<String, Integer> wordMap = new TreeMap<String, Integer>();

  private Map<String, Integer> stopWords;

  private Html2TextFilter html2TextFilter;

  static Map<String, Integer> elementWeigth = new HashMap<String, Integer>();
  static {
    elementWeigth.put("H1", 20);
    elementWeigth.put("H2", 10);
    elementWeigth.put("H3", 5);
    elementWeigth.put("H4", 2);
    elementWeigth.put("SCRIPT", 0);
    elementWeigth.put("STYLE", 0);
  }

  public CreateIndexHtmlFilter(int startLevel, Map<String, Integer> stopWords)
  {
    this.curLevel = new Stack<Integer>();
    this.curLevel.push(startLevel);
    this.stopWords = stopWords;
    this.first = html2TextFilter = new Html2TextFilter();
    this.second = new DefaultFilter();
  }

  @Override
  public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException
  {
    Integer weight = elementWeigth.get(element.rawname);
    if (weight != null) {
      curLevel.push(weight);
    }

    super.startElement(element, attributes, augs);

  }

  @Override
  public void endElement(QName element, Augmentations augs) throws XNIException
  {
    int cl = curLevel.peek();
    Integer weight = elementWeigth.get(element.rawname);
    if (weight != null) {
      curLevel.pop();
    }
    addWords(cl, textBuffer.toString());

    textBuffer = new StringBuilder();
    super.endElement(element, augs);
  }

  @Override
  public void characters(XMLString text, Augmentations augs) throws XNIException
  {
    String t = text.toString();
    if (t.startsWith("<!--") == true) {
      super.characters(text, augs);
      return;
    }
    textBuffer.append(text);
    super.characters(text, augs);
  }

  @Override
  public void startCDATA(Augmentations augs) throws XNIException
  {
    super.startCDATA(augs);
  }

  @Override
  public void endCDATA(Augmentations augs) throws XNIException
  {
    super.endCDATA(augs);
  }

  @Override
  public void comment(XMLString text, Augmentations augs) throws XNIException
  {
    super.comment(text, augs);
  }

  List<String> split(String text)
  {
    List<String> ret = new ArrayList<String>();
    int lastIdx = 0;
    for (int i = 0; i < text.length(); ++i) {
      char c = text.charAt(i);
      if (Character.isLetterOrDigit(c) == false) {
        if (lastIdx < i) {
          ret.add(text.substring(lastIdx, i));
        }
        ret.add(Character.toString(c));
        lastIdx = i + 1;
      }
    }
    if (lastIdx < text.length()) {
      ret.add(text.substring(lastIdx));
    }
    return ret;
  }

  private void addWords(int cl, String text)
  {
    List<String> sl = split(text);
    for (String s : sl) {
      if (s.length() < 2) {
        continue;
      }
      String ns = NormalizeUtils.normalize(s);
      Integer i = wordMap.get(ns);
      if (i == null) {
        i = 0;
      }
      if (stopWords != null) {
        Integer sw = stopWords.get(ns);
        if (sw != null) {
          if (sw == 0) {
            continue;
          }
          cl = cl * sw;
        }
      }
      wordMap.put(ns, i + cl);
    }
  }

  public StringBuilder getTextBuffer()
  {
    return textBuffer;
  }

  public void setTextBuffer(StringBuilder textBuffer)
  {
    this.textBuffer = textBuffer;
  }

  public Map<String, Integer> getWordMap()
  {
    return wordMap;
  }

  public void setWordMap(Map<String, Integer> wordMap)
  {
    this.wordMap = wordMap;
  }

  public Map<String, Integer> getStopWords()
  {
    return stopWords;
  }

  public void setStopWords(Map<String, Integer> stopWords)
  {
    this.stopWords = stopWords;
  }

  public Html2TextFilter getHtml2TextFilter()
  {
    return html2TextFilter;
  }

  public void setHtml2TextFilter(Html2TextFilter html2TextFilter)
  {
    this.html2TextFilter = html2TextFilter;
  }

}
