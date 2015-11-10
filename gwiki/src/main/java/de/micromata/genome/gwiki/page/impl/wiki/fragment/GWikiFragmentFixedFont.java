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

public class GWikiFragmentFixedFont extends GWikiFragmentDecorator
{

  private static final long serialVersionUID = -8245596367479475761L;

  public GWikiFragmentFixedFont(List<GWikiFragment> childs)
  {
    super("<span style=\"font-family:courier new,courier,monospace;\">", "</span>", childs);
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    sb.append("{{");
    getChildSouce(sb);
    sb.append("}}");
  }

}
