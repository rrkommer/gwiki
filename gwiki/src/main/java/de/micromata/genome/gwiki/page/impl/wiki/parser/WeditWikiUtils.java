package de.micromata.genome.gwiki.page.impl.wiki.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.micromata.genome.gwiki.model.GWikiGlobalConfig;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRte;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.rte.RteHtmlParser;
import de.micromata.genome.gwiki.utils.html.Html2WikiTransformInfo;
import de.micromata.genome.gwiki.utils.html.Html2WikiTransformer;

/**
 * Utilities to convert from edit control to/from wiki
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class WeditWikiUtils
{
  private static final Logger LOG = Logger.getLogger(WeditWikiUtils.class);

  public static String weditToWiki(String text)
  {
    String ret = StringUtils.defaultString(text);
    ret = StringUtils.replace(ret, "<br/>", "");
    ret = StringUtils.replace(ret, "<br>", "");
    ret = StringUtils.replace(ret, "</br>", "");
    ret = StringUtils.replace(ret, "&nbsp;", " ");

    //    ret = StringUtils.replace(ret, "\n", "");
    //    ret = StringUtils.replace(ret, "\r", "");
    //    ret = StringUtils.replace(ret, "</p>", "\n");
    //    ret = StringUtils.replace(ret, "<p>", "");
    ret = StringUtils.replace(ret, "&lt;", "<");
    ret = StringUtils.replace(ret, "&gt;", ">");
    LOG.debug("weditToWiki\nwedit: " + text + "\n\nwiki: " + ret);
    return ret;
  }

  public static String wikiToWedit(String text)
  {
    String ret = StringUtils.defaultString(text);
    ret = StringEscapeUtils.escapeXml(ret);

    //    ret = StringUtils.replace(ret, "\n", "<br/>\n");
    //    ret = "<p>" + ret + "</p>";
    return ret;
  }

  public static Set<String> getHtmlTagMacros(GWikiContext wikiContext)
  {
    Set<String> s = new HashSet<String>();
    GWikiGlobalConfig wikiConfig = wikiContext.getWikiWeb().getWikiConfig();
    Map<String, GWikiMacroFactory> macros = wikiConfig.getWikiMacros(wikiContext);
    for (Map.Entry<String, GWikiMacroFactory> me : macros.entrySet()) {
      if ((me.getValue() instanceof GWikiMacroClassFactory) == false) {
        continue;
      }
      GWikiMacroClassFactory cf = (GWikiMacroClassFactory) me.getValue();
      Class<?> cls = cf.getClazz();
      if (cls == null) {
        continue;
      }
      if (GWikiHtmlBodyTagMacro.class.isAssignableFrom(cls) || GWikiHtmlTagMacro.class.isAssignableFrom(cls)) {
        s.add(me.getKey());
      }
    }
    return s;
  }

  public static List<Html2WikiTransformer> collectHtml2WikiTransformers(GWikiContext wikiContext)
  {
    List<Html2WikiTransformer> transformers = new ArrayList<Html2WikiTransformer>();
    GWikiGlobalConfig wikiConfig = wikiContext.getWikiWeb().getWikiConfig();
    Map<String, GWikiMacroFactory> macros = wikiConfig.getWikiMacros(wikiContext);
    for (Map.Entry<String, GWikiMacroFactory> me : macros.entrySet()) {
      if (me.getValue().isRteMacro() == false) {
        continue;
      }
      GWikiMacroRte rteMacro = (GWikiMacroRte) me.getValue().createInstance();
      Html2WikiTransformInfo ti = rteMacro.getTransformInfo();
      if (ti != null) {
        transformers.add(ti);
      }
    }
    return transformers;
  }

  public static String wikiToRte(GWikiContext wikiContext, String wikiText)
  {
    GWikiWikiPageArtefakt wiki = new GWikiWikiPageArtefakt();

    wiki.setStorageData(wikiText);
    GWikiStandaloneContext nctx = new GWikiStandaloneContext(wikiContext);
    nctx.pushWikiElement(wikiContext.getCurrentElement());
    nctx.setCurrentPart(wiki);
    nctx.setCurrentPart(wikiContext.getCurrentPart());
    nctx.setRenderMode(RenderModes.combine(RenderModes.ForRichTextEdit));
    wiki.compileFragements(nctx);
    //    nctx.append("<div class=\"gwikiContent\">");
    wiki.render(nctx);
    //    nctx.append("</div>\n");
    nctx.flush();
    String ret = nctx.getOutString();
    LOG.debug("WikiToRte:\n" + wikiText + "\nRte:\n" + ret);
    return ret;
  }

  public static String rteToWiki(GWikiContext wikiContext, String htmlCode)
  {
    String ttext = RteHtmlParser.convert(wikiContext, htmlCode);
    return ttext;
  }
}
