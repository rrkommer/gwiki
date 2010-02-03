/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   02.12.2009
// Copyright Micromata 02.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search.expr;

import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;

import de.micromata.genome.gwiki.page.search.CreateIndexHtmlFilter;

public class CreateIndexHtmlFilterTest extends TestCase
{
  public String createHtml(String body)
  {
    return "<html><body>" + body + "</body></html>";
  }

  public CreateIndexHtmlFilter parse(String body)
  {
    String html = createHtml(body);
    CreateIndexHtmlFilter nf = new CreateIndexHtmlFilter(1, null);
    XMLParserConfiguration parser = new HTMLConfiguration();
    parser.setProperty("http://cyberneko.org/html/properties/filters", new XMLDocumentFilter[] { nf});
    XMLInputSource source = new XMLInputSource(null, null, null, new StringReader(html), "UTF-8");
    try {
      parser.parse(source);
      return nf;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public void testIt()
  {
    CreateIndexHtmlFilter f = parse("H&auml;nde");
    Integer nf = f.getWordMap().get("HAENDE");
    assertEquals(Integer.valueOf(1), nf);
    f = parse("<h1>H&auml;nde</h1>");
    nf = f.getWordMap().get("HAENDE");
    assertEquals(Integer.valueOf(20), nf);
    f = parse("<h1>H&auml;nde</h1><p>H&auml;nde");
    nf = f.getWordMap().get("HAENDE");
    assertEquals(Integer.valueOf(21), nf);
  }
}
