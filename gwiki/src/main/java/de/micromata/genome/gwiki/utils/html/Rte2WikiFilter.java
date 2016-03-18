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

  public static class RteMacroBodyFragment extends RteInternalFragment
  {
    GWikiMacroFragment macroFragment;

    public RteMacroBodyFragment(GWikiMacroFragment macroFragment)
    {
      this.macroFragment = macroFragment;
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
    LOG.debug("rte_start: " + element.rawname);
    String en = element.rawname.toLowerCase();
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
    if (en.equals("pre") == true) {
      return true;
    }
    if (en.equals("div") == false) {
      return false;
    }
    String cls = attributes.getValue("class");
    if (StringUtils.contains(cls, "weditmacrobody") == false) {
      return false;
    }
    GWikiFragment lastfrag = parseContext.lastFrag();
    RteMacroBodyFragment nfrag = new RteMacroBodyFragment((GWikiMacroFragment) lastfrag);
    parseContext.addFragment(nfrag);
    parseContext.pushFragList();
    return true;
  }

  protected boolean handleMacro(String en, QName element, XMLAttributes attributes, Augmentations augs)
      throws XNIException
  {

    if (en.equals("div") == false) {
      return false;
    }
    String cls = attributes.getValue("class");
    if (StringUtils.contains(cls, "weditmacrohead") == false) {
      return false;
    }
    String macrodef = attributes.getValue("data-macrohead");
    if (StringUtils.isBlank(macrodef) == true) {
      GLog.warn(GWikiLogCategory.Wiki, "Cannot parse macro head while parsing rte");
      return false;
    }
    flushText();
    MacroAttributes ma = new MacroAttributes(macrodef);
    GWikiMacroFragment frag = parseContext.createMacro(ma);
    parseContext.addFragment(frag);
    parseContext.pushFragList();

    return true;
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
    LOG.debug("rte_end: " + element.rawname);

    String en = element.rawname.toLowerCase();
    if (en.equals("pre") == true) {
      return;

    }
    if (en.equals("div") == false) {
      super.endElement(element, augs);
      return;
    }

    //    GWikiFragment lastfrag = parseContext.lastFragment();
    GWikiFragment lastfrag = parseContext.lastParentFrag();
    if (lastfrag instanceof RteMacroBodyFragment) {
      flushText();
      LOG.debug("rte_end: macrobody" + element.rawname);
      List<GWikiFragment> childs = parseContext.popFragList();
      ((RteMacroBodyFragment) lastfrag).addChilds(childs);
      return;
    }
    if (lastfrag instanceof GWikiMacroFragment) {
      flushText();

      LOG.debug("rte_end: macro" + element.rawname);
      GWikiMacroFragment mf = (GWikiMacroFragment) lastfrag;
      List<GWikiFragment> childs = parseContext.popFragList();
      if (mf.getMacro().hasBody() == true) {
        RteMacroBodyFragment mbody = (RteMacroBodyFragment) childs.get(0);
        List<GWikiFragment> bodychilds = mbody.getChilds();
        if (mf.getMacro().evalBody() == true) {
          mf.getAttrs().setChildFragment(new GWikiFragmentChildContainer(bodychilds));
        } else {
          String text = childsToTextString(bodychilds);
          mf.getAttrs().setBody(text);
        }

      }
      return;
    }
    super.endElement(element, augs);
  }

  @Override
  public void characters(XMLString text, Augmentations augs) throws XNIException
  {
    LOG.debug("rt text: " + text.toString());
    GWikiFragment lpfrag = parseContext.lastParentFrag();
    if (lpfrag instanceof GWikiMacroFragment) {
      LOG.debug("rt text ignore: " + text.toString());
      return;
    }
    super.characters(text, augs);
  }
}
