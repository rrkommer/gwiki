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

/**
 * A XML Comment
 */
public class XmlComment extends XmlNode
{
  private final String comment;

  public XmlComment(final String comment)
  {
    this.comment = comment;
  }

  @Override
  public void toXml(XmlRenderer sb) throws IOException
  {
    sb.code("<!-- ").text(comment).code(" -->");

  }

}
