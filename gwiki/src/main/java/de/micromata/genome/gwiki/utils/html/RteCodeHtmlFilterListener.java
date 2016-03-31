package de.micromata.genome.gwiki.utils.html;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiCodeMacro;

public class RteCodeHtmlFilterListener implements HtmlFilterListener
{

  @Override
  public boolean startElement(HtmlEvent event)
  {
    if (event.el.equals("pre") == false || event.containsInStyleClass("language-") == false) {
      return true;
    }
    event.flushText();
    MacroAttributes attr = new MacroAttributes();
    attr.setCmd("code");
    String clsa = event.getClassAttr();
    if (clsa != null && clsa.indexOf("language-") != -1) {
      String lang = StringUtils.substringAfter(clsa, "language-");
      attr.getArgs().setStringValue("lang", lang);
    }
    StringBuilder text = new StringBuilder();
    event.registry.pushListener(new HtmlFilterListener()
    {

      @Override
      public boolean startElement(HtmlEvent event)
      {
        if (event.el.equals("code") == true) {
          event.flushText();
          text.setLength(0);
        }
        return false;
      }

      @Override
      public boolean endElement(HtmlEvent event)
      {
        if (event.el.equals("code") == true) {
          String stext = event.getCollectedText();
          String code = text.toString();
          code = StringUtils.trim(code);
          code = "\n" + code + "\n";
          attr.setBody(code);
          event.resetText();
          return false;
        }
        if (event.el.equals("pre") == false) {
          // TODO UUPS
        }

        event.registry.popListener(this);
        GWikiMacroFragment mf = new GWikiMacroFragment(new GWikiCodeMacro(), attr);
        event.getParseContext().addFragment(mf);
        return false;
      }

      @Override
      public boolean characters(HtmlEvent cevent)
      {
        text.append(cevent.text);
        return false;
      }

    });
    return false;
  }

}
