package de.micromata.genome.gwiki.page.impl.wiki.rte;

import java.util.List;

import org.w3c.dom.Node;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;

public class DomEvent
{
  public HtmlDomWalker walker;
  public RteDomVisitor visitor;

  public DomEvent(HtmlDomWalker walker, RteDomVisitor visitor)
  {
    super();
    this.walker = walker;
    this.visitor = visitor;
  }

  public GWikiWikiParserContext getParseContext()
  {
    return walker.parseContext;
  }

  public void setCurNode(Node node)
  {
    walker.currentNode = node;
  }

  public void walkChilds()
  {

    walker.walkChilds(visitor);
    walker.parseContext.flushText();
  }

  public List<GWikiFragment> walkCollectChilds()
  {
    walker.parseContext.flushText();
    walker.parseContext.pushFragList();
    walkChilds();
    return walker.parseContext.popFragList();
  }

}
