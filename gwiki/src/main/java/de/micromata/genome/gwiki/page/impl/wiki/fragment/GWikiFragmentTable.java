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

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * Render a table.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFragmentTable extends GWikiFragmentChildsBase
{
  private static final long serialVersionUID = 4498961495094305174L;

  public static class TableElement
  {
    public MacroAttributes attributes;

    public TableElement(String name)
    {
      attributes = new MacroAttributes(name);
    }

    public MacroAttributes getAttributes()
    {
      return attributes;
    }

  }

  public static class Cell extends TableElement
  {

    public Cell(String name, GWikiFragmentChildContainer childs)
    {
      super(name);
      this.attributes.setChildFragment(childs);
    }
  }

  public static class Row extends TableElement
  {
    public List<Cell> cells = new ArrayList<Cell>();

    public Row()
    {
      super("tr");
    }

    public void addCell(Cell cell)
    {
      cells.add(cell);
    }
  }

  private MacroAttributes attributes = new MacroAttributes("table");

  private List<Row> rows = new ArrayList<Row>();

  // private List<List<Pair<String, GWikiFragmentChildContainer>>> table = new ArrayList<List<Pair<String, GWikiFragmentChildContainer>>>();

  @Override
  public boolean render(GWikiContext ctx)
  {
    ctx.append("<table class=\"gwikiTable\"><tbody>");
    for (Row row : rows) {
      ctx.append("<tr>\n");
      for (Cell c : row.cells) {
        String ttag = c.getAttributes().getCmd();
        ctx.append("<").append(ttag).append(" class=\"gwiki" + ttag + "\">");
        c.getAttributes().getChildFragment().render(ctx);
        ctx.append("</").append(ttag).append(">");
      }
      ctx.append("\n</tr>");
    }
    ctx.append("</tbody></table>");
    return true;
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    for (Row row : rows) {
      String lastTdTh = "";
      for (Cell c : row.cells) {
        lastTdTh = c.getAttributes().getCmd();
        if (lastTdTh.equals("th") == true) {
          sb.append("||");
        } else {
          sb.append("|");
        }
        c.getAttributes().getChildFragment().getSource(sb);
      }
      if (lastTdTh.equals("th") == true) {
        sb.append("||");
      } else {
        sb.append("|");
      }
      sb.append("\n");
    }
  }

  @Override
  public void iterate(GWikiFragmentVisitor visitor)
  {
    visitor.begin(this);
    for (Row row : rows) {
      for (Cell c : row.cells) {
        c.getAttributes().getChildFragment().iterate(visitor);
      }
    }
    visitor.end(this);
  }

  @Override
  public int getRenderModes()
  {
    return GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NoWrapWithP, GWikiMacroRenderFlags.NewLineBeforeEnd);
  }

  public void addRow(Row row)
  {
    rows.add(row);
  }

  public Cell addCell(String tag)
  {
    Cell cell = new Cell(tag, new GWikiFragmentChildContainer());
    rows.get(rows.size() - 1).addCell(cell);
    return cell;
  }

  public void addCellContent(List<GWikiFragment> frags)
  {
    Row row = rows.get(rows.size() - 1);
    Cell cell = row.cells.get(row.cells.size() - 1);
    cell.attributes.getChildFragment().addChilds(frags);
  }

  public int getRowSize()
  {
    return rows.size();
  }

  public List<Row> getRows()
  {
    return rows;
  }

  public MacroAttributes getAttributes()
  {
    return attributes;
  }

}
