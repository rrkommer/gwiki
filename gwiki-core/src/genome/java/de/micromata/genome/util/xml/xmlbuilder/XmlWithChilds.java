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
import java.util.ArrayList;
import java.util.List;

/**
 * XML with nested childs
 * 
 * @author roger@micromata.de
 * 
 */
public abstract class XmlWithChilds extends XmlNode
{
  /**
   * 
   */
  protected List<XmlNode> childs;

  public XmlWithChilds(XmlNode... childs)
  {
    this.childs = new ArrayList<XmlNode>(childs.length);
    for (XmlNode n : childs) {
      this.childs.add(n);
    }
  }

  public XmlWithChilds nest(XmlNode... nodes)
  {
    for (XmlNode n : nodes) {
      this.childs.add(n);
    }   
    return this;
  }

  public void toStringChilds(XmlRenderer sb) throws IOException
  {
    if (childs == null)
      return;
    for (XmlNode x : childs) {
      x.toXml(sb);
    }
  }

  public List<XmlNode> getChilds()
  {
    return childs;
  }

  public void setChilds(List<XmlNode> childs)
  {
    this.childs = childs;
  }

}
