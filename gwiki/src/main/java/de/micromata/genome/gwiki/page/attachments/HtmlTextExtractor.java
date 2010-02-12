/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   04.12.2009
// Copyright Micromata 04.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.attachments;

import java.io.InputStream;

import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;

import de.micromata.genome.gwiki.utils.html.Html2TextFilter;

/**
 * extracts text from a html page.
 * 
 * @author roger
 * 
 */
public class HtmlTextExtractor extends TextExtractorBase
{

  public HtmlTextExtractor(String fileName, InputStream data)
  {
    super(fileName, data);
  }

  public String extractText()
  {
    Html2TextFilter filter = new Html2TextFilter();
    XMLParserConfiguration parser = new HTMLConfiguration();
    parser.setProperty("http://cyberneko.org/html/properties/filters", new XMLDocumentFilter[] { filter});
    XMLInputSource source = new XMLInputSource(null, null, null, data, "UTF-8");
    try {
      parser.parse(source);
      return filter.getResultText().toString();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

}
