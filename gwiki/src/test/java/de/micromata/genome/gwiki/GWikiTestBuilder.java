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

package de.micromata.genome.gwiki;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.config.GWikiCpContextBootstrapConfigLoader;
import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.gspt.GenomeTemplateUtils;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.gwiki.web.StandaloneHttpServletRequest;
import de.micromata.genome.gwiki.web.StandaloneHttpServletResponse;
import de.micromata.genome.test.web.SimHttpSession;
import de.micromata.genome.test.web.SimServletConfig;
import de.micromata.genome.test.web.SimServletContext;
import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.genome.util.runtime.LocalSettingsEnv;

/**
 * Testbuilder for tests.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiTestBuilder
{
  protected GWikiDAOContext daoContext = new GWikiDAOContext();

  protected StandaloneHttpServletResponse lastResponse;

  protected StandaloneHttpServletRequest lastRequest;

  protected GWikiContext lastWikiContext;

  protected GWikiServlet servlet;

  protected SimHttpSession session = new SimHttpSession();

  public GWikiTestBuilder()
  {
    initGWiki(daoContext);
  }

  public GWikiTestBuilder(GWikiDAOContext daoContext)
  {
    initGWiki(daoContext);
  }

  protected GWikiTestBuilder initGWiki(GWikiDAOContext daoContext)
  {
    this.daoContext = daoContext;
    if (GWikiServlet.INSTANCE != null && GWikiServlet.INSTANCE.getDAOContext() != null
        && GWikiServlet.INSTANCE.getWikiWeb() != null) {
      this.servlet = GWikiServlet.INSTANCE;
      return this;
    }
    LocalSettings.get();
    LocalSettingsEnv.get();
    GenomeTemplateUtils.IN_UNITEST = true;
    SimServletConfig msc = new SimServletConfig();
    msc.setServletName("gwiki");
    SimServletContext sc = new SimServletContext("gwiki", "src/test/external_resources/webapp");
    Map<String, String> initParams = new HashMap<String, String>();
    initParams.put("de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader.className", //
        GWikiCpContextBootstrapConfigLoader.class.getName());
    initParams.put("de.micromata.genome.gwiki.model.config.GwikiFileContextBootstrapConfigLoader.fileName",
        "src/main/external_resources/GWikiContext.xml");
    initParams.put("de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader.fileName",
        "GWikiTestContext.xml");
    sc.setServlet(GWikiServlet.class, "gwiki", initParams);
    msc.setServletContext(sc);
    servlet = GWikiServlet.INSTANCE;
    return this;
  }

  public GWikiTestBuilder dumpLastResponseToOut()
  {
    System.out.println(dumpLastResponse());
    return this;
  }

  public GWikiTestBuilder followRedirect()
  {
    assertHasRedirect();
    return serve(lastResponse.getRedirectUrl());
  }

  public String dumpLastResponse()
  {
    StringBuilder sb = new StringBuilder();
    if (lastResponse == null) {
      sb.append("lastResponse is null");
      return sb.toString();
    }
    if (StringUtils.isNotEmpty(lastResponse.getRedirectUrl()) == true) {
      sb.append("redirect: ").append(lastResponse.getRedirectUrl()).append("\n");
    }
    String outp = lastResponse.getOutputString();
    if (StringUtils.isNotEmpty(outp) == true) {
      sb.append("Out:\n").append(outp).append("\n");
    }
    return sb.toString();
  }

  public void fail(String msg)
  {
    throw new RuntimeException(msg + ": " + dumpLastResponse());
  }

  public GWikiTestBuilder assertHasRedirect()
  {
    if (StringUtils.isNotEmpty(lastResponse.getRedirectUrl()) == true) {
      return this;
    }
    fail("Has no redirect");
    return this;
  }

  public GWikiTestBuilder assertRedirect(String pageId)
  {
    assertHasRedirect();
    String lred = lastResponse.getRedirectUrl();
    if (lred.startsWith("/") == true) {
      lred = lred.substring(1);
    }
    if (lred.equals(pageId) == true) {
      fail("Expecting redirect to: " + pageId);
    }
    return this;
  }

  public GWikiTestBuilder login(String user, String password)
  {
    serve("admin/Login", "method_onLogin", "true", "user", user, "password", password);
    assertHasRedirect();
    return this;
  }

  public StandaloneHttpServletRequest createReq(String pageId)
  {
    StandaloneHttpServletRequest req = new StandaloneHttpServletRequest("", "");
    req.setSession(session);
    if (pageId.startsWith("/") == true) {
      pageId = pageId.substring(1);
    }
    req.setPathInfo(pageId);
    return req;
  }

  public StandaloneHttpServletResponse createResp()
  {
    return new StandaloneHttpServletResponse();
  }

  public GWikiTestBuilder serve(String pageId)
  {
    return serve(createReq(pageId), createResp());
  }

  public GWikiTestBuilder serve(String pageId, String... postArgs)
  {
    StandaloneHttpServletRequest req = createReq(pageId);
    for (int i = 0; i < postArgs.length - 1; ++i) {
      req.getParameterMap().put(postArgs[i], new String[] { postArgs[i + 1] });
      ++i;
    }
    return serve(req, createResp());
  }

  public GWikiTestBuilder serve(StandaloneHttpServletRequest req, StandaloneHttpServletResponse resp)
  {
    lastResponse = resp;
    lastRequest = req;

    // GWikiWeb gwiki = GWikiWeb.get();
    // lastWikiContext = new GWikiContext(gwiki, null, lastRequest, lastResponse);
    try {
      servlet.service(lastRequest, lastResponse);
      Throwable ex = (Throwable) lastRequest.getAttribute("exception");
      if (ex != null) {
        throw ex;
      }
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
    return this;
  }

  public GWikiContext createWikiContext()
  {
    StandaloneHttpServletRequest req = createReq("index");
    StandaloneHttpServletResponse resp = createResp();
    if (servlet.hasWikiWeb() == false) {
      servlet.initWiki(req, resp);
    }
    lastWikiContext = new GWikiContext(servlet.getWikiWeb(), servlet, req, resp);
    GWikiContext.setCurrent(lastWikiContext);
    return lastWikiContext;
  }

  public GWikiDAOContext getDaoContext()
  {
    return daoContext;
  }

  public void setDaoContext(GWikiDAOContext daoContext)
  {
    this.daoContext = daoContext;
  }

  public StandaloneHttpServletResponse getLastResponse()
  {
    return lastResponse;
  }

  public void setLastResponse(StandaloneHttpServletResponse lastResponse)
  {
    this.lastResponse = lastResponse;
  }

  public StandaloneHttpServletRequest getLastRequest()
  {
    return lastRequest;
  }

  public void setLastRequest(StandaloneHttpServletRequest lastRequest)
  {
    this.lastRequest = lastRequest;
  }

  public GWikiContext getLastWikiContext()
  {
    return lastWikiContext;
  }

  public void setLastWikiContext(GWikiContext lastWikiContext)
  {
    this.lastWikiContext = lastWikiContext;
  }

  public GWikiServlet getServlet()
  {
    return servlet;
  }

  public void setServlet(GWikiServlet servlet)
  {
    this.servlet = servlet;
  }
}
