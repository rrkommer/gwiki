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

package de.micromata.genome.gwiki.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.logging.loghtmlwindow.LogHtmlWindowServlet;

public class GWikiLogHtmlWindowServlet extends LogHtmlWindowServlet
{

  @Override
  protected void executeWithAuthentifcation(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException
  {
    if (GWikiServlet.INSTANCE == null) {
      return;
    }
    if (GWikiServlet.INSTANCE.hasWikiWeb() == false) {
      return;
    }
    GWikiWeb wiki = GWikiServlet.INSTANCE.getWikiWeb();
    GWikiContext ctx = new GWikiContext(wiki, this, req, resp);
    GWikiAuthorization auth = wiki.getDaoContext().getAuthorization();
    try {
      auth.initThread(ctx);
      boolean allowed = auth.isAllowTo(ctx, "GWIKI_ADMIN");
      if (allowed == false) {
        return;
      }
      try {
        GWikiContext.setCurrent(ctx);
        execute(req, resp);
      } finally {
        GWikiContext.setCurrent(null);
      }
    } finally {
      auth.clearThread(ctx);
    }
    return;
  }

}
