//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import java.util.List;

/**
 * Text decorations like italic, bold.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFragmentTextDeco extends GWikiFragmentDecorator
{

  private static final long serialVersionUID = -4065615892012017382L;

  private char wikiTag;

  /**
   * if no space is before/after the text deco, macro syntax is required.
   */
  private boolean requireMacroSyntax = false;

  public GWikiFragmentTextDeco(char wikiTag, String prefix, String suffix, List<GWikiFragment> childs)
  {
    super(prefix, suffix, childs);
    this.wikiTag = wikiTag;
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    if (requireMacroSyntax == true) {
      sb.append("{");
    }
    sb.append(wikiTag);
    if (requireMacroSyntax == true) {
      sb.append("}");
    }
    getChildSouce(sb);
    if (requireMacroSyntax == true) {
      sb.append("{");
    }
    sb.append(wikiTag);
    if (requireMacroSyntax == true) {
      sb.append("}");
    }
  }

  public boolean isRequireMacroSyntax()
  {
    return requireMacroSyntax;
  }

  public void setRequireMacroSyntax(boolean requireMacroSyntax)
  {
    this.requireMacroSyntax = requireMacroSyntax;
  }

}
