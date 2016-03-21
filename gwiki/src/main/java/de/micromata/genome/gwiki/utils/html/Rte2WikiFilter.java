package de.micromata.genome.gwiki.utils.html;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;

import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBr;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildsBase;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentImage;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor;
import de.micromata.genome.logging.GLog;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class Rte2WikiFilter extends Html2WikiFilter
{
  private static final Logger LOG = Logger.getLogger(Rte2WikiFilter.class);

  public static class RteInternalFragment extends GWikiFragmentChildsBase
  {

    @Override
    public boolean render(GWikiContext ctx)
    {
      return false;
    }

    @Override
    public void getSource(StringBuilder sb)
    {
    }

  }

  public static class RteMacroFragment extends RteInternalFragment
  {
    GWikiMacroFragment macroFragment;
    boolean ignoreContent = false;
    boolean inHead = false;
    boolean inBody = false;
    int headDivs = 0;
    int bodyDivs = 0;
    int consumeEndDiv = 2;

    public RteMacroFragment()
    {
    }

  }

  public Rte2WikiFilter(GWikiContext wikiContext)
  {
    parseContext.getMacroFactories()
        .putAll(wikiContext.getWikiWeb().getWikiConfig().getWikiMacros(wikiContext));
  }

  @Override
  protected GWikiFragmentVisitor createVisitor()
  {
    return new Rte2WikiFragmentVisitor();
  }

  @Override
  public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException
  {
    String en = element.rawname.toLowerCase();
    if (handleImage(en, element, attributes, augs) == true) {
      return;
    }
    super.emptyElement(element, attributes, augs);
  }

  @Override
  public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException
  {
    String en = element.rawname.toLowerCase();
    if (LOG.isDebugEnabled() == true) {
      StringBuilder sb = new StringBuilder("rte_start: <" + en);
      for (int i = 0; i < attributes.getLength(); ++i) {
        String name = attributes.getLocalName(i);
        String value = attributes.getValue(i);
        sb.append(" ").append(name).append("='").append(value).append("'");
      }
      sb.append(">");
      LOG.debug(sb.toString());
    }

    if (handleMacro(en, element, attributes, augs) == true) {
      return;
    }
    if (handleMacroBody(en, element, attributes, augs) == true) {
      return;
    }
    super.startElement(element, attributes, augs);
  }

  private boolean handleMacroBody(String en, QName element, XMLAttributes attributes, Augmentations augs)
  {
    if (en.equals("div") == false) {
      return false;
    }
    String cls = attributes.getValue("class");
    if (StringUtils.contains(cls, "weditmacrobody") == false) {
      return false;
    }

    GWikiFragment lastfrag = parseContext.peekFragStack();
    if ((lastfrag instanceof RteMacroFragment) == false) {
      GLog.warn(GWikiLogCategory.Wiki, "Expect RteMacroFragment stack in body");
      return false;
    }
    RteMacroFragment nfrag = (RteMacroFragment) lastfrag;
    parseContext.pushFragList();
    nfrag.inBody = true;
    nfrag.bodyDivs = 1;
    return true;
  }

  protected boolean handleMacro(String en, QName element, XMLAttributes attributes, Augmentations augs)
      throws XNIException
  {

    String cls = attributes.getValue("class");
    if (en.equals("div") == true && StringUtils.contains(cls, "weditmacroframe") == true) {
      flushText();
      RteMacroFragment rtmacfragment = new RteMacroFragment();
      parseContext.pushFragStack(rtmacfragment);
      return true;
    }

    if (en.equals("div") == true && StringUtils.contains(cls, "weditmacrohead") == true) {
      GWikiFragment lfrag = parseContext.peekFragStack();
      if ((lfrag instanceof RteMacroFragment) == false) {
        GLog.warn(GWikiLogCategory.Wiki, "Expect RteMacroFragment stack");
        return false;
      }
      String macrodef = attributes.getValue("data-macrohead");
      if (StringUtils.isBlank(macrodef) == true) {
        GLog.warn(GWikiLogCategory.Wiki, "Cannot parse macro head while parsing rte");
        return false;
      }
      RteMacroFragment rtf = (RteMacroFragment) lfrag;
      MacroAttributes attrs = new MacroAttributes(macrodef);
      rtf.macroFragment = parseContext.createMacro(attrs);
      rtf.ignoreContent = true;
      rtf.inHead = true;
      rtf.headDivs = 1;
      flushText();
      return true;
    }
    GWikiFragment lfrag = parseContext.peekFragStack();
    if ((lfrag instanceof RteMacroFragment) == true) {
      RteMacroFragment rtf = (RteMacroFragment) lfrag;
      if (rtf.inHead == true) {
        ++rtf.headDivs;
        return true;
      }
      if (rtf.inBody == true) {
        ++rtf.bodyDivs;
        return false;
      }
    }
    return false;
  }

  protected boolean handleImage(String en, QName element, XMLAttributes attributes, Augmentations augs)
      throws XNIException
  {
    if (en.equals("img") == false) {
      return false;
    }
    String pageId = attributes.getValue("data-pageid");
    if (StringUtils.isBlank(pageId) == true) {
      return false;
    }
    super.parseImage(attributes);
    GWikiFragmentImage image = (GWikiFragmentImage) parseContext.lastFragment();
    String styleClass = image.getStyleClass();
    styleClass = StringUtils.remove(styleClass, "weditimg");
    if (StringUtils.isBlank(styleClass) == true) {
      styleClass = "";
    }
    image.setStyleClass(styleClass);
    image.setTarget(pageId);
    return true;
  }

  private String childsToTextString(List<GWikiFragment> bodychilds)
  {
    StringBuilder sb = new StringBuilder();
    for (GWikiFragment frag : bodychilds) {
      if (frag instanceof GWikiFragmentText) {
        GWikiFragmentText txtf = (GWikiFragmentText) frag;
        sb.append(txtf.getText().toString());
      } else if (frag instanceof GWikiFragmentBr) {
        sb.append("\n");
      } else {
        LOG.warn("Expected text nodes");
      }
    }
    return sb.toString();
  }

  @Override
  public void endElement(QName element, Augmentations augs) throws XNIException
  {
    String en = element.rawname.toLowerCase();

    if (LOG.isDebugEnabled() == true) {
      LOG.debug("rte_end: </" + en + ">");
    }
    GWikiFragment lastparent = parseContext.peekFragStack();
    if ((lastparent instanceof RteMacroFragment) == false) {
      super.endElement(element, augs);
      return;
    }
    RteMacroFragment ftf = (RteMacroFragment) lastparent;
    if (ftf.inBody == true) {
      --ftf.bodyDivs;
      if (ftf.bodyDivs <= 0) {
        ftf.inBody = false;
        List<GWikiFragment> childs = parseContext.popFragList();
        GWikiMacro macro = ftf.macroFragment.getMacro();
        if (macro.hasBody() == true) {
          if (macro.evalBody() == true) {
            ftf.macroFragment.getAttrs().setChildFragment(new GWikiFragmentChildContainer(childs));
          } else {
            String text = childsToTextString(childs);
            ftf.macroFragment.getAttrs().setBody(text);
          }
        } else {
          parseContext.popFragStack();
          parseContext.addFragment(ftf.macroFragment);
        }
        return;
      }
    } else if (ftf.inHead == true) {
      --ftf.headDivs;
      if (ftf.headDivs <= 0) {
        ftf.inHead = false;
        if (ftf.macroFragment.getMacro().hasBody() == false) {
          ftf.inBody = true;
          parseContext.pushFragList();
        }
      }
      return;
    } else {
      parseContext.popFragStack();
      parseContext.addFragment(ftf.macroFragment);
      return;
    }
    super.endElement(element, augs);
  }

  @Override
  public void characters(XMLString text, Augmentations augs) throws XNIException
  {
    GWikiFragment lastfrag = parseContext.peekFragStack();
    if ((lastfrag instanceof RteMacroFragment) == false) {
      super.characters(text, augs);
      return;
    }
    RteMacroFragment rtf = (RteMacroFragment) lastfrag;
    if (rtf.inBody == false) {
      return;
    }
    super.characters(text, augs);
  }
}