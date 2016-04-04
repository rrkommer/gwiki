package de.micromata.genome.gwiki.utils.html;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.logging.GLog;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class RteMacroFilterListener implements HtmlFilterListener
{
  private static final Logger LOG = Logger.getLogger(RteMacroFilterListener.class);

  @Override
  public int listenerPrio()
  {
    return 10;
  }

  @Override
  public boolean startElement(HtmlEvent event)
  {
    if (event.el.equals("div") == false && event.el.equals("span") == false) {
      return true;
    }

    if (event.containsInStyleClass("weditmacroframe") == false) {
      return true;
    }
    String macroElName = event.el;
    event.flushText();
    event.getParseContext().pushFragList();
    event.registry.pushListener(new HtmlFilterListener()
    {
      private StringBuilder bodyText = new StringBuilder();
      private boolean inBody = false;
      private boolean inHead = false;
      private int elCount = 1;
      private int elBodyCount = 0;
      private boolean hasError = false;
      GWikiMacroFragment macroFragment;

      @Override
      public String toString()
      {
        StringBuilder sb = new StringBuilder();
        sb.append(macroFragment).append("; elc=").append(elCount).append("; bc=").append(elBodyCount);
        return sb.toString();
      }

      @Override
      public boolean startElement(HtmlEvent event)
      {
        System.out.println("startElement: " + event + "; " + this);
        ++elCount;
        if (inBody == true) {
          ++elBodyCount;
        }
        if (inHead == false && inBody == false) {
          if (event.el.equals(macroElName) == true && event.containsInStyleClass("weditmacrohead") == true) {
            String macrodef = event.getAttrVal("data-macrohead");
            if (StringUtils.isBlank(macrodef) == true) {
              GLog.warn(GWikiLogCategory.Wiki, "Cannot parse macro head while parsing rte");
              hasError = true;
              return false;
            }
            MacroAttributes attrs = new MacroAttributes(macrodef);
            macroFragment = event.getParseContext().createMacro(attrs);
            inHead = true;
            event.resetText();
            inHead = true;
            return false;
          }
          GLog.warn(GWikiLogCategory.Wiki, "Cannot parse macro head while parsing rte missing head");
          hasError = true;
          return false;
        } else if (inHead == true) {
          if (event.el.equals(macroElName) == true && event.containsInStyleClass("weditmacrobody") == true) {
            inHead = false;
            inBody = true;
            elBodyCount = 1;
          }
        } else if (inBody == true) {
          if (macroFragment.getMacro().evalBody() == false) {
            GLog.warn(GWikiLogCategory.Wiki, "Unexpected tag in pre");
          } else {
            return true;
          }
        }
        return false;
      }

      @Override
      public boolean endElement(HtmlEvent event)
      {
        System.out.println("endElement: " + event + "; " + this);
        --elCount;
        if (inBody == true) {
          --elBodyCount;
          if (elBodyCount == 0) {
            inBody = false;
            event.flushText();
            List<GWikiFragment> childs = event.getParseContext().popFragList();
            if (macroFragment.getMacro().hasBody() == true) {

              if (macroFragment.getMacro().evalBody() == true) {
                macroFragment.getAttrs().setChildFragment(new GWikiFragmentChildContainer(childs));
              } else {
                macroFragment.getAttrs().setBody(bodyText.toString());
              }
            }
          } else if (macroFragment.getMacro().hasBody() == true
              && macroFragment.getMacro().evalBody() == true) {
            return true;
          }
        }
        if (elCount == 0) {
          event.registry.popListener(this);
          event.getParseContext().addFragment(macroFragment);
          return false;
        }
        return false;

      }

      @Override
      public boolean emptyElement(HtmlEvent event)
      {
        if (inBody == false) {
          return false;
        }
        if (macroFragment.getMacro().hasBody() == true) {
          if (macroFragment.getMacro().evalBody() == false) {
            return false;
          } else {
            return true;
          }
        }
        return false;
      }

      @Override
      public boolean characters(HtmlEvent event)
      {
        if (inBody == false) {
          return false;
        }
        if (macroFragment.getMacro().hasBody() == true) {
          if (macroFragment.getMacro().evalBody() == false) {
            bodyText.append(event.text);
            return false;
          } else {
            return true;
          }
        }
        return false;
      }

    });
    return false;
  }

}
