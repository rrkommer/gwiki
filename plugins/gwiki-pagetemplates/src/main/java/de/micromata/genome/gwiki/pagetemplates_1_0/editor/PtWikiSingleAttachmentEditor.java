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
package de.micromata.genome.gwiki.pagetemplates_1_0.editor;

import static de.micromata.genome.util.xml.xmlbuilder.Xml.attrs;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.text;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.input;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.table;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.td;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.tr;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;
import de.micromata.genome.gwiki.page.impl.wiki.parser.WikiParserUtils;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;

/**
 * @author Christian Claus (c.claus@micromata.de)
 */
public class PtWikiSingleAttachmentEditor extends PtWikiUploadEditor
{

  private static final long serialVersionUID = 5901053792188232570L;

  public PtWikiSingleAttachmentEditor(GWikiElement element, String sectionName, String editor, String hint)
  {
    super(element, sectionName, editor, hint);
    
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiArtefaktBase#renderWithParts(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean renderWithParts(GWikiContext ctx)  
  {
    final String discover = ctx.getTranslated("gwiki.editor.image.browse");
      
    XmlElement inputFile = input( //
        attrs("name", sectionName, "type", "file", "size", "50", "accept", "*"));
    
    XmlElement inputTitle = input( //
        attrs("name", "title"));
   
    XmlElement table = table( //
        attrs()).nest( //
            tr( //
                td(text(discover)), //
                td(inputFile) //
            ), //
            tr( //
                td(text("Title: ")), //
                td(inputTitle) //
            )
        );
    
    ctx.append(table.toString());
    
    return true;
  }

  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt#onSave(de.micromata.genome.gwiki.page.GWikiContext)
   */
  public void onSave(GWikiContext ctx) 
  {
    String href = super.saveContent(ctx);
    String title = ctx.getRequest().getParameter("title");
    
    GWikiFragmentLink link = new GWikiFragmentLink(href);
    
    if (StringUtils.isNotEmpty(title)) 
    {
      link.setTitle(title);
    }

    updateSection(link.toString());
  }

}
