package de.micromata.genome.gwiki.page.impl.wiki.rte;

public class DumpDomVisitor implements DomVisitor
{

  @Override
  public void visit(HtmlDomWalker wk)
  {
    System.out.println("walk: " + wk.currentNode);

  }

}
