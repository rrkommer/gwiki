////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

////////////////////////////////////////////////////////////////////////////

// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

////////////////////////////////////////////////////////////////////////////

package de.micromata.genome.gwiki.page.impl;

import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

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

public class NekoTest extends TestCase
{
  public void testRegExp()
  {
    String t = "A/b";
    Pattern p = Pattern.compile("[^A-Za-z0-9/]");
    Matcher m = p.matcher(t);
    String erg = m.replaceAll("");
    System.out.println(erg);
  }

  public void testFirst()
  {
    String data = "<h1>Dies ist eine &Uuml;berschrift</h1>";
    DefaultFilter filter = new DefaultFilter() {

      @Override
      public void characters(XMLString text, Augmentations augs) throws XNIException
      {
        super.characters(text, augs);
      }

      @Override
      public void endCDATA(Augmentations augs) throws XNIException
      {
        super.endCDATA(augs);
      }

      @Override
      public void endElement(QName element, Augmentations augs) throws XNIException
      {
        super.endElement(element, augs);
      }

      @Override
      public void startCDATA(Augmentations augs) throws XNIException
      {
        super.startCDATA(augs);
      }

      @Override
      public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException
      {
        super.startElement(element, attributes, augs);
      }

    };
    XMLParserConfiguration parser = new HTMLConfiguration();
    parser.setProperty("http://cyberneko.org/html/properties/filters", new XMLDocumentFilter[] { filter});
    XMLInputSource source = new XMLInputSource(null, null, null, new StringReader(data), "UTF-8");
    try {
      parser.parse(source);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
