package de.micromata.genome.gwiki.pagetemplates_1_0.wizard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.page.impl.GWikiActionBeanArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBean;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanUtils;
import de.micromata.genome.gwiki.utils.ClassUtils;

/**
 * Wizard for creating articles
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class PageWizardAction extends ActionBeanBase
{
  // TODO: stefan dynamisch hinterlegen und durch Plugin auch erweiterbar machen
  private List<String> wizardSteps = Arrays.asList("/edit/pagetemplates/wizard/CategoryStep",
      "/edit/pagetemplates/wizard/TemplateStep", "/edit/pagelifecycle/wizard/TimingStep");

  public void renderHeaders()
  {
    for (String stepPageId : this.wizardSteps) {
      GWikiElement stepElement = wikiContext.getWikiWeb().findElement(stepPageId);
      GWikiElementInfo info = stepElement.getElementInfo();
      String tabTitle = info.getProps().getStringValue(GWikiPropKeys.TITLE);

      if (tabTitle.startsWith("I{") == true) {
        tabTitle = wikiContext.getTranslatedProp(tabTitle);
      }
      String divAnchor = StringUtils.substringAfterLast(stepPageId, "/");
      wikiContext.append("<li><a href='#").append(divAnchor).append("'>").append(tabTitle).append("</a></li>");
    }
  }

  public Object onSave()
  {
    performValidation();
    if (wikiContext.hasValidationErrors() == true) {
      return null;
    }
    
    // if validation ok create new element
    final GWikiElement newPage = GWikiWebUtils.createNewElement(wikiContext, "", "admin/templates/StandardWikiPageMetaTemplate","");

    // call step save handlers
    for (String wizardStep : this.wizardSteps) {
      runInActionContext(wizardStep, newPage, new Callable<RuntimeException, Void>() {
        public Void call(ActionBean bean) throws RuntimeException
        {
          ActionBeanUtils.dispatchToMethodImpl(bean, "onSave", wikiContext);
          return null;
        }
      });
    }
    
    // saves element
    wikiContext.getWikiWeb().saveElement(wikiContext, newPage, false);
    
    // save page-id in request-attribute for possible later usage
    return newPage;
  }

  /**
   * calls validate methods of each wizard step
   */
  private void performValidation()
  {
    for (String wizardStep : wizardSteps) {
      runInActionContext(wizardStep, null, new Callable<RuntimeException, Void>() {
        public Void call(ActionBean bean) throws RuntimeException
        {
          ActionBeanUtils.dispatchToMethodImpl(bean, "onValidate", wikiContext);
          return null;
        }
      });
    }
  }

  public Void runInActionContext(String actionPageId, GWikiElement element, Callable<RuntimeException, Void> callback)
  {
    GWikiElement page = wikiContext.getWikiWeb().getElement(actionPageId);
    GWikiArtefakt< ? > controller = page.getPart("Controler");
    if (controller instanceof GWikiActionBeanArtefakt == false) {
      return null;
    }
    GWikiActionBeanArtefakt actionBeanArtefakt = (GWikiActionBeanArtefakt) controller;
    ActionBean bean = actionBeanArtefakt.getActionBean(wikiContext);
    bean.setWikiContext(wikiContext);

    // populate element to step action beans
    Map<String, Object> elementParam = new HashMap<String, Object>();
    elementParam.put("element", element);
    ClassUtils.populateBeanWithPuplicMembers(bean, elementParam);
    
    // populate form properies to step action beans
    ActionBeanUtils.fillForm((ActionBean) bean, wikiContext);
    return callback.call(bean);
  }

  /**
   * @param wizardSteps the wizardSteps to set
   */
  public void setWizardSteps(List<String> wizardSteps)
  {
    this.wizardSteps = wizardSteps;
  }

  /**
   * @return the wizardSteps
   */
  public List<String> getWizardSteps()
  {
    return wizardSteps;
  }

  interface Callable<E extends RuntimeException, R>
  {
    R call(ActionBean bean) throws E;
  }
}