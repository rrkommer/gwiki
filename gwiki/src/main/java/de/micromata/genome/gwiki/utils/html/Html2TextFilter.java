/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   03.12.2009
// Copyright Micromata 03.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.utils.html;

import java.io.StringReader;

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

/**
 * HTML Filter to generate text extract.
 * 
 * @author roger
 * 
 */
public class Html2TextFilter extends DefaultFilter
{
  protected StringBuilder resultText = new StringBuilder();

  public static String html2Text(String text)
  {
    Html2TextFilter nf = new Html2TextFilter();
    XMLParserConfiguration parser = new HTMLConfiguration();
    parser.setProperty("http://cyberneko.org/html/properties/filters", new XMLDocumentFilter[] { nf});
    XMLInputSource source = new XMLInputSource(null, null, null, new StringReader(text), "UTF-8");
    try {
      parser.parse(source);
      return nf.resultText.toString();
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public Html2TextFilter()
  {
  }

  @Override
  public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException
  {
    String en = element.rawname.toLowerCase();
    if (en.equals("li") == true) {
      resultText.append("- ");
    }
    super.startElement(element, attributes, augs);
  }

  @Override
  public void endElement(QName element, Augmentations augs) throws XNIException
  {
    String en = element.rawname.toLowerCase();
    if (en.equals("br") == true) {
      resultText.append("\n");
    } else if (en.equals("li") == true) {
      resultText.append("\n");
    } else if (en.equals("p") == true) {
      resultText.append("\n\n");
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
    resultText.append(t);
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

  public StringBuilder getResultText()
  {
    return resultText;
  }

  public void setResultText(StringBuilder resultText)
  {
    this.resultText = resultText;
  }

}
