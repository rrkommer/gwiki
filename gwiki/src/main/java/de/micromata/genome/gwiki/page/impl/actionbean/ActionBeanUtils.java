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

package de.micromata.genome.gwiki.page.impl.actionbean;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.ClassUtils;
import de.micromata.genome.util.runtime.RuntimeIOException;

public class ActionBeanUtils
{
  /**
   * 
   * @param bean
   * @return true if forward/continue
   */
  public static boolean perform(ActionBean bean)
  {
    CommonMutipartRequestHandler.handleMultiPartRequest(bean.getWikiContext());
    setStrutsFormBean(bean);
    fillForm(bean, bean.getWikiContext());
    return callMethod(bean, bean.getWikiContext());
  }

  public static Map getPrivateMap(ActionBean bean, Map<String, Object> reqMap)
  {
    String prefix = bean.getRequestPrefix();
    if (StringUtils.isEmpty(prefix) == true) {
      return reqMap;
    }
    Map<String, Object> nm = new HashMap<String, Object>();
    for (Map.Entry<String, Object> me : reqMap.entrySet()) {
      String k = me.getKey();
      if (k.startsWith(prefix) == true) {
        k = k.substring(prefix.length());
        nm.put(k, me.getValue());
      }
    }
    return nm;
  }

  @SuppressWarnings("unchecked")
  public static void fillForm(ActionBean bean, GWikiContext pctx)
  {
    try {
      Map<String, Object> pm = pctx.getRequest().getParameterMap();
      pm = getPrivateMap(bean, pm);
      ClassUtils.populateBeanWithPuplicMembers(bean, pm);
      //      
      // } else {
      // BeanUtilsBean.getInstance().populate(bean, pm);
      // }

    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static void setValErrors(ActionBean bean, GWikiContext ctx)
  {
    ctx.getRequest().setAttribute(GWikiErrorsTag.ERRORS_TAG_REQUEST_ATTRIBUTE, ctx.getValidationErrors());
  }

  private static Object dispatchToMethod(ActionBean bean, String methodName, GWikiContext ctx)
  {
    Object ret = dispatchToMethodImpl(bean, methodName, ctx);
    setValErrors(bean, ctx);
    return ret;
  }

  private static Method findMethod(ActionBean bean, String methodName)
  {
    for (Method m : bean.getClass().getMethods()) {
      if (m.getName().equals(methodName) == true) {
        return m;
      }
    }
    return null;

  }

  private static Object dispatchToMethodImpl(ActionBean bean, String methodName, GWikiContext ctx)
  {
    if (methodName.startsWith("on") == false) {
      throw new IllegalArgumentException("Invalid method specified " + methodName);
    }
    Method method;
    try {
      method = findMethod(bean, methodName);
      if (method == null) {
        method = findMethod(bean, "onUnbound");
      }
      if (method == null) {
        throw new RuntimeException("Cannot find method " + methodName + " in class " + bean.getClass().getName());
      }
    } catch (SecurityException ex) {
      throw new RuntimeException("Cannot find accessable method " + methodName + " in class " + bean.getClass().getName(), ex);
    }
    /*
     * if (method == null) throw new RuntimeException("Cannot find method " + methodName + " in class " + getClass().getName());
     */

    Object args[] = {};
    Object o;
    try {
      o = method.invoke(bean, args);
    } catch (IllegalArgumentException ex) {
      // TODO Logging
      // /**
      // * @logging
      // * @reason Allgemeiner Fehler bei der Verarbeitung
      // * @action Entwickler kontaktieren
      // */
      // throw new LoggedRuntimeException(ex, LogLevel.Error, Category.RequestProcessing, "Unbehandelte IllegalArgumentException",
      // new LogExceptionAttribute(ex));
      throw new RuntimeException(ex);
    } catch (IllegalAccessException ex) {
      // /**
      // * @logging
      // * @reason Allgemeiner Fehler bei der Verarbeitung
      // * @action Entwickler kontaktieren
      // */
      // throw new LoggedRuntimeException(ex, LogLevel.Error, Category.RequestProcessing, "Unbehandelte IllegalAccessException in GAction",
      // new LogExceptionAttribute(ex));
      throw new RuntimeException(ex);
    } catch (InvocationTargetException ex) {
      Throwable ca = ex.getCause();
      if (ca == null) {
        throw new RuntimeException(ex);
      }
      if (ca instanceof RuntimeException) {
        throw (RuntimeException) ca;
      }
      throw new RuntimeException(ca);
      // Throwable thrEx = ex.getTargetException();
      // if (thrEx instanceof LoggedRuntimeException) {
      // LoggedRuntimeException rtex = (LoggedRuntimeException) thrEx;
      // throw rtex;
      // }
      // if (thrEx instanceof SessionLostException)
      // throw (SessionLostException) thrEx;
      //
      // /**
      // * @logging
      // * @reason Allgemeiner Fehler bei der Verarbeitung
      // * @action Entwickler kontaktieren
      // */
      // throw new LoggedRuntimeException(thrEx, LogLevel.Error, Category.RequestProcessing, "Unbehandelte Exception in GAction 1");
      // throw new RuntimeException(ex);
    }
    return o;
    // if (checkErrors(ctx) == true)
    // return createForward(null, ctx);
    // return createForward(o, ctx);
  }

  public static void redirect(GWikiContext ctx, String pageId)
  {
    try {
      if (pageId.startsWith("//") == true) {
        pageId = pageId.substring(1);
        ctx.getResponse().sendRedirect(pageId);
      } else {
        if (pageId.startsWith("/") == true) {
          pageId = pageId.substring(1);
        }
        ctx.getResponse().sendRedirect(ctx.localUrl(pageId));
      }
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  protected static void createForward(ActionBean bean, GWikiContext pctx, String url, boolean redirect)
  {
    // String url = afw.getPath();
    try {
      if (redirect == true) {
        redirect(pctx, url);
      } else {
        RequestDispatcher rd = pctx.getCreatePageContext().getServletContext().getRequestDispatcher(pctx.localUrl(url));
        if (rd == null) {
        }
        rd.forward(pctx.getRequest(), pctx.getResponse());
      }
    } catch (ServletException ex) {
      throw new RuntimeException(ex);
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  @SuppressWarnings("unchecked")
  public static boolean callMethod(ActionBean bean, GWikiContext pctx)
  {
    ;
    String methPrefix = bean.getRequestPrefix() + "method_";
    Object ret = null;
    boolean found = false;
    for (String name : (Set<String>) pctx.getRequest().getParameterMap().keySet()) {
      if (name.startsWith(methPrefix) == true) {
        found = true;
        ret = dispatchToMethod(bean, name.substring(methPrefix.length()), pctx);
        break;
      }
    }
    if (found == false) {
      ret = bean.onInit();
      setValErrors(bean, pctx);
    }
    if (ret != null) {
      if (ret == NoForward.class) {
        return false;
      } else if (ret instanceof String) {
        createForward(bean, pctx, (String) ret, true);
      } else if (ret instanceof GWikiElementInfo) {
        createWikiRedirect(bean, pctx, (GWikiElementInfo) ret);
      } else if (ret instanceof GWikiElement) {
        createWikiRedirect(bean, pctx, (GWikiElement) ret);
      } else if (ret instanceof Boolean) {
        if (ret == Boolean.FALSE) {
          return false;
        }
      } else {
        throw new RuntimeException("GAction: unknown forward type");
      }
    }
    return ret == null;
  }

  public static void createWikiRedirect(ActionBean bean, GWikiContext pctx, GWikiElement el)
  {
    createWikiRedirect(bean, pctx, el.getElementInfo());
  }

  public static void createWikiRedirect(ActionBean bean, GWikiContext pctx, GWikiElementInfo ei)
  {
    String url = "/" + ei.getId();
    try {
      pctx.getResponse().sendRedirect(pctx.localUrl(url));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static void setStrutsFormBean(ActionBean bean)
  {
    bean.getWikiContext().getRequest().setAttribute("org.apache.struts.taglib.html.BEAN", bean);
    bean.getWikiContext().getRequest().setAttribute("form", bean);
  }

}
