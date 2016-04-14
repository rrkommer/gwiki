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

package de.micromata.genome.gwiki.plugin.wikilink_1_0;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;

/**
 * Wrapper to admin/config/WikiLinkConfig
 * 
 * @author roger
 * 
 */
public class GWikiWikiLinkConfig
{
  GWikiContext wikiContext;

  boolean enabled = false;

  Matcher<String> matcher;

  public GWikiWikiLinkConfig(GWikiContext wikiContext)
  {
    this.wikiContext = wikiContext;
    GWikiElement el = wikiContext.getWikiWeb().findElement("admin/config/WikiLinkConfig");
    if (el == null) {
      return;
    }
    Object co = el.getMainPart().getCompiledObject();
    if ((co instanceof GWikiProps) == false) {
      return;
    }
    GWikiProps pco = (GWikiProps) co;

    enabled = pco.getBooleanValue("WIKILINK_DISABLE", false) == false;
    String m = pco.getStringValue("WIKILINK_PAGEIDMATCHER", "+*,-admin/*,-edit/*");
    if (StringUtils.isNotEmpty(m) == true) {
      matcher = new BooleanListRulesFactory<String>().createMatcher(m);
    }

  }

  public boolean isWikiPageEnabled(GWikiElement el)
  {
    if (isEnabled() == false) {
      return false;
    }
    if (matcher == null) {
      return true;
    }
    String id = el.getElementInfo().getId();
    return matcher.match(id);
  }

  public boolean isEnabled()
  {
    return enabled;
  }

  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
  }
}
