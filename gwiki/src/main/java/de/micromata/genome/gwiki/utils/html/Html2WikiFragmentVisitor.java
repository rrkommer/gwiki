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

package de.micromata.genome.gwiki.utils.html;

import java.util.List;

import org.apache.commons.collections15.ArrayStack;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildsBase;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentTable;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyTagMacro;
import de.micromata.genome.util.types.Holder;

/**
 * Internal visitor to transform html to wiki.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class Html2WikiFragmentVisitor implements GWikiFragmentVisitor
{
  public ArrayStack<GWikiFragment> stack = new ArrayStack<GWikiFragment>();

  protected void copyAttributes(MacroAttributes target, MacroAttributes source)
  {
    target.getArgs().getMap().putAll(source.getArgs().getMap());
  }

  protected GWikiFragment convertTableToHtmlTable(GWikiFragmentTable table)
  {
    MacroAttributes ma = new MacroAttributes("table");
    ma.getArgs().setStringValue("class", "gwikiTable");
    // new GWikiHtmlBodyTagMacro(macroRenderModes);
    GWikiHtmlBodyTagMacro tm = (GWikiHtmlBodyTagMacro) GWikiHtmlBodyTagMacro.table().createInstance();
    GWikiMacroFragment tableFrag = new GWikiMacroFragment(tm, ma);
    for (GWikiFragmentTable.Row tr : table.getRows()) {
      GWikiHtmlBodyTagMacro row = (GWikiHtmlBodyTagMacro) GWikiHtmlBodyTagMacro.tr().createInstance();
      MacroAttributes rowma = new MacroAttributes("tr");
      copyAttributes(rowma, tr.getAttributes());
      if (tr.getAttributes().getArgs().size() == 0) {
        rowma.getArgs().setStringValue("class", "gwikitr");
      }
      GWikiMacroFragment rowFrag = new GWikiMacroFragment(row, rowma);
      tableFrag.addChild(rowFrag);
      for (GWikiFragmentTable.Cell c : tr.cells) {

        MacroAttributes tdma = new MacroAttributes(c.getAttributes().getCmd());
        copyAttributes(tdma, c.getAttributes());
        if (tdma.getArgs().isEmpty() == true) {
          tdma.getArgs().setStringValue("class", "gwiki" + tdma.getCmd());
        }
        GWikiHtmlBodyTagMacro cell = (GWikiHtmlBodyTagMacro) GWikiHtmlBodyTagMacro.td().createInstance();
        GWikiMacroFragment cellFrag = new GWikiMacroFragment(cell, tdma);

        cellFrag.addChild(c.getAttributes().getChildFragment());

        rowFrag.addChild(cellFrag);
      }
    }

    return tableFrag;
  }

  protected boolean hasChildTable(GWikiFragment fragment)
  {
    final Holder<GWikiFragmentTable> foundTable = new Holder<GWikiFragmentTable>(null);
    GWikiFragmentVisitor visitor = new GWikiFragmentVisitor() {

      public void begin(GWikiFragment fragment)
      {
        if (fragment instanceof GWikiFragmentTable) {
          foundTable.set((GWikiFragmentTable) fragment);
        }
      }

      public void end(GWikiFragment fragment)
      {

      }
    };
    fragment.iterate(visitor);
    return foundTable.get() != fragment;
  }

  protected boolean isTable(GWikiFragment fr)
  {
    if (fr instanceof GWikiFragmentTable) {
      return true;
    }
    if (fr instanceof GWikiMacroFragment) {
      GWikiMacroFragment ht = (GWikiMacroFragment) fr;
      if (ht.getAttrs().getCmd().equals("table") == true) {
        return true;
      }
    }
    return false;
  }

  protected boolean hasParentTable()
  {
    for (GWikiFragment fr : stack) {
      if (isTable(fr) == true) {
        return true;
      }
    }
    return false;
  }

  protected boolean hasOnlyClassAttribute(MacroAttributes attr, String classStyle)
  {
    if (attr.getArgs().size() > 1) {
      return false;
    }
    if (attr.getArgs().size() == 1) {
      if (StringUtils.equals(attr.getArgs().getStringValue("class"), classStyle) == false) {
        return false;
      }
    }
    return true;
  }

  protected boolean hasForeignAttributes(GWikiFragmentTable table)
  {
    if (hasOnlyClassAttribute(table.getAttributes(), "gwikitable") == false) {
      return true;
    }

    for (GWikiFragmentTable.Row row : table.getRows()) {
      if (hasOnlyClassAttribute(row.getAttributes(), "gwikitr") == false) {
        return true;
      }
      for (GWikiFragmentTable.Cell cell : row.cells) {
        if (hasOnlyClassAttribute(cell.getAttributes(), "gwikith") == false
            && hasOnlyClassAttribute(cell.getAttributes(), "gwikitd") == false) {
          return true;
        }
      }
    }
    return false;
  }

  public void begin(GWikiFragment fragment)
  {
    if (fragment instanceof GWikiFragmentTable) {
      GWikiFragmentTable table = (GWikiFragmentTable) fragment;
      if (hasChildTable(fragment) == true || hasParentTable() == true || hasForeignAttributes(table) == true) {
        GWikiFragment htmltable = convertTableToHtmlTable(table);
        List<GWikiFragment> childs = stack.peek().getChilds();
        for (int i = 0; i < childs.size(); ++i) {
          if (childs.get(i) == table) {
            childs.set(i, htmltable);
          }
        }
        fragment = htmltable;
      }
    }

    stack.push(fragment);
  }

  public void end(GWikiFragment fragment)
  {
    stack.pop();
    if (fragment instanceof GWikiMacroFragment) {
      GWikiMacroFragment mf = (GWikiMacroFragment) fragment;
      if (StringUtils.equals("div", mf.getAttrs().getCmd()) == true
          && StringUtils.equals("gwikiContent", mf.getAttrs().getArgs().getStringValue("class")) == true) {
        GWikiFragment pf = stack.peek(1);
        if (pf instanceof GWikiFragmentChildsBase) {
          ((GWikiFragmentChildsBase) pf).replaceChilds(fragment, mf.getChilds());
        }
      }
    }
  }

}
