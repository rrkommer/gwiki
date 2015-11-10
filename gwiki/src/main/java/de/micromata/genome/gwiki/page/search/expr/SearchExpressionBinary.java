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

package de.micromata.genome.gwiki.page.search.expr;

import java.util.ArrayList;
import java.util.List;

/**
 * Combine two SearchExpression .
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class SearchExpressionBinary implements SearchExpression
{

  protected SearchExpression left;

  protected SearchExpression right;

  public SearchExpressionBinary()
  {

  }

  public SearchExpressionBinary(SearchExpression left, SearchExpression right)
  {
    this.left = left;
    this.right = right;
  }

  public List<String> getLookupWords()
  {
    List<String> ret = new ArrayList<String>();
    ret.addAll(left.getLookupWords());
    ret.addAll(right.getLookupWords());
    return ret;
  }

  public SearchExpression getLeft()
  {
    return left;
  }

  public void setLeft(SearchExpression left)
  {
    this.left = left;
  }

  public SearchExpression getRight()
  {
    return right;
  }

  public void setRight(SearchExpression right)
  {
    this.right = right;
  }

}
