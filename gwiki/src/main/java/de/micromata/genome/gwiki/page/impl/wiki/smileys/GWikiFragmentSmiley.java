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

package de.micromata.genome.gwiki.page.impl.wiki.smileys;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBase;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiFragmentSmiley extends GWikiFragmentBase
{
  private GWikiSmileyInfo smileyInfo;

  public GWikiFragmentSmiley(GWikiSmileyInfo smileyInfo)
  {
    this.smileyInfo = smileyInfo;
  }

  @Override
  public boolean render(GWikiContext ctx)
  {
    String styleClass = "gwikiSmiley";

    boolean rte = RenderModes.ForRichTextEdit.isSet(ctx.getRenderMode());
    if (rte == true) {
      ctx.append(
          "<span class='mceNonEditable' style='display: inline-block; height:18px; width:18px; background-image: url("
              + ctx.localUrl(smileyInfo.getSource())
              + ")' data-wiki-smiley='" + smileyInfo.getShortName() + "'>&nbsp;</span>");
      return true;
    }

    ctx.append("<img data-wiki-smiley='").append(smileyInfo.getShortName()).append("'");
    ctx.append(" class='").append(styleClass).append("' src='")
        .append(ctx.localUrl(smileyInfo.getSource())).append("'/>");
    return true;
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    if (StringUtils.isNotBlank(smileyInfo.getShortCut()) == true) {
      sb.append("(" + smileyInfo.getShortCut() + ")");
    } else {
      sb.append("(" + smileyInfo.getShortName() + ")");
    }

  }

  @Override
  public int getRenderModes()
  {
    return GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.InTextFlow);
  }

  @Override
  public void ensureRight(GWikiContext ctx) throws AuthorizationFailedException
  {
  }

  @Override
  public List<GWikiFragment> getChilds()
  {
    return new ArrayList<>();
  }

}
