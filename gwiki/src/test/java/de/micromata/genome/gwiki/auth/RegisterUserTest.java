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

package de.micromata.genome.gwiki.auth;

import junit.framework.TestCase;
import de.micromata.genome.gwiki.GWikiTestBuilder;
import de.micromata.genome.gwiki.controls.GWikiLoginActionBean;
import de.micromata.genome.gwiki.model.GWikiAuthorizationExt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class RegisterUserTest extends TestCase
{
  String user = "unittusr1";

  protected void deleteUser(String user, GWikiTestBuilder tb)
  {
    GWikiContext wikiContext = tb.getLastWikiContext();
    GWikiWeb wikiWeb = tb.getLastWikiContext().getWikiWeb();
    GWikiElement el = wikiWeb.findElement("admin/user/" + user);
    if (el == null) {
      return;
    }
    wikiWeb.getStorage().deleteElement(wikiContext, el);
  }

  public void testCreateUser()
  {

    GWikiTestBuilder tb = new GWikiTestBuilder();
    tb.createWikiContext();
    if ((tb.getDaoContext().getAuthorization() instanceof GWikiAuthorizationExt) == false) {
      return;
    }
    deleteUser(user, tb);
    GWikiAuthorizationExt ext = (GWikiAuthorizationExt) tb.getDaoContext().getAuthorization();
    GWikiContext wikiContext = tb.getLastWikiContext();
    GWikiProps userProps = new GWikiProps();
    String pass = GWikiLoginActionBean.genPassword();
    String cp = GWikiSimpleUserAuthorization.encrypt(pass);
    userProps.setStringValue(GWikiAuthorizationExt.USER_PROP_EMAIL, "r.kommer@micromata.de");
    userProps.setStringValue(GWikiAuthorizationExt.USER_PROP_RIGHTSRULE, "GWIKI_VIEWPAGES");
    userProps.setStringValue(GWikiAuthorizationExt.USER_PROP_PASSWORD, cp);

    boolean created = ext.createUser(wikiContext, user, userProps);
    if (created == false) {
      assertFalse("Cannot create user", false);
    }
    GWikiElement userEl = wikiContext.getWikiWeb().findElement("admin/user/" + user);
    assertNotNull(userEl);
    assertEquals(user, userEl.getElementInfo().getCreatedBy());
    deleteUser(user, tb);
  }
}
