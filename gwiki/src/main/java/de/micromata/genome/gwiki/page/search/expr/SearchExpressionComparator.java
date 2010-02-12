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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.util.matcher.Matcher;

/**
 * Comparator of two field selectors.
 * 
 * @author roger
 * 
 */
public class SearchExpressionComparator implements SearchExpression
{
  private Matcher<String> comparator;

  private SearchExpressionFieldSelektor left;

  private SearchExpressionText right;

  public SearchExpressionComparator(Matcher<String> comparator, SearchExpressionFieldSelektor left, SearchExpressionText right)
  {
    super();
    this.comparator = comparator;
    this.left = left;
    this.right = right;
  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    Collection<SearchResult> res = new ArrayList<SearchResult>();
    for (SearchResult sr : query.getResults()) {
      String fv = left.getField(sr);
      if (comparator.match(fv) == true) {
        res.add(sr);
      }
    }
    return res;
  }

  public List<String> getLookupWords()
  {
    return right.getLookupWords();
  }

  @Override
  public String toString()
  {
    return "compare(" + left.toString() + " to " + comparator.toString() + ")";
  }

  public Matcher<String> getComparator()
  {
    return comparator;
  }

  public void setComparator(Matcher<String> comparator)
  {
    this.comparator = comparator;
  }

  public SearchExpressionFieldSelektor getLeft()
  {
    return left;
  }

  public void setLeft(SearchExpressionFieldSelektor left)
  {
    this.left = left;
  }

  public SearchExpressionText getRight()
  {
    return right;
  }

  public void setRight(SearchExpressionText right)
  {
    this.right = right;
  }

}
