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

package de.micromata.genome.gwiki.page.impl.wiki.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.ArrayStack;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiMacroUnknown;

/**
 * State hold by the gwiki parser.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWikiParserContext
{
  private List<List<GWikiFragment>> frags = new ArrayList<List<GWikiFragment>>();

  private ArrayStack<GWikiFragment> fragStack = new ArrayStack<GWikiFragment>();

  private Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();

  public GWikiWikiParserContext()
  {

  }

  public void addFragment(GWikiFragment frag)
  {
    final List<GWikiFragment> top = frags.get(frags.size() - 1);
    top.add(frag);
  }

  public void addFragments(Collection<GWikiFragment> cfrags)
  {
    final List<GWikiFragment> top = frags.get(frags.size() - 1);
    top.addAll(cfrags);
  }

  public GWikiFragment lastDefinedFragment()
  {
    for (int i = frags.size() - 1; i >= 0; --i) {
      List<GWikiFragment> top = frags.get(i);
      if (top.size() > 0) {
        return top.get(top.size() - 1);
      }
    }
    return null;
  }

  public GWikiFragment lastFragment()
  {
    if (frags.size() == 0) {
      return null;
    }
    final List<GWikiFragment> top = frags.get(frags.size() - 1);
    if (top.size() == 0) {
      return null;
    }
    return top.get(top.size() - 1);
  }

  public List<GWikiFragment> peek(int i)
  {
    return frags.get(frags.size() - 1 - i);
  }

  public int stackSize()
  {
    return frags.size();
  }

  public void addTextFragement(String text)
  {
    if (frags.size() > 0) {
      List<GWikiFragment> cf = frags.get(frags.size() - 1);
      if (cf.size() > 0 && cf.get(cf.size() - 1) instanceof GWikiFragmentText) {
        GWikiFragmentText lt = ((GWikiFragmentText) cf.get(cf.size() - 1));
        lt.addText(text);
        return;
      }
    }
    addFragment(new GWikiFragmentText(text));
  }

  public void pushFragList()
  {
    frags.add(new ArrayList<GWikiFragment>());
  }

  public List<GWikiFragment> popFragList()
  {
    List<GWikiFragment> ret = frags.get(frags.size() - 1);
    frags.remove(frags.size() - 1);
    return ret;
  }

  public GWikiFragment lastParentFrag()
  {
    if (frags.size() < 2) {
      return null;
    }
    List<GWikiFragment> rl = frags.get(frags.size() - 2);
    if (rl.isEmpty() == true) {
      return null;
    }
    return rl.get(rl.size() - 1);
  }

  public void pushFragStack(GWikiFragment frag)
  {
    fragStack.push(frag);
  }

  public GWikiFragment popFragStack()
  {
    return fragStack.pop();
  }

  public GWikiMacroFragment createMacro(MacroAttributes ma)
  {
    GWikiMacroFactory mf = macroFactories.get(ma.getCmd());
    GWikiMacro macro = null;
    if (mf != null) {
      macro = mf.createInstance();
    } else {
      macro = new GWikiMacroUnknown();
    }
    return new GWikiMacroFragment(macro, ma);
  }

  public void replaceFragment(GWikiFragment search, GWikiFragment repl)
  {
    for (int i = 0; i < fragStack.size(); ++i) {
      if (fragStack.get(i) == search) {
        fragStack.set(i, repl);
      }
    }
    for (List<GWikiFragment> fl : frags) {
      for (int i = 0; i < fl.size(); ++i) {
        if (fl.get(i) == search) {
          fl.set(i, repl);
        }
      }
    }
  }

  public Map<String, GWikiMacroFactory> getMacroFactories()
  {
    return macroFactories;
  }

  public void setMacroFactories(Map<String, GWikiMacroFactory> macroFactories)
  {
    this.macroFactories = macroFactories;
  }
}
