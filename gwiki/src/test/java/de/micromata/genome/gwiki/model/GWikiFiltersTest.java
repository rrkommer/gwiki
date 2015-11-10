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
package de.micromata.genome.gwiki.model;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import de.micromata.genome.gwiki.model.filter.GWikiFilters;
import de.micromata.genome.gwiki.plugin.GWikiPluginFilterDescriptor;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFiltersTest extends TestCase
{
  public void testSort()
  {
    List<GWikiPluginFilterDescriptor> pluginFilters = new ArrayList<GWikiPluginFilterDescriptor>();
    GWikiPluginFilterDescriptor pf = new GWikiPluginFilterDescriptor("pluginfilter");
    pluginFilters.add(pf);
    GWikiFilters filters = new GWikiFilters();
    List<String> regClasses = new ArrayList<String>();
    regClasses.add("coreplugin");
    List<String> sf = filters.getSortedClasses(regClasses, pluginFilters);
    assertEquals(sf.get(0), "coreplugin");

  }

  public void testSort2()
  {
    List<GWikiPluginFilterDescriptor> pluginFilters = new ArrayList<GWikiPluginFilterDescriptor>();
    GWikiPluginFilterDescriptor pf = new GWikiPluginFilterDescriptor("pluginfilter");
    pf.getAfter().add("coreplugin");
    pluginFilters.add(pf);
    GWikiFilters filters = new GWikiFilters();
    List<String> regClasses = new ArrayList<String>();
    regClasses.add("coreplugin");
    List<String> sf = filters.getSortedClasses(regClasses, pluginFilters);
    assertEquals(sf.get(0), "coreplugin");
  }

  public void testSort3()
  {
    List<GWikiPluginFilterDescriptor> pluginFilters = new ArrayList<GWikiPluginFilterDescriptor>();
    GWikiPluginFilterDescriptor pf = new GWikiPluginFilterDescriptor("pluginfilter");
    pf.getBefore().add("coreplugin");
    pluginFilters.add(pf);
    GWikiFilters filters = new GWikiFilters();
    List<String> regClasses = new ArrayList<String>();
    regClasses.add("coreplugin");
    List<String> sf = filters.getSortedClasses(regClasses, pluginFilters);
    assertEquals(sf.get(1), "coreplugin");
  }
}
