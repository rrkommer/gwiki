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

package de.micromata.genome.gwiki.page.impl.wiki.blueprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByPropComparator;
import de.micromata.genome.gwiki.utils.WebUtils;
import de.micromata.genome.util.matcher.MatcherBase;
import de.micromata.genome.util.types.Pair;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiBlueprintEditorActionBean extends ActionBeanBase
{
  private String pageId;

  private String templateId;

  private List<Pair<String, String>> templates = new ArrayList<Pair<String, String>>();

  private GWikiWikiPageArtefakt wikiTemplate;

  private String evaluatedWiki;

  protected void buildTemplateList()
  {
    List<GWikiElementInfo> elems = wikiContext.getElementFinder().getPageInfos(new MatcherBase<GWikiElementInfo>()
    {

      private static final long serialVersionUID = 6581136045310016723L;

      @Override
      public boolean match(GWikiElementInfo ei)
      {
        if (ei.getId().startsWith("admin/blueprints/") == false) {
          return false;
        }
        if (StringUtils
            .equals(ei.getProps().getStringValue(GWikiPropKeys.WIKIMETATEMPLATE),
                "admin/templates/StandardWikiPageMetaTemplate") == false) {
          return false;
        }
        return true;
      }
    });
    Collections.sort(elems, new GWikiElementByPropComparator(GWikiPropKeys.TITLE));
    for (GWikiElementInfo ei : elems) {
      templates.add(Pair.make(ei.getTitle(), ei.getId()));
    }
  }

  protected void renderTemplate()
  {
    GWikiElement el = wikiContext.getWikiWeb().findElement(templateId);
    if (el == null) {
      wikiContext.addSimpleValidationError("Cannot find template: " + templateId);
      return;
    }
    GWikiArtefakt<?> art = el.getPart("MainPage");
    if (art == null) {
      wikiContext.addSimpleValidationError("Cannot find part 'MainPage' in template: " + templateId);
      return;
    }
    if ((art instanceof GWikiWikiPageArtefakt) == false) {
      wikiContext.addSimpleValidationError("Part 'MainPage' is not a wiki page; template: " + templateId);
      return;
    }
    wikiTemplate = (GWikiWikiPageArtefakt) art;
    wikiContext.setRequestAttribute("MainPage", wikiTemplate);
  }

  @Override
  public Object onInit()
  {
    if (StringUtils.isBlank(templateId) == true) {
      buildTemplateList();
    } else {
      renderTemplate();
    }
    return null;
  }

  public Object onEvaluate()
  {
    if (StringUtils.isBlank(templateId) == true) {
      buildTemplateList();
      return null;
    } else {
      renderTemplate();
    }
    if (wikiTemplate == null) {
      return null;
    }
    // TODO validate form.
    wikiContext.setRequestAttribute(GWikiFormInputMacro.EVAL_FORM, Boolean.TRUE);
    evaluatedWiki = StringUtils.trim(wikiTemplate.getCompiledObject().getSource());
    return null;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getTemplateId()
  {
    return templateId;
  }

  public void setTemplateId(String templateId)
  {
    this.templateId = templateId;
  }

  public List<Pair<String, String>> getTemplates()
  {
    return templates;
  }

  public void setTemplates(List<Pair<String, String>> templates)
  {
    this.templates = templates;
  }

  public String getEvaluatedWiki()
  {
    return evaluatedWiki;
  }

  public void setEvaluatedWiki(String evaluatedWiki)
  {
    this.evaluatedWiki = evaluatedWiki;
  }

  public String getEscapedEvaluatedWiki()
  {
    return WebUtils.escapeJavaScript(evaluatedWiki);
  }
}
