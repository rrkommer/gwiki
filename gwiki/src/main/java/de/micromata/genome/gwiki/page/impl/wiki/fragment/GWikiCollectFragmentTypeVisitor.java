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
