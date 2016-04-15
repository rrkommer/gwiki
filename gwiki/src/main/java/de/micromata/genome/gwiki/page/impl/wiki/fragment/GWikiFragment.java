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

import java.io.Serializable;
import java.util.List;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Base interface for a Wiki Element.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiFragment extends Serializable
{

  /**
   * Gets the source.
   *
   * @return wiki source
   */
  public String getSource();

  /**
   * Gets the source.
   *
   * @param sb the sb
   * @param parent the parent
   * @param previous the previous
   * @param next the next
   */
  default void getSource(StringBuilder sb, GWikiFragment parent, GWikiFragment previous, GWikiFragment next)
  {
    getSource(sb);
  }

  /**
   * Implementation interface of getSource.
   *
   * @param sb the sb
   */
  public void getSource(StringBuilder sb);

  /**
   * if return false, stop processing.
   *
   * @param ctx the ctx
   * @return true if continue to render silblings.
   */
  public boolean render(GWikiContext ctx);

  /**
   * Check before save if current user has right to store a Wiki Fragment.
   *
   * @param ctx the ctx
   * @throws AuthorizationFailedException the authorization failed exception
   */
  public void ensureRight(GWikiContext ctx) throws AuthorizationFailedException;

  /**
   * Gets the childs.
   *
   * @return the childs
   */
  public List<GWikiFragment> getChilds();

  /**
   * iterate trough a visitor.
   *
   * @param visitor the visitor
   * @param parent may be null.
   */
  public void iterate(GWikiFragmentVisitor visitor, GWikiFragment parent);

  /**
   * return true, if requires to call prepareHeader.
   *
   * @param ctx the ctx
   * @return true, if successful
   */
  boolean requirePrepareHeader(GWikiContext ctx);

  /**
   * Will be called before rendering page to add html header informations. simply provide empty implementaiton, if
   * requirePrepareHeader(ctx) doesn't return true;
   *
   * @param ctx the ctx
   */
  void prepareHeader(GWikiContext ctx);

  /**
   * return GWikiMacroRenderFlags
   * 
   * @return
   */
  default int getRenderModes()
  {
    return 0;
  }
}
