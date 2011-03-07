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
package de.micromata.genome.gwiki.controls;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.ZipRamFileSystem;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.plugin.GWikiPluginDescriptor;
import de.micromata.genome.gwiki.plugin.GWikiPluginRepository;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPluginDownloadActionBean extends ActionBeanBase
{
  public static class PDesc
  {
    private GWikiElementInfo elementInfo;

    private GWikiPluginDescriptor descriptor;

    public PDesc(GWikiElementInfo elementInfo, GWikiPluginDescriptor descriptor)
    {
      this.elementInfo = elementInfo;
      this.descriptor = descriptor;
    }

    public GWikiPluginDescriptor getDescriptor()
    {
      return descriptor;
    }

    public void setDescriptor(GWikiPluginDescriptor descriptor)
    {
      this.descriptor = descriptor;
    }

    public GWikiElementInfo getElementInfo()
    {
      return elementInfo;
    }

    public void setElementInfo(GWikiElementInfo elementInfo)
    {
      this.elementInfo = elementInfo;
    }

  }

  private Map<String, List<PDesc>> plugins = new TreeMap<String, List<PDesc>>();

  protected void initPluginAttachment(GWikiElementInfo atel)
  {
    GWikiElement el = wikiContext.getWikiWeb().getElement(atel);
    byte[] data = (byte[]) el.getMainPart().getCompiledObject();
    ZipRamFileSystem fs = new ZipRamFileSystem("", new ByteArrayInputStream(data));
    if (fs.exists("gwikiplugin.xml") == false) {
      return;
    }
    GWikiPluginDescriptor desc = GWikiPluginRepository.loadDescriptor(fs, "gwikiplugin.xml");
    if (desc == null) {
      return;
    }
    PDesc pdesc = new PDesc(atel, desc);
    String cat = StringUtils.defaultIfEmpty(desc.getCategory(), "Standard");
    List<PDesc> ld = plugins.get(cat);
    if (ld == null) {
      ld = new ArrayList<PDesc>();
      plugins.put(cat, ld);
    }
    ld.add(pdesc);
  }

  public void initPluginList()
  {
    List<GWikiElementInfo> el = wikiContext.getElementFinder().getAllDirectChildsByType(wikiContext.getCurrentElement().getElementInfo(),
        "attachment");
    for (GWikiElementInfo e : el) {
      initPluginAttachment(e);
    }
  }

  public Object onInit()
  {
    initPluginList();
    return null;
  }

  public Map<String, List<PDesc>> getPlugins()
  {
    return plugins;
  }

  public void setPlugins(Map<String, List<PDesc>> plugins)
  {
    this.plugins = plugins;
  }

}
