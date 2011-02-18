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

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiContent;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiCollectFragmentTypeVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentImage;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParser;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;

/**
 * @author Christian Claus (c.claus@micromata.de)
 */
public class PtWikiImageEditor extends PtWikiUploadEditor
{

  private static final long serialVersionUID = 5901053792188232570L;

  public PtWikiImageEditor(GWikiElement element, String sectionName, String editor)
  {
    super(element, sectionName, editor);
    
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiArtefaktBase#renderWithParts(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean renderWithParts(GWikiContext ctx)  
  {
    String content = StringEscapeUtils.escapeHtml(getEditContent());
    GWikiWikiParser wkparse = new GWikiWikiParser();
    GWikiContent gwikiContent = wkparse.parse(ctx, content);
    
    GWikiCollectFragmentTypeVisitor images = new GWikiCollectFragmentTypeVisitor(GWikiFragmentImage.class);
    gwikiContent.iterate(images);
    
    GWikiFragmentImage img = null;
    
    if (!images.getFound().isEmpty())
    {
      img = (GWikiFragmentImage) images.getFound().get(0);
      img.setStyle("margin-top:20px");
      img.render(ctx);
    }
    
    final String discover = ctx.getTranslated("gwiki.editor.image.discover");
    
    XmlElement input = input( //
        attrs("name", sectionName, "type", "file", "size", "50", "accept", "image/*"));
        
    XmlElement table = table( //
        attrs("style", "margin:20px 0")).nest( //
            tr( //
                td(text(discover)), //
                td(input) //
            ));
    
    ctx.append(table.toString());
    return true;
  }
  
  public void onSave(GWikiContext ctx) {
    String content = super.saveContent(ctx);
    
    GWikiFragmentImage image = new GWikiFragmentImage(content);
    String newContent = image.toString();
    
    updateSection(newContent);
  }
  

}
