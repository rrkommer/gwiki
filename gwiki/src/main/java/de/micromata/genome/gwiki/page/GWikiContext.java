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

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.collections4.ArrayStack;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
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
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBean;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionMessages;
import de.micromata.genome.gwiki.page.impl.actionbean.CommonMultipartRequest;
import de.micromata.genome.gwiki.page.impl.actionbean.SimpleActionMessage;
import de.micromata.genome.gwiki.page.search.NormalizeUtils;
import de.micromata.genome.gwiki.umgmt.GWikiUserAuthorization;
import de.micromata.genome.gwiki.utils.AbstractAppendable;
import de.micromata.genome.gwiki.utils.TimeUtils;
import de.micromata.genome.gwiki.utils.WebUtils;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.util.collections.ArraySet;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.RuntimeIOException;
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
   * Request attribute with List&lt;String&gt; of CSS for content.
   */
  public static final String CONTENT_CSS = "de.micromata.genome.gwiki.page.GWikiContext.CONTENT_CSS";

  /**
   * Request attribute with List&lt;String&gt; of CSS for javascript.
   */
  public static final String CONTENT_JS = "de.micromata.genome.gwiki.page.GWikiContext.CONTENT_JS";

  /**
   * The Constant HEADER_STATEMENTS.
   */
  public static final String HEADER_STATEMENTS = "de.micromata.genome.gwiki.page.GWikiContext.HEADER_STATEMENTS";

  /**
   * Will be set after initialization.
   */
  protected GWikiWeb wikiWeb;

  /**
   * The wiki elements.
   */
  private ArrayStack<GWikiElement> wikiElements = new ArrayStack<GWikiElement>();

  /**
   * The request.
   */
  private HttpServletRequest request;

  /**
   * The response.
   */
  private HttpServletResponse response;

  /**
   * The servlet.
   */
  final HttpServlet servlet;

  /**
   * The page context.
   */
  protected PageContext pageContext;

  /**
   * The validation errors.
   */
  private ActionMessages validationErrors;

  /**
   * The parts.
   */
  private Map<String, GWikiArtefakt<?>> parts = new HashMap<String, GWikiArtefakt<?>>();

  /**
   * The current part.
   */
  private GWikiArtefakt<?> currentPart;

  /**
   * Arguments for Macros.
   */
  private Map<String, Object> nativeArgs;

  /**
   * The i18n maps.
   */
  private List<GWikiI18nElement> i18nMaps;

  /**
   * The current instance.
   */
  private static ThreadLocal<GWikiContext> CURRENT_INSTANCE = new ThreadLocal<GWikiContext>();

  /**
   * Combination of RenderModes.
   */
  private int renderMode = 0;;

  /**
   * The dom id counter.
   */
  private int domIdCounter = 1;

  /**
   * The required css.
   */
  private Set<String> requiredCss = new ArraySet<String>();

  /**
   * The required js.
   */
  private Set<String> requiredJs = new ArraySet<String>();

  /**
   * Additionally required html header lines.
   */
  private List<String> requiredHeader = new ArrayList<String>();

  /**
   * May set by the controller;.
   */
  private ActionBean actionBean;

  /**
   * Instantiates a new g wiki context.
   *
   * @param wikiWeb the wiki web
   * @param servlet the servlet
   * @param request the request
   * @param response the response
   */
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

  /**
   * Gen html id.
   *
   * @param suffix the suffix
   * @return the string
   */
  public String genHtmlId(String suffix)
  {
    return "gwiki" + (++domIdCounter) + suffix;
  }

  /**
   * return the last generated html id.
   *
   * @param suffix the suffix
   * @return the last html id
   */
  public String getLastHtmlId(String suffix)
  {
    return "gwiki" + (domIdCounter) + suffix;
  }

  /**
   * Adds the simple validation error.
   *
   * @param message the message
   */
  public void addSimpleValidationError(String message)
  {
    if (validationErrors == null) {
      validationErrors = new ActionMessages();
    }
    validationErrors.put("", new SimpleActionMessage(message));
  }

  /**
   * Adds the validation error.
   *
   * @param msgKey the msg key
   * @param args the args
   */
  public void addValidationError(String msgKey, Object... args)
  {
    if (validationErrors == null) {
      validationErrors = new ActionMessages();
    }
    String message = wikiWeb.getI18nProvider().translate(this, msgKey, null, args);
    validationErrors.put("", new SimpleActionMessage(message));
  }

  /**
   * Adds the validation field error.
   *
   * @param msgKey the msg key
   * @param field the field
   * @param args the args
   */
  public void addValidationFieldError(String msgKey, String field, Object... args)
  {
    if (validationErrors == null) {
      validationErrors = new ActionMessages();
    }
    String message = wikiWeb.getI18nProvider().translate(this, msgKey, null, args);
    validationErrors.put(field, new SimpleActionMessage(message));
  }

  /**
   * Checks for validation errors.
   *
   * @return true, if successful
   */
  public boolean hasValidationErrors()
  {
    return validationErrors != null && validationErrors.isEmpty() == false;
  }

  /**
   * Gets the page id from title.
   *
   * @param title the title
   * @return the page id from title
   */
  public static String getPageIdFromTitle(String title)
  {
    String id = StringUtils.replace(StringUtils.replace(StringUtils.replace(title, "\t", "_"), " ", "_"), "\\", "/");
    id = NormalizeUtils.normalizeToPath(id);
    return id;
  }

  /**
   * Push native params.
   *
   * @param m the m
   * @return the map
   */
  public Map<String, Object> pushNativeParams(Map<String, Object> m)
  {
    Map<String, Object> ret = nativeArgs;
    nativeArgs = m;
    return ret;
  }

  /**
   * Gets the parent dir path from page id.
   *
   * @param pageId a/b
   * @return a/b to a/, c to ""
   */
  public static String getParentDirPathFromPageId(String pageId)
  {
    if (StringUtils.isEmpty(pageId) == true) {
      return "";
    }
    int pidx = pageId.lastIndexOf('/');
    if (pidx == -1) {
      return "";
    }
    return pageId.substring(0, pidx + 1);
  }

  /**
   * Gets the name part from page id.
   *
   * @param pageId the page id
   * @return the name part from page id
   */
  public static String getNamePartFromPageId(String pageId)
  {
    if (StringUtils.isEmpty(pageId) == true) {
      return "";
    }
    int pidx = pageId.lastIndexOf('/');
    if (pidx == -1) {
      return pageId;
    }
    return pageId.substring(pidx + 1);
  }

  /**
   * Gen id.
   *
   * @param newId the new id
   * @param parentId the parent id
   * @return the string
   */
  public static String genId(String newId, String parentId)
  {
    return getParentDirPathFromPageId(parentId) + newId;
  }

  /**
   * Include.
   *
   * @param id the id
   */
  public void include(String id)
  {
    GWikiElement el = wikiWeb.getElement(id);
    el.serve(this);
  }

  /**
   * Include (render) an artefakt of given pageId.
   *
   * @param pageId the page id
   * @param partName if null uses main part
   */
  public void includeArtefakt(String pageId, String partName)
  {
    GWikiElement el = wikiWeb.getElement(pageId);
    if (el == null) {
      return;
    }
    GWikiArtefakt<?> art;
    if (partName == null) {
      art = el.getMainPart();
    } else {
      art = el.getPart(partName);
    }
    if (art == null || (art instanceof GWikiExecutableArtefakt<?>) == false) {
      return;
    }
    GWikiExecutableArtefakt<?> exart = (GWikiExecutableArtefakt<?>) art;
    exart.render(this);
  }

  /**
   * Include text.
   *
   * @param id the id
   */
  public void includeText(String id)
  {
    flush();
    GWikiElement el = wikiWeb.getElement(id);
    GWikiArtefakt<?> fact = el.getMainPart();
    if (fact instanceof GWikiTextArtefakt<?>) {
      GWikiTextArtefakt<?> text = (GWikiTextArtefakt<?>) fact;
      append(text.getStorageData());
    } else if (fact instanceof GWikiBinaryAttachmentArtefakt) {
      String data = new String(((GWikiBinaryAttachmentArtefakt) fact).getCompiledObject());
      append(data);
    } else {
      throw new RuntimeException("Unsupported gwiki artefakt type to include: " + fact.getClass().getName());
    }
  }

  /**
   * Global url.
   *
   * @param lurl the lurl
   * @return the string
   */
  public String globalUrl(String lurl)
  {
    if (wikiWeb.findElement(lurl) == null) {
      if (lurl.startsWith("/") == false) {
        GWikiElement wikiElement = getCurrentElement();
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

  /**
   * Local url.
   *
   * @param lurl the lurl
   * @return the string
   */
  public String localUrl(String lurl)
  {
    String url = lurl;
    if (url.startsWith("/") == false) {
      url = "/" + url;
    }

    String res = url;
    if (lurl.indexOf("/") == -1 && wikiWeb.findElementInfo(res) == null) {
      GWikiElement wikiElement = getCurrentElement();
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

  /**
   * Render local url.
   *
   * @param target the target
   * @return the string
   */
  public String renderLocalUrl(String target)
  {
    return renderLocalUrl(target, null, null);
  }

  /**
   * Gen new page link.
   *
   * @param target the target
   * @param title the title
   * @param addArgs the add args
   * @return the string
   */
  public String genNewPageLink(String target, String title, String addArgs)
  {
    StringBuilder sb = new StringBuilder();
    title = getTranslatedProp(title);
    boolean allow = wikiWeb.getAuthorization().isAllowToCreate(this, getCurrentElement().getElementInfo());
    if (allow == false) {

      sb.append("<i>").append(WebUtils.escapeHtml(title)).append("</i>");
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
          .append("'>").append(WebUtils.escapeHtml(title)).append("</a>");
    }
    return sb.toString();
  }

  /**
   * escape html.
   *
   * @param text the text
   * @return the string
   */
  public String escape(String text)
  {
    return WebUtils.escapeHtml(text);
  }

  /**
   * Encodes parameter in UTF-8.
   *
   * @param text the text
   * @return the string
   */
  public String escapeUrlParam(String text)
  {
    try {
      return URLDecoder.decode(text, CharEncoding.UTF_8);
    } catch (UnsupportedEncodingException e) {
      // should never b happen
      throw new RuntimeException(e);
    }
  }

  /**
   * Render existing link.
   *
   * @param ei the ei
   * @param title the title
   * @param addArgs has to be escaped by itself
   * @return the string
   */
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
      sb.append("<i>").append(WebUtils.escapeHtml(title)).append("</i>");
    } else {
      String url = localUrl("/" + ei.getId());
      if (addArgs != null) {
        url += addArgs;
      }
      sb.append("<a href='").append(url).append("'>").append(WebUtils.escapeHtml(title)).append("</a>");
    }
    return sb.toString();
  }

  /**
   * Render existing link with attr.
   *
   * @param ei the ei
   * @param title the title
   * @param addArgs the add args
   * @param attr note caller has to escape the paramers
   * @return the string
   */
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
      sb.append("<i>").append(WebUtils.escapeHtml(title)).append("</i>");
    } else {
      String url = localUrl("/" + ei.getId());
      if (addArgs != null) {
        url += addArgs;
      }
      sb.append("<a href='").append(url).append("'");
      for (int i = 0; i < attr.length; i += 2) {
        sb.append(" ").append(attr[i]).append("='").append(attr[i + 1]).append("' ");
      }
      sb.append(">").append(WebUtils.escapeHtml(title)).append("</a>");
    }
    return sb.toString();
  }

  /**
   * Checks if is allow to.
   *
   * @param right the right
   * @return true, if is allow to
   */
  public boolean isAllowTo(String right)
  {
    return wikiWeb.getAuthorization().isAllowTo(this, right);
  }

  /**
   * Ensure allow to.
   *
   * @param right the right
   */
  public void ensureAllowTo(String right)
  {
    wikiWeb.getAuthorization().ensureAllowTo(this, right);
  }

  /**
   * Render local url.
   *
   * @param target the target
   * @param title the title
   * @param addArgs must be already escaped.
   * @return the string
   */
  public String renderLocalUrl(String target, String title, String addArgs)
  {
    GWikiElementInfo ei = wikiWeb.findElementInfo(target);
    if (ei == null) {
      return genNewPageLink(target, title, addArgs);
    } else {
      return renderExistingLink(ei, title, addArgs);
    }
  }

  /**
   * Gets the translated.
   *
   * @param key the key
   * @return the translated
   */
  public String getTranslated(String key)
  {
    return wikiWeb.getI18nProvider().translate(this, key);
  }

  /**
   * Gets the translated prop.
   *
   * @param key the key
   * @return the translated prop
   */
  public String getTranslatedProp(String key)
  {
    return wikiWeb.getI18nProvider().translateProp(this, key);
  }

  public String translate(String key, Object... args)
  {
    return getWikiWeb().getI18nProvider().translate(this, key, null, args);
  }

  /**
   * Gets the user date string.
   *
   * @param date the date
   * @return the user date string
   */
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

  /**
   * Parses the user date string.
   *
   * @param ds the ds
   * @return the date
   */
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

  /**
   * Gets the user string prop.
   *
   * @param key the key
   * @return the user string prop
   */
  public String getUserStringProp(String key)
  {
    return getUserStringProp(key, "");
  }

  /**
   * Gets the user string prop.
   *
   * @param key the key
   * @param defaultValue the default value
   * @return the user string prop
   */
  public String getUserStringProp(String key, String defaultValue)
  {
    return StringUtils.defaultString(getWikiWeb().getAuthorization().getUserProp(this, key), defaultValue);
  }

  /**
   * Gets the user boolean prop.
   *
   * @param key the key
   * @return the user boolean prop
   */
  public boolean getUserBooleanProp(String key)
  {
    return getUserBooleanProp(key, false);
  }

  /**
   * Gets the user boolean prop.
   *
   * @param key the key
   * @param defaultValue the default value
   * @return the user boolean prop
   */
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
  @Override
  public GWikiContext append(String text)
  {
    if (text == null) {
      return this;
    }
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

  /**
   * Flush.
   *
   * @return the g wiki context
   */
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

  /**
   * Append esc text.
   *
   * @param text the text
   * @return the g wiki context
   */
  public GWikiContext appendEscText(String text)
  {
    return append(WebUtils.escapeHtml(text));
  }

  @Override
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

  /**
   * Run element.
   *
   * @param <T> the generic type
   * @param el the el
   * @param cb the cb
   * @return the t
   */
  public <T> T runElement(GWikiElement el, CallableX<T, RuntimeException> cb)
  {
    try {
      pushWikiElement(el);
      return cb.call();
    } finally {
      popWikiElement();
    }
  }

  /**
   * Run with parts.
   *
   * @param newParts the new parts
   * @param cb the cb
   * @return true, if successful
   */
  public boolean runWithParts(Map<String, GWikiArtefakt<?>> newParts, CallableX<Boolean, RuntimeException> cb)
  {
    Map<String, GWikiArtefakt<?>> pushParts = null;
    if (newParts.size() > 0) {
      for (Map.Entry<String, GWikiArtefakt<?>> npe : newParts.entrySet()) {
        GWikiArtefakt<?> backup = parts.put(npe.getKey(), npe.getValue());
        if (backup != null) {
          if (pushParts == null) {
            pushParts = new HashMap<String, GWikiArtefakt<?>>();
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

  /**
   * Run with artefakt.
   *
   * @param artefakt the artefakt
   * @param cb the cb
   * @return true, if successful
   */
  public boolean runWithArtefakt(GWikiArtefakt<?> artefakt, CallableX<Boolean, RuntimeException> cb)
  {
    GWikiArtefakt<?> oa = getCurrentPart();
    try {
      setCurrentPart(artefakt);
      return cb.call();
    } finally {
      setCurrentPart(oa);
    }
  }

  /**
   * runs the callback code inside specified tenant.
   *
   * @param <R> the generic type
   * @param <E> the element type
   * @param tenantId the tenant id
   * @param wikiSelector the wiki selector
   * @param callBack the call back
   * @return the r
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

  /**
   * Push wiki element.
   *
   * @param wikiElement the wiki element
   */
  public void pushWikiElement(GWikiElement wikiElement)
  {
    this.wikiElements.push(wikiElement);
  }

  /**
   * Pop wiki element.
   *
   * @return the g wiki element
   */
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

  /**
   * Gets the request parameter.
   *
   * @param key the key
   * @return the request parameter
   */
  public String getRequestParameter(String key)
  {
    return request.getParameter(key);
  }

  /**
   * Gets the request values.
   *
   * @param key the key
   * @return the request values
   */
  public String[] getRequestValues(String key)
  {
    return request.getParameterValues(key);
  }

  /**
   * Sets the request attribute.
   *
   * @param key the key
   * @param value the value
   */
  public void setRequestAttribute(String key, Object value)
  {
    if (value == null) {
      request.removeAttribute(key);
    } else {
      request.setAttribute(key, value);
    }
  }

  /**
   * Gets the file item.
   *
   * @param key the key
   * @return the file item
   */
  public FileItem getFileItem(String key)
  {
    if ((request instanceof CommonMultipartRequest) == false) {
      return null;
    }

    CommonMultipartRequest qr = (CommonMultipartRequest) request;
    return qr.getFileItems().get(key);
  }

  /**
   * Gets the request attribute.
   *
   * @param key the key
   * @return the request attribute
   */
  public Object getRequestAttribute(String key)
  {
    return request.getAttribute(key);
  }

  /**
   * Gets the boolean request attribute.
   *
   * @param key the key
   * @return the boolean request attribute
   */
  public boolean getBooleanRequestAttribute(String key)
  {
    return request.getAttribute(key) == Boolean.TRUE;

  }

  /**
   * Gets the string list request attribute.
   *
   * @param key the key
   * @return the string list request attribute
   */
  @SuppressWarnings("unchecked")
  public List<String> getStringListRequestAttribute(String key)
  {
    Object obj = request.getAttribute(key);
    if ((obj instanceof List) == true) {
      return (List<String>) obj;
    }
    List<String> list = new ArrayList<String>();
    request.setAttribute(key, list);
    return list;

  }

  /**
   * Gets the session attribute.
   *
   * @param key the key
   * @return the session attribute
   */
  public Object getSessionAttribute(String key)
  {
    return wikiWeb.getSessionProvider().getSessionAttribute(this, key);
  }

  /**
   * Sets the session attribute.
   *
   * @param key the key
   * @param value the value
   */
  public void setSessionAttribute(String key, Object value)
  {
    if (value != null && (value instanceof Serializable) == false) {
      throw new RuntimeException(
          "Session object with key is not serializable: " + key + "; " + value.getClass().getName());
    }
    wikiWeb.getSessionProvider().setSessionAttribute(this, key, (Serializable) value);
  }

  /**
   * Removes the session attribute.
   *
   * @param key the key
   */
  public void removeSessionAttribute(String key)
  {
    wikiWeb.getSessionProvider().removeSessionAttribute(this, key);
  }

  public void setCharacterEncoding(String enc) throws UnsupportedEncodingException
  {
    request.setCharacterEncoding(enc);
  }

  /**
   * Send error silent.
   *
   * @param errorCode the error code
   */
  public void sendErrorSilent(int errorCode)
  {
    try {
      sendError(errorCode);
    } catch (IOException ex) {
      // nothing
    }
  }

  /**
   * Send error.
   *
   * @param errorCode the error code
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void sendError(int errorCode) throws IOException
  {
    response.sendError(errorCode);
  }

  public void setResponseStatus(int status)
  {
    response.setStatus(status);
  }

  /**
   * Send error.
   *
   * @param errorCode the error code
   * @param errorMessage the error message
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void sendError(int errorCode, String errorMessage) throws IOException
  {
    response.sendError(errorCode, errorMessage);
  }

  public ServletOutputStream getResponseOutputStream() throws IOException
  {
    return response.getOutputStream();
  }

  /**
   * Creates the session.
   */
  public void createSession()
  {
    getSession(true);
  }

  public HttpSession getCreateSession()
  {
    return getSession(true);
  }

  /**
   * Gets the session.
   *
   * @param create the create
   * @return the session
   */
  public HttpSession getSession(boolean create)
  {
    if (create == false && request == null) {
      return null;
    }

    Validate.notNull(request, "request not set");
    if (create == true) {
      System.out.println("createsession");
    }
    return request.getSession(create);
  }

  /**
   * set a cookie.
   *
   * @param key the key
   * @param value the value
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

  /**
   * Clear cookie.
   *
   * @param key the key
   */
  public void clearCookie(String key)
  {
    Cookie tsc = new Cookie(key, "");
    tsc.setPath(getWikiWeb().getContextPath());
    // tsc.setSecure(true);
    tsc.setMaxAge(0);
    response.addCookie(tsc);
  }

  /**
   * get a named cookie.
   *
   * @param key the key
   * @return by default null
   */
  public String getCookie(String key)
  {
    return getCookie(key, null);
  }

  /**
   * Gets the cookie.
   *
   * @param key the key
   * @param defaultValue the default value
   * @return the cookie
   */
  @SuppressWarnings("deprecation")
  public String getCookie(String key, String defaultValue)
  {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return defaultValue;
    }
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

  /**
   * Skin include.
   *
   * @param name the name
   */
  public void skinInclude(String name)
  {
    String skin = getSkin();
    String id = "inc/" + skin + "/" + name;
    include(id);
  }

  /**
   * Adds the cookie.
   *
   * @param cookie the cookie
   */
  public void addCookie(Cookie cookie)
  {
    response.addCookie(cookie);
  }

  public String getUserAgent()
  {
    return request.getHeader("user-agent");
  }

  /**
   * Adds the header content.
   *
   * @param content the content
   */
  public void addHeaderContent(String content)
  {
    Object obj = request.getAttribute(HEADER_STATEMENTS);
    if (obj == null) {
      request.setAttribute(HEADER_STATEMENTS, content);
    } else {
      request.setAttribute(HEADER_STATEMENTS, content + obj);
    }
  }

  public String getHeaderContent()
  {
    Object o = request.getAttribute(HEADER_STATEMENTS);
    if (o == null) {
      return "";
    }
    return ObjectUtils.toString(o);
  }

  /**
   * Adds the content css.
   *
   * @param localPath the local path
   */
  public void addContentCss(String localPath)
  {
    getStringListRequestAttribute(CONTENT_CSS).add(localPath);
  }

  /**
   * Adds the content js.
   *
   * @param localPath the local path
   */
  public void addContentJs(String localPath)
  {
    getStringListRequestAttribute(CONTENT_JS).add(localPath);
  }

  public List<String> getContentCsse()
  {
    return getStringListRequestAttribute(CONTENT_CSS);
  }

  public List<String> getContentJs()
  {
    return getStringListRequestAttribute(CONTENT_JS);
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

  public Map<String, GWikiArtefakt<?>> getParts()
  {
    return parts;
  }

  public void setParts(Map<String, GWikiArtefakt<?>> parts)
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

  public GWikiArtefakt<?> getCurrentPart()
  {
    return currentPart;
  }

  public void setCurrentPart(GWikiArtefakt<?> currentPart)
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

  public ActionBean getActionBean()
  {
    return actionBean;
  }

  public void setActionBean(ActionBean actionBean)
  {
    this.actionBean = actionBean;
  }

}
