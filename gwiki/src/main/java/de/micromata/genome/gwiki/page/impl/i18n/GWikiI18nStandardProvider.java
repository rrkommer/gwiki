/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   25.11.2009
// Copyright Micromata 25.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.i18n;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiI18nProvider;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiI18nElement;
import de.micromata.genome.util.text.PlaceHolderReplacer;
import de.micromata.genome.util.text.StringResolver;

public class GWikiI18nStandardProvider implements GWikiI18nProvider
{
  protected boolean containsContext(GWikiContext ctx, String pageId)
  {
    if (ctx.getI18nMaps() == null) {
      return false;
    }
    for (GWikiI18nElement m : ctx.getI18nMaps()) {
      if (m.getElementInfo().getId().equals(pageId) == true) {
        return true;
      }
    }
    return false;
  }

  public void addTranslationElement(GWikiContext ctx, String pageId)
  {
    if (containsContext(ctx, pageId) == true) {
      return;
    }
    GWikiI18nElement el = (GWikiI18nElement) ctx.getWikiWeb().getElement(pageId);
    if (ctx.getI18nMaps() == null) {
      ctx.setI18nMaps(new ArrayList<GWikiI18nElement>());
    }
    ctx.getI18nMaps().add(el);
  }

  protected String getI18nValue(GWikiContext ctx, String key, String defaultValue)
  {
    // TODO check user web locale
    String lang = ctx.getWikiWeb().getAuthorization().getUserProp(ctx, "lang");
    if (StringUtils.isEmpty(lang) == true) {
      lang = "en";
    }
    if (ctx.getI18nMaps() == null) {
      return defaultValue;
    }
    for (GWikiI18nElement m : ctx.getI18nMaps()) {
      String v = m.getMessage(lang, key);
      if (v != null) {
        return v;
      }
    }
    return defaultValue;
  }

  public String translate(GWikiContext ctx, String key)
  {
    return translate(ctx, key, "");
  }

  public String translate(GWikiContext ctx, String key, String defaultValue)
  {
    return getI18nValue(ctx, key, defaultValue);
  }

  public String translate(GWikiContext ctx, String key, String defaultValue, Object... args)
  {
    String value = getI18nValue(ctx, key, defaultValue);
    return MessageFormat.format(value, args);
  }

  public String translateProp(final GWikiContext ctx, String key)
  {
    if (key == null) {
      return key;
    }
    String ret = PlaceHolderReplacer.resolveReplace(key, "I{", "}", new StringResolver() {

      public String resolve(String placeholder)
      {
        return translate(ctx, placeholder);
      }
    });
    return ret;
  }
}
