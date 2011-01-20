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
package de.micromata.genome.gwiki.pagelifecycle_1_0.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.util.matcher.MatcherBase;
import de.micromata.genome.util.runtime.CallableX;

/**
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class ViewBranchContentActionBean extends ActionBeanBase {

	private Map<String, List<GWikiElementInfo>> contentMap = new HashMap<String, List<GWikiElementInfo>>();
	
	@Override
	public Object onInit() {
		final GWikiMultipleWikiSelector wikiSelector = getWikiSelector();
		if (wikiSelector == null) {
			wikiContext.addSimpleValidationError("No multiple branches supported");
			return null;
		}
		
		List<String> tenants = wikiSelector.getMptIdSelector().getTenants(GWikiWeb.getRootWiki());
		if (tenants == null || tenants.size() == 0) {
			return null;
		}
		
		for (final String tenant : tenants) {
			runInTenantContext(tenant, wikiSelector, new CallableX<Void, RuntimeException>() {
				public Void call() throws RuntimeException {
					List<GWikiElementInfo> tenantContent = wikiContext.getElementFinder().getPageInfos(new MatcherBase<GWikiElementInfo>() {
						private static final long serialVersionUID = -6020166500681070082L;

						public boolean match(GWikiElementInfo ei) {
							String tid = ei.getProps().getStringValue(GWikiPropKeys.TENANT_ID);
							return StringUtils.equals(tenant, tid);
						}
					});
					
					getContentMap().put(tenant, tenantContent);
					return null;
				}
			}); 
		}
		
		return null;

	}

	private Void runInTenantContext(String tenantId, GWikiMultipleWikiSelector wikiSelector, CallableX<Void, RuntimeException> callBack) {
		try {
			wikiSelector.enterTenant(wikiContext, tenantId);
			return callBack.call();
		} finally {
			wikiSelector.leaveTenant(wikiContext);
		}
	}

	private GWikiMultipleWikiSelector getWikiSelector() {
		GWikiWikiSelector wikiSelector = wikiContext.getWikiWeb().getDaoContext().getWikiSelector();
		if (wikiSelector instanceof GWikiMultipleWikiSelector == true) {
			GWikiMultipleWikiSelector multipleSelector = (GWikiMultipleWikiSelector) wikiSelector;
			return multipleSelector;
		}
		return null;
	}

	/**
	 * @param contentMap the contentMap to set
	 */
	public void setContentMap(Map<String, List<GWikiElementInfo>> contentMap) {
		this.contentMap = contentMap;
	}

	/**
	 * @return the contentMap
	 */
	public Map<String, List<GWikiElementInfo>> getContentMap() {
		return contentMap;
	}

}
