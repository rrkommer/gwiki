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

package de.micromata.genome.gwiki.page.search.expr;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchExpressionList implements SearchExpression
{

  protected List<SearchExpression> list = new ArrayList<SearchExpression>();

  public SearchExpressionList()
  {

  }

  public SearchExpressionList(List<SearchExpression> list)
  {
    this.list = list;
  }

  public List<String> getLookupWords()
  {
    List<String> ret = new ArrayList<String>();
    for (SearchExpression sr : list) {
      List<String> lw = sr.getLookupWords();
      if (lw != null) {
        ret.addAll(lw);
      }
    }
    return ret;
  }

  public List<SearchExpression> getList()
  {
    return list;
  }

  public void setList(List<SearchExpression> list)
  {
    this.list = list;
  }

}
