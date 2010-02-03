package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;

public class GWikiCollectMacroFragmentVisitor extends GWikiCollectFragmentTypeVisitor
{
  private String macroName;

  public GWikiCollectMacroFragmentVisitor(String macroName)
  {
    super(GWikiMacroFragment.class);
    this.macroName = macroName;
  }

  @Override
  public void begin(GWikiFragment fragment)
  {
    if (classToFind.isAssignableFrom(fragment.getClass()) == false) {
      return;
    }
    GWikiMacroFragment mf = (GWikiMacroFragment) fragment;
    if (mf.getAttrs().getCmd().equals(macroName) == false) {
      return;
    }
    found.add(fragment);
  }
}
