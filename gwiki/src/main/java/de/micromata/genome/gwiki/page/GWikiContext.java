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

package de.micromata.genome.gwiki.page;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.collections15.ArrayStack;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.taglibs.standard.tag.el.core.UrlTag;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementFinder;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiTextArtefakt;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.impl.GWikiBinaryAttachmentArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiI18nElement;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionMessages;
import de.micromata.genome.gwiki.page.impl.actionbean.CommonMultipartRequest;
import de.micromata.genome.gwiki.page.impl.actionbean.SimpleActionMessage;
import de.micromata.genome.gwiki.page.search.NormalizeUtils;
import de.micromata.genome.gwiki.umgmt.GWikiUserAuthorization;
import de.micromata.genome.gwiki.utils.AbstractAppendable;
import de.micromata.genome.gwiki.utils.TimeUtils;
import de.micromata.genome.gwiki.utils.WebUtils;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.RuntimeIOException;
import de.micromata.genome.util.types.ArraySet;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * A GWikiContext is the central state to hold information in a request/response cycle.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiContext extends AbstractAppendable implements GWikiPropKeys
{
  /**
   * Request attribute with List<String> of CSS for content.
   */
  public static final String CONTENT_CSS = "de.micromata.genome.gwiki.page.GWikiContext.CONTENT_CSS";

  /**
   * Request attribute with List<String> of CSS for javascript.
   */
  public static final String CONTENT_JS = "de.micromata.genome.gwiki.page.GWikiContext.CONTENT_JS";

  /**
   * Will be set after initialization.
   */
  protected GWikiWeb wikiWeb;

  private ArrayStack<GWikiElement> wikiElements = new ArrayStack<GWikiElement>();

  private HttpServletRequest request;

  private HttpServletResponse response;

  final HttpServlet servlet;

  protected PageContext pageContext;

  private ActionMessages validationErrors;

  private Map<String, GWikiArtefakt< ? >> parts = new HashMap<String, GWikiArtefakt< ? >>();

  private GWikiArtefakt< ? > currentPart;

  /**
   * Arguments for Macros.
   */
  private Map<String, Object> nativeArgs;

  private List<GWikiI18nElement> i18nMaps;

  private static ThreadLocal<GWikiContext> CURRENT_INSTANCE = new ThreadLocal<GWikiContext>();

  /**
   * Combination of RenderModes.
   */
  private int renderMode = 0;;

  private int domIdCounter = 1;

  private Set<String> requiredCss = new ArraySet<String>();

  private Set<String> requiredJs = new ArraySet<String>();

  /**
   * Additionally required html header lines.
   */
  private List<String> requiredHeader = new ArrayList<String>();

  public GWikiContext(GWikiWeb wikiWeb, HttpServlet servlet, HttpServletRequest request, HttpServletResponse response)
  {
    this.request = request;
    this.response = response;
    this.servlet = servlet;
    this.wikiWeb = wikiWeb;
  }

  public static GWikiContext getCurrent()
  {
    return CURRENT_INSTANCE.get();
  }

  /**
   * get current context. if not set, creates a standalone context.
   * 
   * @return
   */
  public static GWikiContext getCreateContext()
  {
    GWikiContext ctx = getCurrent();
    if (ctx != null) {
      return ctx;
    }
    ctx = GWikiStandaloneContext.create();
    return ctx;
  }

  public static void setCurrent(GWikiContext ctx)
  {
    CURRENT_INSTANCE.set(ctx);
  }

  public String genHtmlId(String suffix)
  {
    return "gwiki" + (++domIdCounter) + suffix;
  }

  /**
   * return the last generated html id.
   * 
   * @param suffix
   * @return
   */
  public String getLastHtmlId(String suffix)
  {
    return "gwiki" + (domIdCounter) + suffix;
  }

  public void addSimpleValidationError(String message)
  {
    if (validationErrors == null)
      validationErrors = new ActionMessages();
    validationErrors.put("", new SimpleActionMessage(message));
  }

  public void addValidationError(String msgKey, Object... args)
  {
    if (validationErrors == null)
      validationErrors = new ActionMessages();
    String message = wikiWeb.getI18nProvider().translate(this, msgKey, null, args);
    validationErrors.put("", new SimpleActionMessage(message));
  }

  public void addValidationFieldError(String msgKey, String field, Object... args)
  {
    if (validationErrors == null)
      validationErrors = new ActionMessages();
    String message = wikiWeb.getI18nProvider().translate(this, msgKey, null, args);
    validationErrors.put(field, new SimpleActionMessage(message));
  }

  public boolean hasValidationErrors()
  {
    return validationErrors != null && validationErrors.isEmpty() == false;
  }

  public static String getPageIdFromTitle(String title)
  {
    String id = StringUtils.replace(StringUtils.replace(StringUtils.replace(title, "\t", "_"), " ", "_"), "\\", "/");
    id = NormalizeUtils.normalizeToPath(id);
    return id;
  }

  public Map<String, Object> pushNativeParams(Map<String, Object> m)
  {
    Map<String, Object> ret = nativeArgs;
    nativeArgs = m;
    return ret;
  }

  /**
   * 
   * @param pageId a/b
   * @return a/b -> a/, c -> ""
   */
  public static String getParentDirPathFromPageId(String pageId)
  {
    if (StringUtils.isEmpty(pageId) == true)
      return "";
    int pidx = pageId.lastIndexOf('/');
    if (pidx == -1)
      return "";
    return pageId.substring(0, pidx + 1);
  }

  public static String getNamePartFromPageId(String pageId)
  {
    if (StringUtils.isEmpty(pageId) == true)
      return "";
    int pidx = pageId.lastIndexOf('/');
    if (pidx == -1)
      return pageId;
    return pageId.substring(pidx + 1);
  }

  public static String genId(String newId, String parentId)
  {
    return getParentDirPathFromPageId(parentId) + newId;
  }

  public void include(String id)
  {
    GWikiElement el = wikiWeb.getElement(id);
    el.serve(this);
  }

  /**
   * Include (render) an artefakt of given pageId
   * 
   * @param pageId
   * @param partName if null uses main part
   * @throws nothing if page is not found.
   */
  public void includeArtefakt(String pageId, String partName)
  {
    GWikiElement el = wikiWeb.getElement(pageId);
    if (el == null) {
      return;
    }
    GWikiArtefakt< ? > art;
    if (partName == null) {
      art = el.getMainPart();
    } else {
      art = el.getPart(partName);
    }
    if (art == null || (art instanceof GWikiExecutableArtefakt< ? >) == false) {
      return;
    }
    GWikiExecutableArtefakt< ? > exart = (GWikiExecutableArtefakt< ? >) art;
    exart.render(this);
  }

  public void includeText(String id)
  {
    flush();
    GWikiElement el = wikiWeb.getElement(id);
    GWikiArtefakt< ? > fact = el.getMainPart();
    if (fact instanceof GWikiTextArtefakt< ? >) {
      GWikiTextArtefakt< ? > text = (GWikiTextArtefakt< ? >) fact;
      append(text.getStorageData());
    } else if (fact instanceof GWikiBinaryAttachmentArtefakt) {
      String data = new String(((GWikiBinaryAttachmentArtefakt) fact).getCompiledObject());
      append(data);
    } else {
      throw new RuntimeException("Unsupported gwiki artefakt type to include: " + fact.getClass().getName());
    }
  }

  public String globalUrl(String lurl)
  {
    if (wikiWeb.findElement(lurl) == null) {
      if (lurl.startsWith("/") == false) {
        GWikiElement wikiElement = getWikiElement();
        if (wikiElement != null) {
          String pa = getParentDirPathFromPageId(wikiElement.getElementInfo().getId());
          pa = pa + lurl;
          if (wikiWeb.findElement(pa) != null) {
            lurl = pa;
          }
        }
      }
    }

    String url = FileNameUtils.join(wikiWeb.getWikiConfig().getPublicURL(), lurl);
    return url;
  }

  public String localUrl(String lurl)
  {
    String url = lurl;
    if (url.startsWith("/") == false)
      url = "/" + url;

    String res = url;
    if (lurl.indexOf("/") == -1 && wikiWeb.findElementInfo(res) == null) {
      GWikiElement wikiElement = getWikiElement();
      if (wikiElement != null) {
        String pa = getParentDirPathFromPageId(wikiElement.getElementInfo().getId());
        pa = pa + lurl;
        if (wikiWeb.findElementInfo(pa) != null) {
          res = "/" + pa;
        }
      }
    }
    res = wikiWeb.getServletPath() + res;
    if (pageContext != null) {
      try {
        res = UrlTag.resolveUrl(res, null, pageContext);
      } catch (JspException ex) {
        throw new RuntimeException(ex);
      }
    } else {
      String ctxpath = getRequest().getContextPath();
      String svpath = getRequest().getServletPath();
      res = ctxpath + svpath + res;
    }
    if (response != null) {
      res = response.encodeURL(res);
    }
    return res;
  }

  public String renderLocalUrl(String target)
  {
    return renderLocalUrl(target, null, null);
  }

  public String genNewPageLink(String target, String title, String addArgs)
  {
    StringBuilder sb = new StringBuilder();
    title = getTranslatedProp(title);
    boolean allow = wikiWeb.getAuthorization().isAllowToCreate(this, getWikiElement().getElementInfo());
    if (allow == false) {

      sb.append("<i>").append(StringEscapeUtils.escapeHtml(title)).append("</i>");
    } else {
      if (StringUtils.isBlank(title) == true) {
        title = target;
        target = getPageIdFromTitle(target);
      } else if (StringUtils.isBlank(target) == true) {
        target = getPageIdFromTitle(title);
      }
      String tid = target;
      String pid = getWikiElement().getElementInfo().getId();

      tid = genId(tid, pid);

      sb.append("<a href='")//
          .append(localUrl("edit/EditPage?newPage=true&parentPageId="))//
          .append(WebUtils.encodeUrlParam(pid))//
          .append("&pageId=") //
          .append(WebUtils.encodeUrlParam(tid))//
          .append("&title=")//
          .append(WebUtils.encodeUrlParam(title))//
          .append("'>").append(StringEscapeUtils.escapeHtml(title)).append("</a>");
    }
    return sb.toString();
  }

  public String escape(String text)
  {
    return StringEscapeUtils.escapeHtml(text);
  }

  public String renderExistingLink(GWikiElementInfo ei, String title, String addArgs)
  {
    if (title == null) {
      title = getTranslatedProp(ei.getTitle());
    } else {
      title = getTranslatedProp(title);
    }

    StringBuilder sb = new StringBuilder();
    boolean allow = wikiWeb.getAuthorization().isAllowToView(this, ei);
    if (allow == false) {
      sb.append("<i>").append(StringEscapeUtils.escapeHtml(title)).append("</i>");
    } else {
      String url = localUrl("/" + ei.getId());
      if (addArgs != null) {
        url += addArgs;
      }
      sb.append("<a href='").append(url).append("'>").append(StringEscapeUtils.escapeHtml(title)).append("</a>");
    }
    return sb.toString();
  }

  public String renderExistingLinkWithAttr(GWikiElementInfo ei, String title, String addArgs, String... attr)
  {
    if (title == null) {
      title = getTranslatedProp(ei.getTitle());
    } else {
      title = getTranslatedProp(title);
    }

    StringBuilder sb = new StringBuilder();
    boolean allow = wikiWeb.getAuthorization().isAllowToView(this, ei);
    if (allow == false) {
      sb.append("<i>").append(StringEscapeUtils.escapeHtml(title)).append("</i>");
    } else {
      String url = localUrl("/" + ei.getId());
      if (addArgs != null) {
        url += addArgs;
      }
      sb.append("<a href='").append(url).append("'");
      for (int i = 0; i < attr.length; i += 2) {
        sb.append(" ").append(attr[i]).append("='").append(attr[i + 1]).append("' ");
      }
      sb.append(">").append(StringEscapeUtils.escapeHtml(title)).append("</a>");
    }
    return sb.toString();
  }

  public boolean isAllowTo(String right)
  {
    return wikiWeb.getAuthorization().isAllowTo(this, right);
  }

  public void ensureAllowTo(String right)
  {
    wikiWeb.getAuthorization().ensureAllowTo(this, right);
  }

  public String renderLocalUrl(String target, String title, String addArgs)
  {
    GWikiElementInfo ei = wikiWeb.findElementInfo(target);
    if (ei == null) {
      return genNewPageLink(target, title, addArgs);
    } else {
      return renderExistingLink(ei, title, addArgs);
    }
  }

  public String getTranslated(String key)
  {
    return wikiWeb.getI18nProvider().translate(this, key);
  }

  public String getTranslatedProp(String key)
  {
    return wikiWeb.getI18nProvider().translateProp(this, key);
  }

  public String getUserDateString(Date date)
  {
    if (date == null) {
      return "";
    }
    String tz = getWikiWeb().getAuthorization().getUserProp(this, GWikiUserAuthorization.USER_TZ);
    tz = StringUtils.defaultIfEmpty(tz, TimeZone.getDefault().getID());
    String df = getWikiWeb().getAuthorization().getUserProp(this, GWikiUserAuthorization.USER_DATEFORMAT);
    df = StringUtils.defaultIfEmpty(df, TimeUtils.ISO_DATETIME);
    return TimeUtils.formatDate(date, df, tz);
  }

  public TimeZone getUserTimeZone()
  {
    String tz = getWikiWeb().getAuthorization().getUserProp(this, GWikiUserAuthorization.USER_TZ);
    tz = StringUtils.defaultIfEmpty(tz, TimeZone.getDefault().getID());
    TimeZone timeZone = TimeZone.getTimeZone(tz);
    return timeZone;
  }

  public Date parseUserDateString(String ds)
  {
    if (StringUtils.isBlank(ds) == true) {
      return null;
    }
    String tz = getWikiWeb().getAuthorization().getUserProp(this, GWikiUserAuthorization.USER_TZ);
    tz = StringUtils.defaultIfEmpty(tz, TimeZone.getDefault().getID());
    String df = getWikiWeb().getAuthorization().getUserProp(this, GWikiUserAuthorization.USER_DATEFORMAT);
    df = StringUtils.defaultIfEmpty(df, TimeUtils.ISO_DATETIME);
    return TimeUtils.parseDate(ds, df, tz);
  }

  public String getUserStringProp(String key)
  {
    return getUserStringProp(key, "");
  }

  public String getUserStringProp(String key, String defaultValue)
  {
    return StringUtils.defaultString(getWikiWeb().getAuthorization().getUserProp(this, key), defaultValue);
  }

  public boolean getUserBooleanProp(String key)
  {
    return getUserBooleanProp(key, false);
  }

  public boolean getUserBooleanProp(String key, boolean defaultValue)
  {
    return StringUtils.equals("true", getUserStringProp(key, defaultValue ? "true" : "false"));
  }

  /**
   * prints to JspWriter if exists, otherwise to response outputstream
   * 
   * @param text
   * @return
   */
  public GWikiContext append(String text)
  {
    try {
      if (pageContext != null) {
        pageContext.getOut().write(text);
      } else {
        getResponseOutputStream().print(text);
      }
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
    return this;
  }

  public GWikiContext flush()
  {
    try {
      if (pageContext != null) {
        pageContext.getOut().flush();
      } else {
        getResponseOutputStream().flush();
      }
    } catch (IOException ex) {
      // ignore it here.
      // throw new RuntimeIOException(ex);
    }
    return this;
  }

  public GWikiContext appendEscText(String text)
  {
    return append(StringEscapeUtils.escapeHtml(text));
  }

  public GWikiContext append(Object... objects)
  {
    try {
      if (pageContext != null) {
        for (Object o : objects) {
          pageContext.getOut().write(o == null ? "" : o.toString());
        }
      } else {
        for (Object o : objects) {
          getResponseOutputStream().print(o == null ? "" : o.toString());
        }
      }
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
    return this;
  }

  public GWikiElementFinder getElementFinder()
  {
    return new GWikiElementFinder(this);
  }

  public <T> T runElement(GWikiElement el, CallableX<T, RuntimeException> cb)
  {
    try {
      pushWikiElement(el);
      return cb.call();
    } finally {
      popWikiElement();
    }
  }

  @SuppressWarnings("unchecked")
  public boolean runWithParts(Map<String, GWikiArtefakt< ? >> newParts, CallableX<Boolean, RuntimeException> cb)
  {
    Map<String, GWikiArtefakt< ? >> pushParts = null;
    if (newParts.size() > 0) {
      for (Map.Entry<String, GWikiArtefakt< ? >> npe : newParts.entrySet()) {
        GWikiArtefakt backup = parts.put(npe.getKey(), npe.getValue());
        if (backup != null) {
          if (pushParts == null) {
            pushParts = new HashMap<String, GWikiArtefakt< ? >>();
          }
          pushParts.put(npe.getKey(), backup);
        }
      }
    }
    try {
      return cb.call();
    } finally {
      if (pushParts != null) {
        parts.putAll(pushParts);
      }
    }
  }

  public boolean runWithArtefakt(GWikiArtefakt< ? > artefakt, CallableX<Boolean, RuntimeException> cb)
  {
    GWikiArtefakt< ? > oa = getCurrentPart();
    try {
      setCurrentPart(artefakt);
      return cb.call();
    } finally {
      setCurrentPart(oa);
    }
  }

  /**
   * runs the callback code inside specified tenant
   * 
   * @param tenantId
   * @param wikiSelector
   * @param callBack
   * @return
   */
  public <R, E extends RuntimeException> R runInTenantContext(String tenantId, GWikiMultipleWikiSelector wikiSelector,
      CallableX<R, E> callBack)
  {
    String currentTenant = null;
    try {
      currentTenant = wikiSelector.getTenantId(GWikiServlet.INSTANCE, getRequest());
      if (StringUtils.equals(currentTenant, tenantId) == false) {
        wikiSelector.enterTenant(this, tenantId);
      }
      return callBack.call();
    } finally {
      // switch to previous tenant
      if (StringUtils.equals(currentTenant, tenantId) == false) {
        if (StringUtils.isBlank(currentTenant) == true) {
          wikiSelector.leaveTenant(this);
        } else {
          wikiSelector.enterTenant(this, currentTenant);
        }
      }
    }
  }

  /**
   * use getCurrentElement()
   * 
   * @return
   */
  @Deprecated
  public GWikiElement getWikiElement()
  {
    return getCurrentElement();
  }

  public GWikiElement getCurrentElement()
  {
    if (wikiElements.isEmpty() == true) {
      return null;
    }
    return wikiElements.peek();

  }

  public void pushWikiElement(GWikiElement wikiElement)
  {
    this.wikiElements.push(wikiElement);
  }

  public GWikiElement popWikiElement()
  {
    return this.wikiElements.pop();
  }

  public void setWikiElement(GWikiElement wikiElement)
  {
    if (wikiElements.isEmpty() == false) {
      wikiElements.pop();
    }
    wikiElements.push(wikiElement);
  }

  public GWikiElement getParentWikiElement()
  {
    if (wikiElements.size() < 2) {
      return null;
    }
    return wikiElements.peek(1);
  }

  public HttpServletRequest getRequest()
  {
    return request;
  }

  public HttpServletResponse getResponse()
  {
    return response;
  }

  public HttpServlet getServlet()
  {
    return servlet;
  }

  public String getRequestParameter(String key)
  {
    return request.getParameter(key);
  }

  public String[] getRequestValues(String key)
  {
    return request.getParameterValues(key);
  }

  public void setRequestAttribute(String key, Object value)
  {
    if (value == null)
      request.removeAttribute(key);
    else
      request.setAttribute(key, value);
  }

  public FileItem getFileItem(String key)
  {
    if ((request instanceof CommonMultipartRequest) == false)
      return null;

    CommonMultipartRequest qr = (CommonMultipartRequest) request;
    return qr.getFileItems().get(key);
  }

  public Object getRequestAttribute(String key)
  {
    return request.getAttribute(key);
  }

  public boolean getBooleanRequestAttribute(String key)
  {
    return request.getAttribute(key) == Boolean.TRUE;

  }

  @SuppressWarnings("unchecked")
  public List<String> getStringListRequestAttribute(String key)
  {
    Object obj = request.getAttribute(key);
    if ((obj instanceof List) == true) {
      return (List) obj;
    }
    List<String> list = new ArrayList<String>();
    request.setAttribute(key, list);
    return list;

  }

  public Object getSessionAttribute(String key)
  {
    return wikiWeb.getSessionProvider().getSessionAttribute(this, key);
  }

  public void setSessionAttribute(String key, Object value)
  {
    if (value != null && (value instanceof Serializable) == false) {
      throw new RuntimeException("Session object with key is not serializable: " + key + "; " + value.getClass().getName());
    }
    wikiWeb.getSessionProvider().setSessionAttribute(this, key, (Serializable) value);
  }

  public void removeSessionAttribute(String key)
  {
    wikiWeb.getSessionProvider().removeSessionAttribute(this, key);
  }

  public void setCharacterEncoding(String enc) throws UnsupportedEncodingException
  {
    request.setCharacterEncoding(enc);
  }

  public void sendErrorSilent(int errorCode)
  {
    try {
      sendError(errorCode);
    } catch (IOException ex) {
      // nothing
    }
  }

  public void sendError(int errorCode) throws IOException
  {
    response.sendError(errorCode);
  }

  public void setResponseStatus(int status)
  {
    response.setStatus(status);
  }

  public void sendError(int errorCode, String errorMessage) throws IOException
  {
    response.sendError(errorCode, errorMessage);
  }

  public ServletOutputStream getResponseOutputStream() throws IOException
  {
    return response.getOutputStream();
  }

  public void createSession()
  {
    getSession(true);
  }

  public HttpSession getCreateSession()
  {
    return getSession(true);
  }

  public HttpSession getSession(boolean create)
  {
    if (create == false && request == null) {
      return null;
    }
    Validate.notNull(request, "request not set");
    return request.getSession(create);
  }

  /**
   * set a cookie
   */
  @SuppressWarnings("deprecation")
  public void setCookie(String key, String value)
  {
    String cvalue = URLEncoder.encode(value);
    Cookie tsc = new Cookie(key, cvalue);
    tsc.setPath(getWikiWeb().getContextPath());
    if (StringUtils.isEmpty(tsc.getPath()) == true) {
      tsc.setPath("/");
    }
    tsc.setMaxAge((int) TimeInMillis.YEAR);
    response.addCookie(tsc);

  }

  public void clearCookie(String key)
  {
    Cookie tsc = new Cookie(key, "");
    tsc.setPath(getWikiWeb().getContextPath());
    // tsc.setSecure(true);
    tsc.setMaxAge(0);
    response.addCookie(tsc);
  }

  /**
   * get a named cookie
   * 
   * @return by default null
   */
  public String getCookie(String key)
  {
    return getCookie(key, null);
  }

  @SuppressWarnings("deprecation")
  public String getCookie(String key, String defaultValue)
  {
    Cookie[] cookies = request.getCookies();
    if (cookies == null)
      return defaultValue;
    for (Cookie co : cookies) {
      if (StringUtils.equals(key, co.getName()) == true) {
        String sv = co.getValue();
        sv = URLDecoder.decode(sv);
        return sv;
      }
    }

    return defaultValue;
  }

  public String getSkin()
  {
    if (getCurrentElement() != null) {
      String skin = getCurrentElement().getElementInfo().getRecStringValue(SKIN, null, this);
      if (StringUtils.isNotEmpty(skin) == true) {
        return skin;
      }
    }
    String userStyle = wikiWeb.getAuthorization().getUserProp(this, "skin");
    if (StringUtils.isEmpty(userStyle) == false) {
      if (wikiWeb.getWikiConfig().isSkinAvailable(this, userStyle) == true) {
        return userStyle;
      }
    }
    return wikiWeb.getStandardSkin();
  }

  public void skinInclude(String name)
  {
    String skin = getSkin();
    String id = "inc/" + skin + "/" + name;
    include(id);
  }

  public void addCookie(Cookie cookie)
  {
    response.addCookie(cookie);
  }

  public String getUserAgent()
  {
    return request.getHeader("user-agent");
  }

  public void addContentCss(String localPath)
  {
    getStringListRequestAttribute(CONTENT_CSS).add(localPath);
  }

  public List<String> getContentCsse()
  {
    return getStringListRequestAttribute(CONTENT_CSS);
  }

  public String getRealContextPath()
  {
    String cp = (String) request.getAttribute("javax.servlet.include.servlet_path");
    if (cp != null) {
      return cp;
    }
    cp = (String) request.getAttribute("javax.servlet.forward.servlet_path");
    if (cp != null) {
      return cp;
    }
    cp = request.getContextPath();
    return cp;
  }

  public String getRealPathInfo()
  {
    String cp = (String) request.getAttribute("javax.servlet.include.path_info");
    if (cp != null) {
      return cp;
    }
    cp = (String) request.getAttribute("javax.servlet.forward.path_info");
    if (cp != null) {
      return cp;
    }
    cp = request.getPathInfo();
    return cp;
  }

  public String getRealServletPath()
  {
    String cp = (String) request.getAttribute("javax.servlet.include.servlet_path");
    if (cp != null) {
      return cp;
    }
    cp = (String) request.getAttribute("javax.servlet.forward.servlet_path");
    if (cp != null) {
      return cp;
    }
    cp = request.getServletPath();
    return cp;
  }

  public String getRealRequestUri()
  {
    String cp = (String) request.getAttribute("javax.servlet.include.request_uri");
    if (cp != null) {
      return cp;
    }
    cp = (String) request.getAttribute("javax.servlet.forward.request_uri");
    if (cp != null) {
      return cp;
    }
    cp = request.getRequestURI();
    return cp;
  }

  public String getRealQueryString()
  {
    String cp = (String) request.getAttribute("javax.servlet.include.query_string");
    if (cp != null) {
      return cp;
    }
    cp = (String) request.getAttribute("javax.servlet.forward.query_string");
    if (cp != null) {
      return cp;
    }
    cp = request.getQueryString();
    return cp;
  }

  public GWikiWeb getWikiWeb()
  {
    return wikiWeb;
  }

  public void setWikiWeb(GWikiWeb wikiWeb)
  {
    this.wikiWeb = wikiWeb;
  }

  public PageContext getPageContext()
  {
    return pageContext;
  }

  public PageContext getCreatePageContext()
  {
    if (pageContext != null) {
      return pageContext;
    }
    return pageContext = wikiWeb.getJspProcessor().createPageContext(this);
  }

  public void setPageContext(PageContext pageContext)
  {
    this.pageContext = pageContext;
  }

  public Map<String, GWikiArtefakt< ? >> getParts()
  {
    return parts;
  }

  public void setParts(Map<String, GWikiArtefakt< ? >> parts)
  {
    this.parts = parts;
  }

  public void setRequest(HttpServletRequest request)
  {
    this.request = request;
  }

  public void setResponse(HttpServletResponse response)
  {
    this.response = response;
  }

  public GWikiArtefakt< ? > getCurrentPart()
  {
    return currentPart;
  }

  public void setCurrentPart(GWikiArtefakt< ? > currentPart)
  {
    this.currentPart = currentPart;
  }

  public Map<String, Object> getNativeArgs()
  {
    return nativeArgs;
  }

  public void setNativeArgs(Map<String, Object> nativeArgs)
  {
    this.nativeArgs = nativeArgs;
  }

  public ActionMessages getValidationErrors()
  {
    return validationErrors;
  }

  public void setValidationErrors(ActionMessages validationErrors)
  {
    this.validationErrors = validationErrors;
  }

  public List<GWikiI18nElement> getI18nMaps()
  {
    return i18nMaps;
  }

  public void setI18nMaps(List<GWikiI18nElement> i18nMaps)
  {
    this.i18nMaps = i18nMaps;
  }

  public int getRenderMode()
  {
    return renderMode;
  }

  public void setRenderMode(int renderMode)
  {
    this.renderMode = renderMode;
  }

  public Set<String> getRequiredCss()
  {
    return requiredCss;
  }

  public void setRequiredCss(Set<String> requiredCss)
  {
    this.requiredCss = requiredCss;
  }

  public Set<String> getRequiredJs()
  {
    return requiredJs;
  }

  public void setRequiredJs(Set<String> requiredJs)
  {
    this.requiredJs = requiredJs;
  }

  public List<String> getRequiredHeader()
  {
    return requiredHeader;
  }

  public void setRequiredHeader(List<String> requiredHeader)
  {
    this.requiredHeader = requiredHeader;
  }

}
