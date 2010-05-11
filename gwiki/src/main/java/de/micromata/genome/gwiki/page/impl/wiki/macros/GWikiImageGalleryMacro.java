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
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiWithHeaderPrepare;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiImageGalleryMacro extends GWikiMacroBean implements GWikiWithHeaderPrepare
{
  private String title;

  private int columns;

  private String sort;

  private boolean reverse;

  private String size;

  private String include;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean#renderImpl(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.GWikiWithHeaderPrepare#prepareHeader(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  public void prepareHeader(GWikiContext ctx, MacroAttributes attrs)
  {
    ctx.getRequiredJs().add("static/js/jquery.thumbs.js");
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public int getColumns()
  {
    return columns;
  }

  public void setColumns(int columns)
  {
    this.columns = columns;
  }

  public String getSort()
  {
    return sort;
  }

  public void setSort(String sort)
  {
    this.sort = sort;
  }

  public boolean isReverse()
  {
    return reverse;
  }

  public void setReverse(boolean reverse)
  {
    this.reverse = reverse;
  }

  public String getSize()
  {
    return size;
  }

  public void setSize(String size)
  {
    this.size = size;
  }

  public String getInclude()
  {
    return include;
  }

  public void setInclude(String include)
  {
    this.include = include;
  }

}
