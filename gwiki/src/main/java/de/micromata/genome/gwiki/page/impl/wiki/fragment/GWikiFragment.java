/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   18.10.2009
// Copyright Micromata 18.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import java.io.IOException;
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
   * 
   * @return wiki source
   */
  public String getSource();

  /**
   * Implementation interface of getSource()
   * 
   * @param sb
   */
  public void getSource(StringBuilder sb);

  /**
   * if return false, stop processing.
   * 
   * @param sb
   * @param ctx
   * @return
   * @throws IOException
   */
  public boolean render(GWikiContext ctx);

  /**
   * Check before save if current user has right to store a Wiki Fragment
   * 
   * @param ctx
   * @throws
   */
  public void ensureRight(GWikiContext ctx) throws AuthorizationFailedException;

  public List<GWikiFragment> getChilds();

  public void iterate(GWikiFragmentVisitor visitor);
}
