/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   14.11.2009
// Copyright Micromata 14.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search.expr;

import de.micromata.genome.gwiki.page.search.SearchResult;

/**
 * Liest den wert aus.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface SearchExpressionFieldSelektor
{
  String getField(SearchResult sr);
}
