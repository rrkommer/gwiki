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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * Base class to implement html macros.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiHtmlTagMacro extends GWikiMacroBean
{

  private static final long serialVersionUID = 2630664358437072168L;

  public static Set<String> restrictedTags = new HashSet<String>();

  static {
    restrictedTags.add("script");
    restrictedTags.add("frame");
    restrictedTags.add("body");
    restrictedTags.add("html");
    restrictedTags.add("iframe");
  }

  public GWikiHtmlTagMacro()
  {
  }

  public static GWikiMacroFactory getFactory()
  {
    return new GWikiMacroClassFactory(GWikiHtmlTagMacro.class);
  }

  public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException
  {
    if (ctx.isAllowTo(GWikiAuthorizationRights.GWIKI_EDITHTML.name()) == true) {
      return;
    }
    if (restrictedTags.contains(attrs.getCmd()) == true) {
      throw new AuthorizationFailedException("Usage of tag " + attrs.getCmd() + " is not allowed");
    }
    Map<String, String> args = attrs.getArgs().getMap();
    for (Map.Entry<String, String> me : args.entrySet()) {
      String k = me.getKey().toLowerCase();
      if (k.startsWith("on") == true) {
        throw new AuthorizationFailedException("Java Script methods in Macros are not allowed");
      }
      if (k.equals("href") == true || k.equals("src") == true) {
        String v = StringUtils.trim(me.getValue().toLowerCase());
        if (v.startsWith("javascript:") == true) {
          throw new AuthorizationFailedException("Java Script methods in Macros are not allowed");
        }
      }
    }
  }

  private static String esc(String t)
  {
    return StringEscapeUtils.escapeHtml(t);
  }

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {

    ctx.append("<", attrs.getCmd());
    boolean isFirst = true;
    for (Map.Entry<String, String> me : attrs.getArgs().getMap().entrySet()) {
      if (isFirst == false) {
        isFirst = true;
      } else {
        ctx.append(" ");
      }
      ctx.append(esc(me.getKey())).append("=\"").append(esc(me.getValue())).append("\"");
    }
    ctx.append(">");
    if (attrs.getChildFragment() != null) {
      // if (preview == true) {
      // attrs.getChildFragment().renderPreview(ctx, sb);
      // } else {
      attrs.getChildFragment().render(ctx);
      // }
    } else if (attrs.getBody() != null) {
      ctx.append(StringEscapeUtils.escapeHtml(attrs.getBody()));
    }
    ctx.append("</").append(attrs.getCmd()).append(">");
    return true;
  }
}
