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

import de.micromata.genome.gwiki.page.GWikiContext;

public abstract class GWikiFragmentBase implements GWikiFragment
{

  private static final long serialVersionUID = -1842371131960720605L;

  public abstract void getSource(StringBuilder sb);

  public String getSource()
  {
    StringBuilder sb = new StringBuilder();
    getSource(sb);
    return sb.toString();
  }

  public String toString()
  {
    return getSource();
  }

  public void iterate(GWikiFragmentVisitor visitor)
  {
    visitor.begin(this);
    visitor.end(this);
  }

  public boolean requirePrepareHeader(GWikiContext ctx)
  {
    return false;
  }

  public void prepareHeader(GWikiContext ctx)
  {

  }
}
