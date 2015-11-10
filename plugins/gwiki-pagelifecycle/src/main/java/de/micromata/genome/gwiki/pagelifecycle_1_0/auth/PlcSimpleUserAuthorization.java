////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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
package de.micromata.genome.gwiki.pagelifecycle_1_0.auth;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.auth.GWikiAuthorizationExtWrapper;
import de.micromata.genome.gwiki.model.GWikiAuthorizationExt;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.BranchFileStats;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.FileStatsDO;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.GWikiPlcRights;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcUtils;
import de.micromata.genome.util.runtime.CallableX;

/**
 * @author Christian Claus (c.claus@micromata.de)
 * 
 */
public class PlcSimpleUserAuthorization extends GWikiAuthorizationExtWrapper
{

  /**
   * @param previousAuthorization
   */
  public PlcSimpleUserAuthorization(GWikiAuthorizationExt previousAuthorization)
  {
    super(previousAuthorization);
  }

  /**
   * @param ctx
   * @param ei
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#isAllowToEdit(de.micromata.genome.gwiki.page.GWikiContext,
   *      de.micromata.genome.gwiki.model.GWikiElementInfo)
   */
  public boolean isAllowToEdit(final GWikiContext ctx, final GWikiElementInfo ei)
  {
    if (isAllowTo(ctx, GWikiAuthorizationRights.GWIKI_ADMIN.name()) == true) {
      return true;
    }

    if (super.isAllowTo(ctx, GWikiPlcRights.PLC_EDIT_ARTICLE.name()) && ei != null) {

      boolean hasFileStatItem = ctx.runInTenantContext(ctx.getWikiWeb().getTenantId(), getWikiSelector(ctx),
          new CallableX<Boolean, RuntimeException>() {
            public Boolean call() throws RuntimeException
            {
              BranchFileStats stats = PlcUtils.getBranchFileStats(ctx);

              if (stats != null) {
                FileStatsDO statsForId = stats.getFileStatsForId(ei.getId());

                if (statsForId == null) {
                  return false;
                } else {
                  return true;
                }
              }
              return false;
            }
          });

      // Element has a branchFileStatEntry. Use the own authorization method.
      if (hasFileStatItem) {
        return ctx.runInTenantContext(ctx.getWikiWeb().getTenantId(), getWikiSelector(ctx), new CallableX<Boolean, RuntimeException>() {
          public Boolean call() throws RuntimeException
          {
            BranchFileStats stats = PlcUtils.getBranchFileStats(ctx);
            FileStatsDO statsForId = stats.getFileStatsForId(ei.getId());

            if (StringUtils.equals(statsForId.getAssignedTo(), ctx.getWikiWeb().getAuthorization().getCurrentUserName(ctx))) {
              return true;
            }
            return false;
          }
        });
      }
    }

    return super.isAllowToEdit(ctx, ei);
  }

  private GWikiMultipleWikiSelector getWikiSelector(final GWikiContext ctx)
  {
    GWikiWikiSelector wikiSelector = ctx.getWikiWeb().getDaoContext().getWikiSelector();
    if (wikiSelector == null) {
      ctx.addValidationError("gwiki.error.tenantsNotSupported");
      return null;
    }

    if (wikiSelector instanceof GWikiMultipleWikiSelector == true) {
      GWikiMultipleWikiSelector multipleSelector = (GWikiMultipleWikiSelector) wikiSelector;
      return multipleSelector;
    }
    return null;
  }
}
