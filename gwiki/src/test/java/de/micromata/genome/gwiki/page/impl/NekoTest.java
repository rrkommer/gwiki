/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   29.10.2009
// Copyright Micromata 29.10.2009
//
/////////////////////////////////////////////////////////////////////////////
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
    String data = "<h1>Dies ist eine Überschrift</h1>";
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
