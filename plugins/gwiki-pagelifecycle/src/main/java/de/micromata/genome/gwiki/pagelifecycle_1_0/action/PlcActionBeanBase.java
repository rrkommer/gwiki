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

package de.micromata.genome.gwiki.pagelifecycle_1_0.action;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * Abstract actionbean for actions in a multiple tenant context. 
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class PlcActionBeanBase extends ActionBeanBase
{

  private String backUrl;
  
  protected GWikiMultipleWikiSelector getWikiSelector()
  {
    GWikiWikiSelector wikiSelector = wikiContext.getWikiWeb().getDaoContext().getWikiSelector();
    if (wikiSelector == null) {
      wikiContext.addValidationError("gwiki.error.tenantsNotSupported");
      return null;
    }

    if (wikiSelector instanceof GWikiMultipleWikiSelector == true) {
      GWikiMultipleWikiSelector multipleSelector = (GWikiMultipleWikiSelector) wikiSelector;
      return multipleSelector;
    }
    return null;
  }
  
  protected Object goBack()
  {
    if (StringUtils.isNotBlank(backUrl)) {
      return backUrl;
    }
    return wikiContext.getWikiWeb().getHomeElement(wikiContext);
  }
  
  /**
   * Closes the fancy box
   * 
   * @param reloadParent if <code>true</code> the page where the fancybox were opened will be reloaded after closing (e.g. for loading
   *          changes made in the fancybox)
   */
  protected Object closeFancyBox(final boolean reloadParent)
  {
    StringBuffer sb = new StringBuffer("<script type='text/javascript'>");
    sb.append("parent.$.fancybox.close();");
    if (reloadParent == true) {
      sb.append("window.parent.location.reload();");
    }
    sb.append("</script>");
    wikiContext.append(sb.toString());
    wikiContext.flush();
    return noForward();
  }

  /**
   * @param backUrl the backUrl to set
   */
  public void setBackUrl(String backUrl)
  {
    this.backUrl = backUrl;
  }
}