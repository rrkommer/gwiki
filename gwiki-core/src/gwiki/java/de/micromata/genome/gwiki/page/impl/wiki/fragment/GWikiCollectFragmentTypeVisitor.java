package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import java.util.ArrayList;
import java.util.List;

public class GWikiCollectFragmentTypeVisitor implements GWikiFragmentVisitor
{
  protected Class< ? extends GWikiFragment> classToFind;

  protected List<GWikiFragment> found = new ArrayList<GWikiFragment>();

  public GWikiCollectFragmentTypeVisitor(Class< ? extends GWikiFragment> classToFind)
  {
    super();
    this.classToFind = classToFind;
  }

  public void begin(GWikiFragment fragment)
  {
    if (classToFind.isAssignableFrom(fragment.getClass()) == true) {
      found.add(fragment);
    }
  }

  public void end(GWikiFragment fragment)
  {

  }

  public List<GWikiFragment> getFound()
  {
    return found;
  }

  public void setFound(List<GWikiFragment> found)
  {
    this.found = found;
  }
}
