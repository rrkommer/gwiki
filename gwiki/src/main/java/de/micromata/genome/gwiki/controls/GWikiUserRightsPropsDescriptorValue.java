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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.GWikiAuthorizationExt;
import de.micromata.genome.gwiki.model.GWikiRight;
import de.micromata.genome.gwiki.page.impl.GWikiPropsDescriptorValue;
import de.micromata.genome.gwiki.page.impl.GWikiPropsEditorArtefakt;
import de.micromata.genome.gwiki.page.impl.PropsEditContext;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiUserRightsPropsDescriptorValue extends GWikiPropsDescriptorValue
{

  private static final long serialVersionUID = 8887148894696467389L;

  @SuppressWarnings("unchecked")
  @Override
  public void parseRequest(PropsEditContext pct)
  {
    List<String> selrights = new ArrayList<String>();
    Map<String, String[]> pm = pct.getWikiContext().getRequest().getParameterMap();
    // String rv = pct.getRequestValue();
    for (Map.Entry<String, String[]> me : pm.entrySet()) {
      if (me.getKey().startsWith("right.") == false) {
        continue;
      }
      selrights.add(me.getKey().substring("right.".length()));
    }
    String rl = StringUtils.join(selrights.toArray(), ",");

    pct.setPropsValue(rl);
  }

  @Override
  public String render(GWikiPropsEditorArtefakt< ? > editor, PropsEditContext pct)
  {
    GWikiAuthorization auth = pct.getWikiContext().getWikiWeb().getAuthorization();
    if ((auth instanceof GWikiAuthorizationExt) == false) {
      return super.render(editor, pct);
    }
    GWikiAuthorizationExt authx = (GWikiAuthorizationExt) auth;
    SortedMap<String, GWikiRight> systemRights = authx.getSystemRights(pct.getWikiContext());
    String pval = pct.getPropsValue();
    SortedMap<String, GWikiRight> usr = authx.getUserRight(pct.getWikiContext(), systemRights, pval);
    for (Map.Entry<String, GWikiRight> me : usr.entrySet()) {
      if (systemRights.containsKey(me.getKey()) == false) {
        systemRights.put(me.getKey(), me.getValue());
      }
    }
    Map<String, List<GWikiRight>> groups = new TreeMap<String, List<GWikiRight>>();

    for (Map.Entry<String, GWikiRight> me : systemRights.entrySet()) {
      List<GWikiRight> gr = groups.get(me.getValue().getCategory());
      if (gr == null) {
        gr = new ArrayList<GWikiRight>();
        groups.put(me.getValue().getCategory(), gr);
      }
      gr.add(me.getValue());
    }

    StringBuilder sb = new StringBuilder();
    sb.append("<ul>\n");

    for (Map.Entry<String, List<GWikiRight>> ge : groups.entrySet()) {
      sb.append("<li>").append(ge.getKey()).append("<br/><ul>");
      for (GWikiRight r : ge.getValue()) {
        if (r.isPrivateRight() == true) {
          continue;
        }
        sb.append("<li><input type=\"checkbox\" name=\"right.").append(r.getName()).append("\"");
        if (usr.containsKey(r.getName()) == true) {
          sb.append(" checked=\"checked\"");
        }
        sb.append(">");
        if (StringUtils.isNotBlank(r.getDescription()) == true) {
          sb.append("<a href=\"#\" title=\"")
              //
              .append(StringEscapeUtils.escapeXml(r.getDescription())).append("\">").append(StringEscapeUtils.escapeXml(r.getName()))
              .append("</a>");
        } else {
          sb.append(StringEscapeUtils.escapeXml(r.getName()));
        }
        sb.append("</li>\n");
      }
      sb.append("</ul></li>\n");
    }
    // for (Map.Entry<String, GWikiRight> me : systemRights.entrySet()) {
    //      
    // }
    sb.append("</ul>\n");
    return sb.toString();// + super.render(editor, pct);
  }

  @Override
  public void validate(PropsEditContext pct)
  {
    super.validate(pct);
  }

}
