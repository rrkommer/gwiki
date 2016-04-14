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
