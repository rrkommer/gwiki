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
package de.micromata.genome.gwiki.pagelifecycle_1_0;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * @author stefans
 *
 */
public class CreateBranchActionBean extends ActionBeanBase
{
  /**
   * 
   */
  private static final String TEMPLATE_ID = "admin/templates/GWikiBranchInfoElementTemplate";

  private String branchId;

  private String description;

  /**
   * @param branchId the branchId to set
   */
  public void setBranchId(String branchId)
  {
    this.branchId = branchId;
  }

  /**
   * @return the branchId
   */
  public String getBranchId()
  {
    return branchId;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * @return the description
   */
  public String getDescription()
  {
    return description;
  } 
  
  public Object onCreateBranch() {
    if (isBranchIdValid(branchId) == false) {
      return null;
    }
    
    GWikiWikiSelector wikiSelector = wikiContext.getWikiWeb().getDaoContext().getWikiSelector();
    if (wikiSelector instanceof GWikiMultipleWikiSelector == false) {
      wikiContext.addSimpleValidationError("No multiple branches supported.");
      return null;
    }
    wikiSelector.enterTenant(wikiContext, this.branchId);
    
    createBranchInfoElement();
    
    
    return null;
  }

  /**
   * 
   */
  private void createBranchInfoElement()
  {
    GWikiElement el = GWikiWebUtils.createNewElement(wikiContext, "admin/branch/BranchInfoElement", TEMPLATE_ID, "BranchInfo");
    GWikiArtefakt< ? > artefakt = el.getMainPart();

    GWikiPropsArtefakt art = (GWikiPropsArtefakt) artefakt;
    GWikiProps props = art.getCompiledObject();
    props.setStringValue("BRANCH_ID", this.branchId);
    props.setStringValue("DESCRIPTION", this.description);
    
    wikiContext.getWikiWeb().saveElement(wikiContext, el, false);
    
    
    GWikiElement element = wikiContext.getWikiWeb().findElement("admin/branch/BranchInfoElement");

    GWikiProps configProps = wikiContext.getElementFinder().getConfigProps("admin/branch/BranchInfoElement");
    
  }

  /**
   * @return
   */
  private boolean isBranchIdValid(String id)
  {
    if (StringUtils.isBlank(id) == true) {
      wikiContext.addSimpleValidationError("branch id not valid. It must have at least one character");
      return false;
    }
    if (StringUtils.containsAny(id, new char[]{'/',',','*','#','"','\''})) {
      wikiContext.addSimpleValidationError("branch id not valid. It contains invalid chracters.");
      return false;
    }
    if (id.length() > 200) {
      wikiContext.addSimpleValidationError("branch id not valid. There are too many characters.");
      return false;
    }
    return true;
  }
  
  

}
