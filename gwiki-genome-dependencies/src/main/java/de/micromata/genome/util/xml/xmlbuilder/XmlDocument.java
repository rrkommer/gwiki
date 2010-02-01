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
 * Document node.
 * 
 * @author roger@micromata.de
 * 
 */
public class XmlDocument extends XmlWithChilds
{
  /**
   * attributes. Must be even.
   */
  private String[] attributes;

  public XmlDocument(XmlNode... childs)
  {
    super(childs);
  }

  public XmlDocument(String[]... attrs)
  {
    attributes = XmlElement.join(attrs);
  }

  public XmlDocument(String[][] attrs, XmlNode... childs)
  {
    super(childs);
    attributes = XmlElement.join(attrs);
  }

  @Override
  public void toXml(XmlRenderer sb) throws IOException
  {
    sb.code("<?xml");
    XmlElement.renderAttributes(attributes, sb);
    sb.code("?>");
    toStringChilds(sb);
  }

}
