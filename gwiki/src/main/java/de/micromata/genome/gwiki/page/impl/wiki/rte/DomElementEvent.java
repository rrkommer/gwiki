package de.micromata.genome.gwiki.page.impl.wiki.rte;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.StringUtils;

public class DomElementEvent extends DomEvent
{

  public Element element;

  public DomElementEvent(HtmlDomWalker walker, RteDomVisitor visitor, Element element)
  {
    super(walker, visitor);
    this.element = element;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    if (element != null) {
      DomToStringUtils.toString(element, sb);
    }
    return sb.toString();
  }

  public String getAttr(String attrName)
  {
    return getAttr(element, attrName);
  }

  public String getAttr(Node node, String attrName)
  {
    if ((node instanceof Element) == false) {
      return "";
    }
    return StringUtils.defaultString(((Element) node).getAttribute(attrName));
  }

  public String getStyleClass(Node node)
  {
    return getAttr(node, "class");
  }

  public String getStyleClass()
  {
    return getStyleClass(element);

  }

  public String getElementName()
  {
    return element.getNodeName();
  }

  public Element firstChildElement()
  {
    return firstChildElement(element);
  }

  public Element firstChildElement(Element element)
  {
    Node child = element.getFirstChild();
    if (child != null && (child instanceof Element) == false) {
      child = child.getNextSibling();
    }
    if (child instanceof Element) {
      return (Element) child;
    }
    return null;
  }

  public boolean containsInStyleClass(Node node, String text)
  {
    return getStyleClass(node).contains(text);
  }

  public boolean containsInStyleClass(String text)
  {
    return getStyleClass().contains(text);
  }

  public Element getNextElementSibling(Element node)
  {
    Node ret = node.getNextSibling();
    while (ret != null && (ret instanceof Element) == false) {
      ret = ret.getNextSibling();
    }

    return (Element) ret;
  }

  public GWikiContext getWikiContext()
  {
    return walker.wikiContext;
  }

}