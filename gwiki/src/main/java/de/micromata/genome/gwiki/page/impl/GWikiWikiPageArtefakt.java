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

package de.micromata.genome.gwiki.page.impl;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.GWikiIndexedArtefakt;

/**
 * Artefakt containing a GWiki Text.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWikiPageArtefakt extends GWikiWikiPageBaseArtefakt implements GWikiIndexedArtefakt,
    GWikiExecutableArtefakt<GWikiContent>, GWikiEditableArtefakt
{

  private static final long serialVersionUID = 4468622483428547577L;

  @Override
  public void prepareHeader(GWikiContext wikiContext)
  {
    //    wikiContext.getRequiredJs().add("static/js/jquery.thumbs.js");
    wikiContext.getRequiredJs().add("static/prism/prism.js");
    wikiContext.getRequiredCss().add("static/prism/prism.css");
    super.prepareHeader(wikiContext);
    GWikiElementInfo ei = wikiContext.getCurrentElement().getElementInfo();
    if (wikiContext.getWikiWeb().getAuthorization().isAllowToEdit(wikiContext, ei) == false) {
      return;
    }
    wikiContext.getRequiredJs().add("static/wedit/gwiki_edit_hook.js");

    //    Object attr = wikiContext.getRequestAttribute("gwiki_edit_e_key_added");
    //    if (attr != null) {
    //      return;
    //    }
    //
    //    String editByE = "<script type=\"text/javascript\">\n" +
    //        "$(document).keydown(function (event) {\n" +
    //        "   if (event.which == 69) {\n" +
    //        "     window.location.href = '" + wikiContext.localUrl("edit/EditPage") + "?pageId="
    //        + wikiContext.escapeUrlParam(ei.getId())
    //        + "';\n"
    //        +
    //        "   }\n" +
    //        "});\n</script>\n";
    //
    //    wikiContext.getRequiredHeader().add(editByE);
    //    wikiContext.setRequestAttribute("gwiki_edit_e_key_added", Boolean.TRUE);
  }

}
