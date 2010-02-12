/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   03.11.2009
// Copyright Micromata 03.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.auth;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Implementation of common methods of GWikiAuthorization.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiAuthorizationBase implements GWikiAuthorization, GWikiPropKeys
{
  private boolean generalPublicEdit = false;

  private boolean generalPublicView = false;

  public void ensureAllowTo(GWikiContext ctx, String right, GWikiElementInfo el)
  {
    if (isAllowTo(ctx, right) == true) {
      return;
    }
    AuthorizationFailedException.failRight(ctx, right, el);
  }

  public void ensureAllowTo(GWikiContext ctx, String right)
  {
    if (isAllowTo(ctx, right) == true)
      return;
    AuthorizationFailedException.failRight(ctx, right);
  }

  // TODO gwiki noch nicht richtig
  public boolean isAllowToCreate(GWikiContext ctx, GWikiElementInfo ei)
  {
    if (generalPublicEdit == true)
      return true;
    if (isAllowToEdit(ctx, ei) == false)
      return false;

    if (ei.getMetaTemplate() != null && ei.getMetaTemplate().getRequiredEditRight() != null) {
      if (isAllowTo(ctx, ei.getMetaTemplate().getRequiredEditRight()) == false) {
        return false;
      }
    }
    if (isAllowTo(ctx, GWikiAuthorizationRights.GWIKI_CREATEPAGES.name()) == false) {
      return false;
    }
    // TODO gwiki check if parent has right to edit.
    return true;
  }

  public boolean isAllowToEdit(GWikiContext ctx, GWikiElementInfo ei)
  {
    if (generalPublicEdit == true) {
      return true;
    }
    Boolean cp = checkPageSpecificRight(ctx, AUTH_EDIT, ei);
    if (cp != null) {
      return cp;
    }

    boolean hasEditRight = isAllowTo(ctx, GWikiAuthorizationRights.GWIKI_EDITPAGES.name());
    if (hasEditRight == false) {
      return false;
    }
    if (ei.getMetaTemplate() != null && ei.getMetaTemplate().getRequiredEditRight() != null) {
      if (isAllowTo(ctx, ei.getMetaTemplate().getRequiredEditRight()) == false) {
        return false;
      }
    }
    return true;
  }

  protected String getMetaTemplateRight(GWikiContext ctx, GWikiElementInfo ei, String pageRight)
  {
    if (ei.getMetaTemplate() == null) {
      return null;
    }
    if (GWikiPropKeys.AUTH_VIEW.equals(pageRight) == true) {
      return ei.getMetaTemplate().getRequiredViewRight();
    } else if (GWikiPropKeys.AUTH_EDIT.equals(pageRight) == true) {
      return ei.getMetaTemplate().getRequiredEditRight();
    } else {
      return null;
    }
  }

  public String getEffectiveRight(GWikiContext ctx, GWikiElementInfo ei, String pageRight)
  {
    if (generalPublicView == true) {
      return GWikiAuthorizationRights.GWIKI_PUBLIC.name();
    }
    String r = ei.getProps().getStringValue(pageRight);
    if (StringUtils.isNotEmpty(r) == true) {
      return r;
    }
    r = getMetaTemplateRight(ctx, ei, pageRight);
    if (StringUtils.isNotEmpty(r) == true) {
      return r;
    }
    GWikiElementInfo pi = ei.getParent(ctx);
    if (pi != null) {
      return getEffectiveRight(ctx, pi, pageRight);
    }
    if (GWikiPropKeys.AUTH_VIEW.equals(pageRight) == true) {
      return GWikiAuthorizationRights.GWIKI_VIEWPAGES.name();
    } else if (GWikiPropKeys.AUTH_EDIT.equals(pageRight) == true) {
      return GWikiAuthorizationRights.GWIKI_EDITPAGES.name();
    } else {
      return "";
    }

  }

  public boolean isAllowToView(GWikiContext ctx, GWikiElementInfo ei)
  {
    if (generalPublicView == true) {
      return true;
    }

    if (ei.isViewable() == false) {
      return false;
    }
    Boolean pc = checkPageSpecificRight(ctx, AUTH_VIEW, ei);
    if (pc != null) {
      return pc;
    }
    if (isAllowTo(ctx, GWikiAuthorizationRights.GWIKI_VIEWPAGES.name()) == false) {
      return false;
    }

    if (ei.getMetaTemplate() != null && ei.getMetaTemplate().getRequiredViewRight() != null) {
      if (isAllowTo(ctx, ei.getMetaTemplate().getRequiredViewRight()) == false) {
        return false;
      }
    }
    return true;
  }

  protected Boolean checkPageSpecificRight(GWikiContext ctx, String propKey, GWikiElementInfo ei)
  {
    String r = ei.getProps().getStringValue(propKey);
    if (StringUtils.isEmpty(r) == true) {
      if (ei.getParentId() != null) {
        GWikiElementInfo pei = ctx.getWikiWeb().findElementInfo(ei.getParentId());
        if (pei != null && pei != ei) {
          return checkPageSpecificRight(ctx, propKey, pei);
        }
      }
      return null;
    }
    if (r.equals(GWikiAuthorizationRights.GWIKI_PRIVATE.name()) == true) {
      if (isAllowTo(ctx, GWikiAuthorizationRights.GWIKI_EDITPAGES.name()) == true) {
        return true;
      }
      if (StringUtils.equals(getCurrentUserName(ctx), ei.getCreatedBy()) == true) {
        return true;
      }
      return false;
    } else if (r.equals(GWikiAuthorizationRights.GWIKI_PUBLIC.name()) == true) {
      return true;
    }
    return isAllowTo(ctx, r);
  }

  public Locale getLocaleByLang(String lang)
  {
    if (StringUtils.isEmpty(lang) == true) {
      return null;
    }
    if (lang.equals("en") == true || lang.equals("eng") == true) {
      return Locale.ENGLISH;
    } else if (lang.equals("de") == true || lang.equals("deu") == true) {
      return Locale.GERMAN;
    }
    return new Locale(lang);
  }

  public Locale getCurrentUserLocale(GWikiContext ctx)
  {
    String lang = getUserProp(ctx, USER_LANG);
    if (StringUtils.isBlank(lang) == false) {
      Locale loc = getLocaleByLang(lang);
      if (loc != null) {
        return loc;
      }
    }
    return ctx.getRequest().getLocale();
  }

  protected void clearSession(GWikiContext wikiContext)
  {
    wikiContext.getWikiWeb().getSessionProvider().clearSessionAttributes(wikiContext);
  }

  public boolean runIfAuthentificated(GWikiContext wikiContext, CallableX<Void, RuntimeException> callback)
  {
    if (initThread(wikiContext) == true) {
      return false;
    }
    try {
      callback.call();
    } finally {
      clearThread(wikiContext);
    }
    return true;
  }
}
