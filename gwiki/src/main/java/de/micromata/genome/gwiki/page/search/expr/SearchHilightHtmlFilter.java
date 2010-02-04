/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   10.12.2009
// Copyright Micromata 10.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search.expr;

import java.io.Writer;
import java.util.List;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;

import de.micromata.genome.gwiki.utils.html.PassthroughHtmlFilter;

/**
 * Filter to highlight words in HTML.
 * 
 * @author roger
 * 
 */
public class SearchHilightHtmlFilter extends PassthroughHtmlFilter
{
  private List<String> words;

  private int ignoreLevel = 0;

  public SearchHilightHtmlFilter(Writer writer, List<String> words)
  {
    super(writer, "UTF-8");
    this.words = words;
  }

  @Override
  public void characters(XMLString text, Augmentations augs) throws XNIException
  {
    if (ignoreLevel > 0) {
      super.characters(text, augs);
      return;
    }
    String rt = text.toString();
    String sr = SearchUtils.sampleToHtml(rt, words, "<span style=\"background-color:#FFFF66;\">", "</span>");
    fPrinter.print(sr);
  }

  @Override
  public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException
  {
    if (ignoreLevel > 0) {
      ++ignoreLevel;

    } else if (element.rawname.equals("SCRIPT") == true) {
      ++ignoreLevel;
    }
    super.startElement(element, attributes, augs);
  }

  @Override
  public void endElement(QName element, Augmentations augs) throws XNIException
  {
    if (ignoreLevel > 0) {
      --ignoreLevel;
    }
    super.endElement(element, augs);
  }

}
