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
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de) TODO not documented.
 */
@MacroInfo(info = "Render a list of change comments", renderFlags = { GWikiMacroRenderFlags.TrimTextContent })
public class GWikiChangeCommentMacroBean extends GWikiMacroBean implements GWikiBodyEvalMacro
{

  private static final long serialVersionUID = -3551578834533228553L;

  private String modifiedBy;

  private String modifiedAt;

  private int version;

  public GWikiChangeCommentMacroBean()
  {
    setRenderModesFromAnnot();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean#renderImpl(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    ctx.append("<fieldset><legend>");
    ctx.appendEscText(modifiedBy).append(", ").appendEscText(ctx.getUserDateString(GWikiProps.string2date(modifiedAt)))
        .append(", (")
        .append(version).append(")");
    ctx.append("</legend>");
    if (attrs.getChildFragment() != null) {
      attrs.getChildFragment().render(ctx);
    }
    ctx.append("</fieldset>");
    return true;
  }

  public String getModifiedBy()
  {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy)
  {
    this.modifiedBy = modifiedBy;
  }

  public String getModifiedAt()
  {
    return modifiedAt;
  }

  public void setModifiedAt(String modifiedAt)
  {
    this.modifiedAt = modifiedAt;
  }

  public int getVersion()
  {
    return version;
  }

  public void setVersion(int version)
  {
    this.version = version;
  }

}
