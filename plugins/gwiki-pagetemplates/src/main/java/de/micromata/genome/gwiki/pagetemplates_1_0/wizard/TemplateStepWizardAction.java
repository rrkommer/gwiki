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

package de.micromata.genome.gwiki.pagetemplates_1_0.wizard;

import static de.micromata.genome.util.xml.xmlbuilder.Xml.attrs;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.code;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.text;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.a;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.div;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.input;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.table;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.td;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.th;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.tr;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiAttachment;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.matcher.GWikiPageIdMatcher;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentImage;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;

/**
 * Step which applies the selected template to the recent saved page/category
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class TemplateStepWizardAction extends AbstractStepWizardAction
{
  /*
   * Template
   */
  private String tplSelectedPageTemplate;
  
  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase#onInit()
   */
  @Override
  public Object onInit()
  {
    return null;
  }
  
  public Object onSave()
  {
    if (getElement() == null) {
      return null;
    }

    // load page template and extract content
    final GWikiElement tpl = wikiContext.getWikiWeb().findElement(tplSelectedPageTemplate);
    GWikiArtefakt< ? > tplArtefakt = tpl.getPart("MainPage");
    if (tplArtefakt instanceof GWikiWikiPageArtefakt == false) {
      return noForward();
    }
    GWikiWikiPageArtefakt tplPageArtefakt = (GWikiWikiPageArtefakt) tplArtefakt;
    final String tplContent = tplPageArtefakt.getStorageData();
    if (StringUtils.isBlank(tplContent) == true) {
      return noForward();
    }

    GWikiArtefakt< ? > artefakt = getElement().getPart("MainPage");
    if (artefakt instanceof GWikiWikiPageArtefakt == false) {
      return null;
    }
    GWikiWikiPageArtefakt wikiPageArtefakt = (GWikiWikiPageArtefakt) artefakt;
    wikiPageArtefakt.setStorageData(tplContent);

    return noForward();
  }

  public Object onValidate()
  {
    if (StringUtils.isBlank(this.tplSelectedPageTemplate)) {
      wikiContext.addValidationError("gwiki.page.articleWizard.error.noTemplate");
    }
    return null;
  }
  
  public boolean onIsVisible() {
    return true;
  }
  
  /**
   * renders a table with available page templates
   */
  public void renderTemplates() {
    // find page templates
    final Matcher<String> m = new BooleanListRulesFactory<String>().createMatcher("*tpl/*Template*");
    final List<GWikiElementInfo> tpl = wikiContext.getElementFinder().getPageInfos(new GWikiPageIdMatcher(wikiContext, m));

    // table header
    XmlElement table = table(attrs("border", "1", "cellspacing", "0", "cellpadding", "2", "width", "100%"));
    table.nest(//
        tr(//
        th(text(translate("gwiki.page.articleWizard.template.select"))), //
        th(text(translate("gwiki.page.articleWizard.template.name"))), //
            th(text(translate("gwiki.page.articleWizard.template.desc"))), //
            th(text(translate("gwiki.page.articleWizard.template.preview"))) //
        ));


    StringBuilder script = new StringBuilder("<script type=\"text/javascript\">\n" + "$(document).ready(function(){\n");
    
    // render each template in row
    int row = 1;
    for (final GWikiElementInfo ei : tpl) {
      final GWikiElement el = wikiContext.getWikiWeb().findElement(ei.getId());
      if (el == null || ei.isViewable() == false) {
        continue;
      }

      final String part = "MainPage";
      GWikiArtefakt< ? > art = null;
      if (StringUtils.isNotEmpty(part) == true) {
        art = el.getPart(part);
      } else {
        art = el.getMainPart();
      }
      if ((art instanceof GWikiExecutableArtefakt< ? >) == false || art instanceof GWikiAttachment) {
        continue;
      }
      final String desc = wikiContext.getTranslatedProp(ei.getProps().getStringValue("DESCRIPTION"));
      final String name = wikiContext.getTranslatedProp(ei.getProps().getStringValue("TITLE"));
      final GWikiExecutableArtefakt< ? > exec = (GWikiExecutableArtefakt< ? >) art;
      String renderedPreview = wikiContext.runElement(el, new CallableX<String, RuntimeException>() {
        public String call() throws RuntimeException
        {
          return renderPreview(exec);
        }
      });

      
      // render template row
      table.nest( //
          tr(//
              td(input("type", "radio", "name", "tplSelectedPageTemplate", "value", ei.getId())), //
              td(text(name)), //
              td(text(desc)), //
              td(a(new String[][]{{"href", "javascript:openPreviewDialog('tpl" + row + "')"}}, //
                  text(wikiContext.getTranslated("gwiki.page.articleWizard.template.preview"))), //
                  div(new String[][]{{"id", "tpl" + row}}, code(renderedPreview))
                )
             )
       );
      script.append("$(\"#tpl").append(row).append("\").dialog({ autoOpen: false,  height: 800,  width: 800 , modal: true});");
      row++;
    }
    script.append("});\n" + "</script>");

    // write table and JavaScript
    wikiContext.append(table.toString());
    wikiContext.append(script.toString());
  }
  
  /**
   * Generates HTML Output of given executable artefakt
   * 
   * @param exec artefakt
   * @return rendered output
   */
  private String renderPreview(final GWikiExecutableArtefakt< ? > exec)
  {
    final GWikiStandaloneContext standaloneContext = GWikiStandaloneContext.create();
    standaloneContext.setRequestAttribute(GWikiFragmentImage.WIKI_MAX_IMAGE_WIDTH, "300px");
    standaloneContext.setRenderMode(RenderModes.LocalImageLinks.getFlag());
    standaloneContext.setWikiElement(wikiContext.getCurrentElement());
    standaloneContext.setCurrentPart(exec);
    exec.render(standaloneContext);
    standaloneContext.flush();
    String prev = standaloneContext.getOutString();
    return prev;
  }

  /**
   * @param selectedPageTemplate the selectedPageTemplate to set
   */
  public void setTplSelectedPageTemplate(String selectedPageTemplate)
  {
    this.tplSelectedPageTemplate = selectedPageTemplate;
  }

  /**
   * @return the selectedPageTemplate
   */
  public String getTplSelectedPageTemplate()
  {
    return tplSelectedPageTemplate;
  }

}
