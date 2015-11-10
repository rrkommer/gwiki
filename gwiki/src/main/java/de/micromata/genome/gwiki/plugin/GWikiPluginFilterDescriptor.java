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
package de.micromata.genome.gwiki.plugin;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.util.types.Converter;

/**
 * Descriptor for a filter.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPluginFilterDescriptor
{
  /**
   * the class name of the filter.
   */
  private String className;

  /**
   * List of class names, where this filter should be before.
   */
  private List<String> before = new ArrayList<String>();

  /**
   * List of class anme, where this filter should be after.
   */
  private List<String> after = new ArrayList<String>();

  public GWikiPluginFilterDescriptor()
  {

  }

  public GWikiPluginFilterDescriptor(String className)
  {
    this.className = className;
  }

  public GWikiPluginFilterDescriptor(String className, String before, String after)
  {
    this(className);
    if (before != null) {
      this.before = Converter.parseStringTokens(before, ",", false);
    }
    if (after != null) {
      this.after = Converter.parseStringTokens(after, ",", false);
    }
  }

  public String getClassName()
  {
    return className;
  }

  public void setClassName(String className)
  {
    this.className = className;
  }

  public List<String> getBefore()
  {
    return before;
  }

  public void setBefore(List<String> before)
  {
    this.before = before;
  }

  public List<String> getAfter()
  {
    return after;
  }

  public void setAfter(List<String> after)
  {
    this.after = after;
  }
}
