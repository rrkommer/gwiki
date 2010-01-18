/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   13.06.2009
// Copyright Micromata 13.06.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.xml.xmlbuilder;

import java.io.IOException;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * 
 * @author roger@micromata.de
 * 
 */
public class AppendableXmlRenderer implements XmlRenderer
{
  protected final Appendable appender;

  public AppendableXmlRenderer(final Appendable appender)
  {
    this.appender = appender;
  }

  public XmlRenderer code(String code) throws IOException
  {
    appender.append(code);
    return this;
  }

  public XmlRenderer nl() throws IOException
  {
    return this;
  }

  public XmlRenderer text(String code) throws IOException
  {
    appender.append(StringEscapeUtils.escapeXml(code));
    return this;
  }

  public XmlRenderer elementBeginOpen() throws IOException
  {
    appender.append("<");
    return this;
  }

  public XmlRenderer elementBeginEndClosed() throws IOException
  {
    appender.append("/>");
    return this;
  }

  public XmlRenderer elementBeginEndOpen() throws IOException
  {
    appender.append(">");
    return this;
  }

  public XmlRenderer elementEndClose() throws IOException
  {
    appender.append(">");
    return this;
  }

  public XmlRenderer elementEndOpen() throws IOException
  {
    appender.append("</");
    return this;
  }
}
