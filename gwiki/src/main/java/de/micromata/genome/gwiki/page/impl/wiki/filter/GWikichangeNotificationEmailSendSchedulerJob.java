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

package de.micromata.genome.gwiki.page.impl.wiki.filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiEmailProvider;
import de.micromata.genome.gwiki.model.GWikiSchedulerJobBase;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.util.runtime.CallableX;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikichangeNotificationEmailSendSchedulerJob extends GWikiSchedulerJobBase
{

  private static final long serialVersionUID = 8545136772683895346L;

  @Override
  public void call()
  {
    String id = getPageId();
    GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(id);
    if (ei == null) {
      GWikiLog.warn("ChangeNotfication, pageId not found; " + id);
      return;
    }
    Map<String, String> props = GWikiChangeNotificationActionBean.getNotificationEmails(wikiContext);
    Set<String> userNames = new HashSet<String>();
    GWikiChangeNotificationFilter.findUser(wikiContext, props, ei, false, userNames);
    if (userNames.isEmpty() == true) {
      return;
    }

    final Map<String, String> ctx = new HashMap<String, String>();
    String url = wikiContext.globalUrl(id);
    String cuser = args.get("user");
    if (StringUtils.isBlank(cuser) == true) {
      cuser = "Unknown";
    }
    String changeUser = cuser;
    ctx.put(GWikiEmailProvider.FROM, wikiContext.getWikiWeb().getWikiConfig().getSendEmail());

    for (String userName : userNames) {
      try {
        wikiContext.getWikiWeb().getAuthorization().runAsUser(userName, wikiContext,
            new CallableX<Void, RuntimeException>()
            {

              @Override
              public Void call() throws RuntimeException
              {
                String title = wikiContext.getTranslatedProp(ei.getTitle());

                ctx.put(GWikiEmailProvider.SUBJECT,
                    wikiContext.translate("gwiki.page.edit.ChangeNotification.email.subject", title, url, changeUser));
                ctx.put(GWikiEmailProvider.TEXT,
                    wikiContext.translate("gwiki.page.edit.ChangeNotification.email.body", title, url, changeUser));

                String email = wikiContext.getWikiWeb().getAuthorization().getCurrentUserEmail(wikiContext);
                if (StringUtils.isEmpty(email) == true) {
                  return null;
                }
                ctx.put(GWikiEmailProvider.TO, email);
                wikiContext.getWikiWeb().getDaoContext().getEmailProvider().sendEmail(ctx);
                return null;
              }
            });
      } catch (AuthorizationFailedException ex) {
        GWikiLog.warn("Cannot determine email for user: " + userName, ex);
      }
    }

  }

}
