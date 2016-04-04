package de.micromata.genome.gwiki.page.impl.wiki.rte;

import org.w3c.dom.Text;

public class DomTextEvent extends DomEvent
{
  Text text;

  public DomTextEvent(HtmlDomWalker walker, RteDomVisitor visitor, Text text)
  {
    super(walker, visitor);
    this.text = text;
  }

  public String getText()
  {
    return text.getNodeValue();
  }
}
