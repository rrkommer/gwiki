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
 * @author Roger Rene Kommer (r.kommer@micromata.de)
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
