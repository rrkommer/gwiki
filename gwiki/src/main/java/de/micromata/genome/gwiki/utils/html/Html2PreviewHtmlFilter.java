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

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.filters.DefaultFilter;

/**
 * Fitler, rendering a HTML preview.
 * 
 * @author roger
 * 
 */
public class Html2PreviewHtmlFilter extends DefaultFilter
{
  private StringBuilder textBuffer = new StringBuilder();

  private StringBuilder resultHtmlBuffer = new StringBuilder();

  @Override
  public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException
  {
    resultHtmlBuffer.append("<").append(element.rawname);
    for (int i = 0; i < attributes.getLength(); ++i) {
      resultHtmlBuffer.append(" ").append(attributes.getLocalName(i)).append("=\"").append(attributes.getValue(i)).append("\"");
    }
    resultHtmlBuffer.append(">");
    super.startElement(element, attributes, augs);
  }

  @Override
  public void endElement(QName element, Augmentations augs) throws XNIException
  {
    resultHtmlBuffer.append("</").append(element.rawname).append(">");
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

  public StringBuilder getTextBuffer()
  {
    return textBuffer;
  }

  public void setTextBuffer(StringBuilder textBuffer)
  {
    this.textBuffer = textBuffer;
  }

  public StringBuilder getResultHtmlBuffer()
  {
    return resultHtmlBuffer;
  }

  public void setResultHtmlBuffer(StringBuilder resultHtmlBuffer)
  {
    this.resultHtmlBuffer = resultHtmlBuffer;
  }

}
