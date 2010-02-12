////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

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
