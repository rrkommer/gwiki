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
package de.micromata.genome.gwiki.pagelifecycle_1_0.wizard;

import static de.micromata.genome.util.xml.xmlbuilder.Xml.attrs;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.text;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.a;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.input;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.table;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.td;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.th;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.tr;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiAttachment;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.model.matcher.GWikiPageIdMatcher;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentImage;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;

/**
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class TimingStepWizardAction extends ActionBeanBase
{
  
private List<String> availableReviewers;
  
  private String selectedReviewer;

  private boolean immediately = true;
  
  private Date from;

  private String fromDate;

  private int fromHour;

  private int fromMin;

  private int fromSec;

  private Date to;

  private String toDate;

  private int toHour;

  private int toMin;

  private int toSec;
  
  /**
   * renders a table with available page templates
   */
  public void renderTemplates()
  {
    // find page templates
    final Matcher<String> m = new BooleanListRulesFactory<String>().createMatcher("*tpl/*Template*");
    final List<GWikiElementInfo> ret = wikiContext.getElementFinder().getPageInfos(new GWikiPageIdMatcher(wikiContext, m));

    // table header
    XmlElement table = table(attrs("border", "1", "cellspacing", "0", "cellpadding", "2", "width", "100%"));
    table.nest(//
        tr(//
        th(text(translate("gwiki.page.articleWizard.template.select"))), //
        th(text(translate("gwiki.page.articleWizard.template.name"))), //
            th(text(translate("gwiki.page.articleWizard.template.desc"))), //
            th(text(translate("gwiki.page.articleWizard.template.preview"))) //
        ));

    // render each template in row
    for (final GWikiElementInfo ei : ret) {
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
      final String name = wikiContext.getTranslatedProp(ei.getProps().getStringValue("NAME"));
      final GWikiExecutableArtefakt< ? > exec = (GWikiExecutableArtefakt< ? >) art;
      final String renderedPreview = renderPreview(exec);

      StringBuffer sb = new StringBuffer();
      sb.append("displayHilfeLayer('<div style=\"font-size:0.6em;size:0.6em;\">");
      sb.append(StringEscapeUtils.escapeJavaScript(renderedPreview));
      sb.append("</div>");
      sb.append("', '").append(wikiContext.genHtmlId(""));
      sb.append("')");

      // render template row
      table.nest( //
          tr(//
          td(input("type", "radio", "name", "selectedPageTemplate", "value", ei.getId())), //
              td(text(name)), //
              td(text(desc)), //
              td(new String[][] { { "valign", "top"}}, //
                  a(new String[][] { //
                  { "class", "wikiSmartTag"}, { "href", "#"}, { "onclick", "return false;"}, { "onmouseout", "doNotOpenHilfeLayer();"},
                      { "onmouseover", sb.toString()}//
                  }, text(translate("gwiki.page.articleWizard.template.preview"))))));
    }

    // write table
    wikiContext.append(table.toString());
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
    standaloneContext.setRequestAttribute(GWikiFragmentImage.WIKI_MAX_IMAGE_WIDTH, "100px");
    standaloneContext.setRenderMode(RenderModes.LocalImageLinks.getFlag());
    standaloneContext.setWikiElement(wikiContext.getCurrentElement());
    standaloneContext.setCurrentPart(exec);
    exec.render(standaloneContext);
    standaloneContext.flush();
    return standaloneContext.getOutString();
  }

  public Object onSave()
  {
    return null;
  }

  public Object onValidate()
  {
    return null;
  }

  /**
   * @return the availableReviewers
   */
  public List<String> getAvailableReviewers()
  {
    return availableReviewers;
  }

  /**
   * @param availableReviewers the availableReviewers to set
   */
  public void setAvailableReviewers(List<String> availableReviewers)
  {
    this.availableReviewers = availableReviewers;
  }

  /**
   * @return the selectedReviewer
   */
  public String getSelectedReviewer()
  {
    return selectedReviewer;
  }

  /**
   * @param selectedReviewer the selectedReviewer to set
   */
  public void setSelectedReviewer(String selectedReviewer)
  {
    this.selectedReviewer = selectedReviewer;
  }

  /**
   * @return the immediately
   */
  public boolean isImmediately()
  {
    return immediately;
  }

  /**
   * @param immediately the immediately to set
   */
  public void setImmediately(boolean immediately)
  {
    this.immediately = immediately;
  }

  /**
   * @return the from
   */
  public Date getFrom()
  {
    return from;
  }

  /**
   * @param from the from to set
   */
  public void setFrom(Date from)
  {
    this.from = from;
  }

  /**
   * @return the fromDate
   */
  public String getFromDate()
  {
    return fromDate;
  }

  /**
   * @param fromDate the fromDate to set
   */
  public void setFromDate(String fromDate)
  {
    this.fromDate = fromDate;
  }

  /**
   * @return the fromHour
   */
  public int getFromHour()
  {
    return fromHour;
  }

  /**
   * @param fromHour the fromHour to set
   */
  public void setFromHour(int fromHour)
  {
    this.fromHour = fromHour;
  }

  /**
   * @return the fromMin
   */
  public int getFromMin()
  {
    return fromMin;
  }

  /**
   * @param fromMin the fromMin to set
   */
  public void setFromMin(int fromMin)
  {
    this.fromMin = fromMin;
  }

  /**
   * @return the fromSec
   */
  public int getFromSec()
  {
    return fromSec;
  }

  /**
   * @param fromSec the fromSec to set
   */
  public void setFromSec(int fromSec)
  {
    this.fromSec = fromSec;
  }

  /**
   * @return the to
   */
  public Date getTo()
  {
    return to;
  }

  /**
   * @param to the to to set
   */
  public void setTo(Date to)
  {
    this.to = to;
  }

  /**
   * @return the toDate
   */
  public String getToDate()
  {
    return toDate;
  }

  /**
   * @param toDate the toDate to set
   */
  public void setToDate(String toDate)
  {
    this.toDate = toDate;
  }

  /**
   * @return the toHour
   */
  public int getToHour()
  {
    return toHour;
  }

  /**
   * @param toHour the toHour to set
   */
  public void setToHour(int toHour)
  {
    this.toHour = toHour;
  }

  /**
   * @return the toMin
   */
  public int getToMin()
  {
    return toMin;
  }

  /**
   * @param toMin the toMin to set
   */
  public void setToMin(int toMin)
  {
    this.toMin = toMin;
  }

  /**
   * @return the toSec
   */
  public int getToSec()
  {
    return toSec;
  }

  /**
   * @param toSec the toSec to set
   */
  public void setToSec(int toSec)
  {
    this.toSec = toSec;
  }

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
}
