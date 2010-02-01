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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.filters.DefaultFilter;

public class CollectHtmlXmlDocumentFilter extends DefaultFilter
{
  private Stack<Integer> curLevel;

  private WordCallback backend;

  static Map<String, Integer> elementWeigth = new HashMap<String, Integer>();
  static {
    elementWeigth.put("H1", 20);
    elementWeigth.put("H2", 10);
    elementWeigth.put("H3", 5);
    elementWeigth.put("H4", 2);
    elementWeigth.put("SCRIPT", 0);
    elementWeigth.put("STYLE", 0);
  }

  public CollectHtmlXmlDocumentFilter(WordCallback backend, int startLevel)
  {
    this.curLevel = new Stack<Integer>();
    this.curLevel.push(startLevel);
    this.backend = backend;
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
    Integer weight = elementWeigth.get(element.rawname);
    if (weight != null) {
      curLevel.pop();
    }
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
    int cl = curLevel.peek();
    backend.callback(t, cl);
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
    // TODO Auto-generated method stub
    super.comment(text, augs);
  }

}
