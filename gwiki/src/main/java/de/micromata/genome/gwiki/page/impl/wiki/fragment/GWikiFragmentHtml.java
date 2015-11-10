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

import java.util.Collections;
import java.util.List;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Hard coded html fragment.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiFragmentHtml extends GWikiFragmentBase
{

  private static final long serialVersionUID = 6726490960427363801L;

  protected String html;

  public GWikiFragmentHtml()
  {

  }

  public GWikiFragmentHtml(String html)
  {
    this.html = html;
  }

  public GWikiFragmentHtml(GWikiFragmentHtml other)
  {
    this.html = other.html;
  }

  public boolean render(GWikiContext ctx)
  {
    ctx.append(getHtml());
    return true;
  }

  public void ensureRight(GWikiContext ctx) throws AuthorizationFailedException
  {
    // nothing
  }

  public String getHtml()
  {
    return html;
  }

  public List<GWikiFragment> getChilds()
  {
    return Collections.emptyList();
  }

  public void setHtml(String html)
  {
    this.html = html;
  }

}
