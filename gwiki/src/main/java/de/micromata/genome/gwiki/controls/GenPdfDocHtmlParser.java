/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   13.12.2009
// Copyright Micromata 13.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.controls;

import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import com.lowagie.text.html.HtmlParser;

/**
 * Pre-Alpha implementation of Pdf exporter.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GenPdfDocHtmlParser extends HtmlParser
{
  public void setEntityResolver(EntityResolver resolver) throws SAXException
  {
    parser.getParser().setEntityResolver(resolver);
  }
}
