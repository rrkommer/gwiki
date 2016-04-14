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
 * @author Roger Rene Kommer (r.kommer@micromata.de)
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
