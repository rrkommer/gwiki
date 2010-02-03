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

import java.io.StringReader;

import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;

@Deprecated
public class HtmlParserWordCallback2 implements WordCallback
{
  private WordCallback backend;

  public HtmlParserWordCallback2(WordCallback backend)
  {
    this.backend = backend;
  }

  public void callback(String word, int level)
  {
    CollectHtmlXmlDocumentFilter nf = new CollectHtmlXmlDocumentFilter(backend, level);
    XMLParserConfiguration parser = new HTMLConfiguration();
    parser.setProperty("http://cyberneko.org/html/properties/filters", new XMLDocumentFilter[] { nf});
    XMLInputSource source = new XMLInputSource(null, null, null, new StringReader(word), "UTF-8");
    try {
      parser.parse(source);
    } catch (Exception ex) {
      // TODO gwiki log only
      throw new RuntimeException(ex);
    }

  }

  public void popLevel()
  {
    backend.popLevel();
  }

  public void pushLevel(int level)
  {
    backend.pushLevel(level);
  }
}
