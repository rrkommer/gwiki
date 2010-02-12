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

import java.util.List;


/**
 * list elements inside a list.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFragmentLi extends GWikiFragmentDecorator
{

  private static final long serialVersionUID = 5564320556231411169L;

  GWikiFragmentList listfrag;

  public GWikiFragmentLi(GWikiFragmentList listfrag)
  {
    super("<li>", "</li>\n");
    this.listfrag = listfrag;
  }

  public GWikiFragmentLi(GWikiFragmentList listfrag, List<GWikiFragment> childs)
  {
    super("<li>", "</li>", childs);
    this.listfrag = listfrag;
  }

  public void getSource(StringBuilder sb)
  {
    sb.append(listfrag.getListTag()).append(" ");
    getChildSouce(sb);
    sb.append("\n");
  }
}
