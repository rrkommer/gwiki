package de.micromata.genome.gwiki.page.impl.wiki.rte;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DomToStringUtils
{
  public static void attrToString(Element el, StringBuilder sb)
  {
    if (el.hasAttributes() == false) {
      return;
    }
    NamedNodeMap attrs = el.getAttributes();
    for (int i = 0; i < attrs.getLength(); ++i) {
      sb.append(" ");
      Node att = attrs.item(i);
      sb.append(att);

    }
  }

  public static String toString(Node node)
  {
    StringBuilder sb = new StringBuilder();
    toString(node, sb);
    return sb.toString();
  }

  public static void toString(Node node, StringBuilder sb)
  {
    sb.append(node.getClass().getSimpleName());
    if (node instanceof Element) {

      Element el = (Element) node;
      sb.append(":");
      sb.append(el.getNodeName());
      sb.append(" ");
      attrToString(el, sb);
    }
  }
}
