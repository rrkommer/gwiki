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

import java.util.HashMap;
import java.util.Map;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;

/**
 * Visitor to find macros with name and args.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiCollectMacroFragmentVisitor extends GWikiCollectFragmentTypeVisitor
{
  private String macroName;

  /**
   * Key name of macro attribute, value matcher expression.
   */
  private Map<String, String> attributeMatcher;

  private Map<String, Matcher<String>> attrMatcher = null;

  public GWikiCollectMacroFragmentVisitor(String macroName)
  {
    super(GWikiMacroFragment.class);
    this.macroName = macroName;
  }

  public GWikiCollectMacroFragmentVisitor(String macroName, Map<String, String> attributeMatcher)
  {
    super(GWikiMacroFragment.class);
    this.macroName = macroName;
    setAttributeMatcher(attributeMatcher);

  }

  protected void addFragment(GWikiFragment fragment)
  {
    found.add(fragment);
  }

  @Override
  public void begin(GWikiFragment fragment)
  {
    if (classToFind.isAssignableFrom(fragment.getClass()) == false) {
      return;
    }
    GWikiMacroFragment mf = (GWikiMacroFragment) fragment;
    if (mf.getAttrs().getCmd().equals(macroName) == false) {
      return;
    }
    if (attrMatcher != null) {
      for (Map.Entry<String, Matcher<String>> me : attrMatcher.entrySet()) {
        String val = mf.getAttrs().getArgs().getStringValue(me.getKey());
        if (me.getValue().match(val) == false) {
          return;
        }
      }

    }
    addFragment(fragment);
  }

  public String getMacroName()
  {
    return macroName;
  }

  public void setMacroName(String macroName)
  {
    this.macroName = macroName;
  }

  public Map<String, String> getAttributeMatcher()
  {
    return attributeMatcher;
  }

  public void setAttributeMatcher(Map<String, String> attributeMatcher)
  {
    if (attributeMatcher == null) {
      this.attrMatcher = null;
    } else {
      BooleanListRulesFactory<String> fac = new BooleanListRulesFactory<String>();
      attrMatcher = new HashMap<String, Matcher<String>>(attributeMatcher.size());
      for (Map.Entry<String, String> me : attributeMatcher.entrySet()) {
        attrMatcher.put(me.getKey(), fac.createMatcher(me.getValue()));
      }
    }
    this.attributeMatcher = attributeMatcher;
  }

  public Map<String, Matcher<String>> getAttrMatcher()
  {
    return attrMatcher;
  }

  public void setAttrMatcher(Map<String, Matcher<String>> attrMatcher)
  {
    this.attrMatcher = attrMatcher;
  }
}
