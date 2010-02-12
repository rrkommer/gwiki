/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.11.2009
// Copyright Micromata 21.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.tools.confluence.imp;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

/**
 * Holds a confluence body element.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class BodyContent extends Entity
{
  private List<String> bodies;

  public BodyContent(Element el)
  {
    super(el);
  }

  @Override
  public void parse()
  {
    bodies = new ArrayList<String>();
    List<Element> nodes = selectElements("property[@name = \"body\"]");
    for (Element n : nodes) {
      // String s = n.toString();
      bodies.add(selectText(n, "child::text()"));
      // bodies.add(selectText(n, ))
    }
    // ent.setBody(selectText(tel, "property[@name = \"body\"]/child::text()"));

  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("BodyContent|")//
        .append("id:").append(getId())//
        .append("|body:").append(bodies)//
    ;
    return sb.toString();
  }

  public List<String> getBodies()
  {
    return bodies;
  }

  public void setBodies(List<String> bodies)
  {
    this.bodies = bodies;
  }

}
