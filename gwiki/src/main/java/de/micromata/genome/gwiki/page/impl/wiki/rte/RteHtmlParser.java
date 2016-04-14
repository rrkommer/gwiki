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

package de.micromata.genome.gwiki.page.impl.wiki.rte;

import java.io.Reader;
import java.io.StringReader;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.utils.StringUtils;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.LogExceptionAttribute;
import de.micromata.genome.logging.LoggableRuntimeException;

public class RteHtmlParser
{
  public static String convert(GWikiContext wikiContext, String htmlCode)
  {
    DocumentFragment document = toDom(wikiContext, htmlCode);
    HtmlDomWalker walker = new HtmlDomWalker(document, wikiContext);
    walker.parseContext.pushFragList();
    //    walker.walk(new DumpDomVisitor());
    walker.walk(new RteDomVisitor());
    GWikiFragmentChildContainer cont = new GWikiFragmentChildContainer(walker.parseContext.popFragList());

    return cont.getSource();
  }

  public static DocumentFragment toDom(GWikiContext wikiContext, String text)
  {

    HTMLDocument document = new HTMLDocumentImpl();
    DocumentFragment fragment = document.createDocumentFragment();
    DOMFragmentParser parser = new DOMFragmentParser();
    parser.setErrorHandler(new ErrorHandler()
    {

      @Override
      public void warning(SAXParseException ex) throws SAXException
      {
        GLog.warn(GWikiLogCategory.Wiki, "Rte; Parse warn: " + ex.getMessage(), new LogExceptionAttribute(ex));

      }

      @Override
      public void fatalError(SAXParseException ex) throws SAXException
      {
        throw new LoggableRuntimeException(GWikiLogCategory.Wiki, "Rte; Parse fatal: " + ex.getMessage(),
            new LogExceptionAttribute(ex));

      }

      @Override
      public void error(SAXParseException ex) throws SAXException
      {
        throw new LoggableRuntimeException(GWikiLogCategory.Wiki, "Rte; Parse error: " + ex.getMessage(),
            new LogExceptionAttribute(ex));

      }
    });
    //    parser.setProperty("http://cyberneko.org/html/properties/filters", new XMLDocumentFilter[] { this });
    text = StringUtils.defaultString(text);
    String htmltext = text;
    XMLInputSource source = new XMLInputSource(null, null, null, new StringReader(text), "UTF-8");
    InputSource xsource = new InputSource()
    {

      @Override
      public Reader getCharacterStream()
      {
        return new StringReader(htmltext);
      }

    };
    try {
      parser.parse(xsource, fragment);
      return fragment;
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
