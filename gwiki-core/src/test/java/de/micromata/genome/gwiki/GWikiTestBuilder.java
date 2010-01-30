package de.micromata.genome.gwiki;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.stripes.mock.MockServletConfig;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.model.config.GwikiFileContextBootstrapConfigLoader;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.gspt.GenomeTemplateUtils;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.gwiki.web.StandaloneHttpServletRequest;
import de.micromata.genome.gwiki.web.StandaloneHttpServletResponse;
import de.micromata.genome.test.web.SimHttpSession;
import de.micromata.genome.test.web.SimServletContext;

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
    if (GWikiServlet.INSTANCE != null) {
      this.servlet = GWikiServlet.INSTANCE;
      return this;
    }
    GenomeTemplateUtils.IN_UNITEST = true;
    MockServletConfig msc = new MockServletConfig();
    msc.setServletName("gwiki");
    SimServletContext sc = new SimServletContext("gwiki", "dev/gwikiweb");
    Map<String, String> initParams = new HashMap<String, String>();
    initParams.put("de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader.className",
        GwikiFileContextBootstrapConfigLoader.class.getName());
    initParams
        .put("de.micromata.genome.gwiki.model.config.GwikiFileContextBootstrapConfigLoader.fileName", "dev/jettydev/GWikiContext.xml");
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
      req.getParameterMap().put(postArgs[i], new String[] { postArgs[i + 1]});
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
