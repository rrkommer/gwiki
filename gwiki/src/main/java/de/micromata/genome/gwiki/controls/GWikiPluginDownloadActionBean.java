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

package de.micromata.genome.gwiki.controls;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gdbfs.ZipRamFileSystem;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;
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

  private boolean finalRelease;

  private boolean betaRelease;

  private boolean alphaRelease;

  private boolean experimentalRelease;

  public static String getDetailPage(GWikiContext wikiContext, GWikiPluginDescriptor pdesc)
  {
    String pluginId = pdesc.getPluginId();
    String parenPageId = "gwikidocs/plugins/en/GWiki_Plugins";
    GWikiElementInfo pei = wikiContext.getWikiWeb().findElementInfo(parenPageId);
    if (pei == null) {
      return null;
    }
    for (GWikiElementInfo ei : wikiContext.getElementFinder().getAllDirectChilds(pei)) {
      String id = ei.getId();
      String n = FileNameUtils.getNamePart(id);
      if (StringUtils.equals(n, pluginId) == true) {
        return id;
      }
    }
    return null;
  }

  protected void initPluginAttachment(GWikiElementInfo atel)
  {
    GWikiElement el = wikiContext.getWikiWeb().getElement(atel);
    byte[] data = (byte[]) el.getMainPart().getCompiledObject();
    if (data == null) {
      GWikiLog.warn("Plugin zip has no attachment bytes: " + el.getElementInfo().getId());
      return;
    }
    ZipRamFileSystem fs = new ZipRamFileSystem("", new ByteArrayInputStream(data));
    if (fs.exists("gwikiplugin.xml") == false) {
      return;
    }
    GWikiPluginDescriptor desc = GWikiPluginRepository.loadDescriptor(fs, "gwikiplugin.xml");
    if (desc == null) {
      return;
    }
    String versionState = desc.getVersionState();
    if (StringUtils.equals(versionState, "Final") == true && finalRelease == false) {
      return;
    }
    if (StringUtils.equals(versionState, "Beta") == true && betaRelease == false) {
      return;
    }
    if (StringUtils.equals(versionState, "Alpha") == true && alphaRelease == false) {
      return;
    }
    if (StringUtils.equals(versionState, "Experimental") == true && experimentalRelease == false) {
      return;
    }
    PDesc pdesc = new PDesc(atel, desc);
    if (desc.getDescriptionPath() == null) {
      desc.setDescriptionPath(getDetailPage(wikiContext, pdesc.getDescriptor()));
    }

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
    if (finalRelease == false && betaRelease == false && alphaRelease == false && experimentalRelease == false) {
      finalRelease = true;
    }
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

  public boolean isFinalRelease()
  {
    return finalRelease;
  }

  public void setFinalRelease(boolean finalRelease)
  {
    this.finalRelease = finalRelease;
  }

  public boolean isBetaRelease()
  {
    return betaRelease;
  }

  public void setBetaRelease(boolean betaRelease)
  {
    this.betaRelease = betaRelease;
  }

  public boolean isAlphaRelease()
  {
    return alphaRelease;
  }

  public void setAlphaRelease(boolean alphaRelease)
  {
    this.alphaRelease = alphaRelease;
  }

  public boolean isExperimentalRelease()
  {
    return experimentalRelease;
  }

  public void setExperimentalRelease(boolean experimentalRelease)
  {
    this.experimentalRelease = experimentalRelease;
  }

}
