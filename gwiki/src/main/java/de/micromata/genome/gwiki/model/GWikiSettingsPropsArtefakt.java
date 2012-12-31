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

package de.micromata.genome.gwiki.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.GWikiIndexedArtefakt;
import de.micromata.genome.gwiki.utils.AppendableI;

@SuppressWarnings("unchecked")
public class GWikiSettingsPropsArtefakt extends GWikiPropsArtefakt implements GWikiIndexedArtefakt
{
  private static final long serialVersionUID = -7444414246579867245L;

  public GWikiSettingsPropsArtefakt()
  {
    this(new GWikiSettingsProps());
  }

  public GWikiSettingsPropsArtefakt(GWikiProps props)
  {
    super(props);
  }

  public void getPreview(GWikiContext ctx, AppendableI sb)
  {
    // StringBuilder sb = new StringBuilder();
    Map<String, String> m = getStorageData();
    String title = m.get(GWikiPropKeys.TITLE);
    if (StringUtils.isNotBlank(title) == true) {
      title = ctx.getTranslatedProp(title);
      sb.append("<h1 class=\"gwikititle\">").append(StringEscapeUtils.escapeHtml(title)).append("</h1>");
    }
    List<String> keywords = new GWikiProps(m).getStringList(GWikiPropKeys.KEYWORDS);
    if (keywords != null && keywords.isEmpty() == false) {
      sb.append("<ul>");
      for (String kw : keywords) {
        sb.append("<li class=\"gwikikeyword\">").append(StringEscapeUtils.escapeHtml(kw)).append("</li>\n");
      }
      sb.append("</ul>");
    }
  }

  private void cleanupEmptyProps(GWikiProps props)
  {
    Set<String> keys = new HashSet<String>();
    keys.addAll(props.getMap().keySet());
    for (String key : keys) {
      if (StringUtils.isEmpty(props.getStringValue(key)) == true) {
        props.remove(key);
      }
    }
  }

  private void cleanupSuperflousEntrys(GWikiProps props)
  {
    props.remove(GWikiPropKeys.PAGEID);
    // do not use this, because of type is used in search
    // props.remove(GWikiPropKeys.TYPE);
  }

  @Override
  public Map<String, String> getStorageData()
  {
    GWikiProps props = getCompiledObject();
    cleanupEmptyProps(props);
    cleanupSuperflousEntrys(props);
    return props.getMap();
  }
}
