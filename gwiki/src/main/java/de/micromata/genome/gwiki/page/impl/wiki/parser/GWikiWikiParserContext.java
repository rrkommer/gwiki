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

package de.micromata.genome.gwiki.page.impl.wiki.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.ArrayStack;
import org.apache.log4j.Logger;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentTextDeco;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiMacroUnknown;
import de.micromata.genome.gwiki.utils.StringUtils;
import de.micromata.genome.gwiki.utils.html.Html2WikiFilter;

/**
 * State hold by the gwiki parser.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWikiParserContext
{
  private static final Logger LOG = Logger.getLogger(GWikiWikiParserContext.class);
  private List<List<GWikiFragment>> frags = new ArrayList<List<GWikiFragment>>();

  /**
   * Current DOM path of parsing.
   */
  private ArrayStack<GWikiFragment> fragStack = new ArrayStack<GWikiFragment>();

  private Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();
  public StringBuilder collectedText = new StringBuilder();
  private boolean ignoreWsNl = true;
  private static final String DEFAULT_SPECIAL_CHARACTERS = "*-_~^+{}[]!#|\\";

  /**
   * Character, which has to be escaped.
   */
  protected String specialCharacters = DEFAULT_SPECIAL_CHARACTERS;
  /**
   * Element id of current paresed page. may be null;
   */
  protected String currentPageId;

  public GWikiWikiParserContext()
  {
    macroFactories.putAll(Html2WikiFilter.TextDecoMacroFactories);
  }

  public GWikiWikiParserContext(String currentPageId)
  {
    this.currentPageId = currentPageId;
    macroFactories.putAll(Html2WikiFilter.TextDecoMacroFactories);
  }

  public GWikiWikiParserContext createChildParseContext()
  {
    GWikiWikiParserContext ret = new GWikiWikiParserContext(getCurrentPageId());
    ret.getMacroFactories().putAll(macroFactories);
    return ret;
  }

  public void addFragment(GWikiFragment frag)
  {
    final List<GWikiFragment> top = frags.get(frags.size() - 1);
    top.add(frag);
    if (LOG.isDebugEnabled() == true) {
      LOG.debug("+ " + frag + ": " + getFragsToString());
    }
  }

  public void addFragments(Collection<GWikiFragment> cfrags)
  {
    final List<GWikiFragment> top = frags.get(frags.size() - 1);
    top.addAll(cfrags);
    if (LOG.isDebugEnabled() == true) {
      LOG.debug("+ " + fragListToString(cfrags) + ": " + getFragsToString());
    }
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
    for (int i = frags.size() - 1; i >= 0; --i) {
      List<GWikiFragment> ltop = frags.get(i);
      if (ltop.size() > 0) {
        return ltop.get(ltop.size() - 1);
      }
    }
    return null;
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
    if (LOG.isDebugEnabled() == true) {
      LOG.debug("push ;" + getFragsToString());
    }
  }

  public void pushFragList(List<GWikiFragment> l)
  {
    frags.add(l);
    if (LOG.isDebugEnabled() == true) {
      LOG.debug("push " + fragListToString(l) + "; " + getFragsToString());
    }
  }

  public List<GWikiFragment> popFragList()
  {
    List<GWikiFragment> ret = frags.get(frags.size() - 1);
    frags.remove(frags.size() - 1);
    if (LOG.isDebugEnabled() == true) {
      LOG.debug("poped " + fragListToString(ret) + "; " + getFragsToString());
    }
    //    ret.toString();
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

  public GWikiFragment lastFrag()
  {
    if (frags.isEmpty() == true) {
      return null;
    }
    List<GWikiFragment> lf = frags.get(frags.size() - 1);
    if (lf.isEmpty() == true) {
      return null;
    }
    return lf.get(lf.size() - 1);

  }

  public void pushFragStack(GWikiFragment frag)
  {
    fragStack.push(frag);
  }

  public GWikiFragment popFragStack()
  {
    return fragStack.pop();
  }

  public GWikiFragment peekFragStack()
  {
    if (fragStack.isEmpty() == true) {
      return null;
    }
    return fragStack.peek();
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

  public String fragListToString(Collection<GWikiFragment> frags)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("[ ");
    for (GWikiFragment frag : frags) {
      sb.append(frag).append(", ");
    }
    sb.append(" ]");
    return sb.toString();
  }

  public String getFragsToString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("[ ");
    for (List<GWikiFragment> fragl : frags) {
      sb.append("[ ");
      for (GWikiFragment frag : fragl) {
        sb.append(frag).append(", ");
      }
      sb.append(" ]");
    }
    sb.append(" ]");
    return sb.toString();
  }

  public void addText(String text)
  {
    collectedText.append(text);
  }

  public void resetText()
  {
    collectedText.setLength(0);
  }

  public void flushText()
  {
    if (collectedText.length() == 0) {
      return;
    }
    String t = collectedText.toString();
    collectedText.setLength(0);
    GWikiFragment lf = lastFrag();

    if (t.length() > 0 && Character.isWhitespace(t.charAt(0)) == false) {
      if (lf instanceof GWikiFragmentTextDeco) {
        ((GWikiFragmentTextDeco) lf).setRequireMacroSyntax(true);
      }
    }
    if (ignoreWsNl == true) {
      String s = StringUtils.trim(t);
      if (StringUtils.isBlank(s) || StringUtils.isNewLine(s)) {
        return;
      }
    }
    // int cp = Character.codePointAt(t.toCharArray(), 0);
    if (t.startsWith("<!--") == true) {
      return;
    }
    if (StringUtils.isNewLine(t) == false) {
      addTextFragement(escapeText(t));
    }
  }

  public <T> T findFragInStack(Class<T> cls)
  {
    for (int i = 0; i < stackSize(); ++i) {
      List<GWikiFragment> fl = peek(i);
      if (fl.size() > 0) {
        GWikiFragment lr = fl.get(fl.size() - 1);
        if (cls.isAssignableFrom(lr.getClass()) == true) {
          return (T) lr;
        }
      }
    }
    return null;
  }

  public GWikiFragment findFragsInStack(Class<? extends GWikiFragment>... classes)
  {
    for (Class<? extends GWikiFragment> cls : classes) {
      GWikiFragment f = findFragInStack(cls);
      if (f != null) {
        return f;
      }
    }
    return null;
  }

  protected String escapeText(String t)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < t.length(); ++i) {
      char c = t.charAt(i);
      if (specialCharacters.indexOf(c) != -1) {
        sb.append('\\').append(c);
      } else {
        sb.append(c);
      }
    }
    return sb.toString();

  }

  public String getCollectedText()
  {
    return collectedText.toString();
  }

  public Map<String, GWikiMacroFactory> getMacroFactories()
  {
    return macroFactories;
  }

  public void setMacroFactories(Map<String, GWikiMacroFactory> macroFactories)
  {
    this.macroFactories = macroFactories;
  }

  public ArrayStack<GWikiFragment> getFragStack()
  {
    return fragStack;
  }

  public void setFragStack(ArrayStack<GWikiFragment> fragStack)
  {
    this.fragStack = fragStack;
  }

  public List<List<GWikiFragment>> getFrags()
  {
    return frags;
  }

  public void setFrags(List<List<GWikiFragment>> frags)
  {
    this.frags = frags;
  }

  public String getCurrentPageId()
  {
    return currentPageId;
  }

  public void setCurrentPageId(String currentPageId)
  {
    this.currentPageId = currentPageId;
  }

}
