////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

import java.util.List;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiNestableFragment
{
  public void renderChilds(GWikiContext ctx);

  public void addChilds(List<GWikiFragment> childs);

  public void addChilds(GWikiFragment child);

  /**
   * 
   * @param search direct child of this Fragment to replace.
   * @param replace if null or empty, just remove empty
   * @return true if search was found and replaced
   */
  public boolean replaceChilds(GWikiFragment search, List<GWikiFragment> replace);

}
