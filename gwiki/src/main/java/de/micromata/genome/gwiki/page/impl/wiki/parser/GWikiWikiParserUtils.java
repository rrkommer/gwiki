package de.micromata.genome.gwiki.page.impl.wiki.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentP;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiNestableFragment;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiWikiParserUtils
{
  private static final Logger LOG = Logger.getLogger(GWikiWikiParserUtils.class);

  public static class RevisePsVisitor implements GWikiFragmentVisitor
  {
    List<GWikiFragment> path = new ArrayList<>();

    @Override
    public void begin(GWikiFragment fragment)
    {
      if (GWikiMacroRenderFlags.ContainsTextBlock.isSet(fragment.getRenderModes()) == true) {
        if (path.size() >= 2 && path.get(path.size() - 1) instanceof GWikiFragmentP) {
          GWikiFragmentP pp = (GWikiFragmentP) path.get(path.size() - 1);
          if (pp.getChilds().size() == 1) {
            pushThisAbove(fragment);
          }
        }

      }
      path.add(fragment);
    }

    private void pushThisAbove(GWikiFragment fragment)
    {
      GWikiFragment toreplace = path.get(path.size() - 1);
      GWikiFragment whereToreplace = path.get(path.size() - 2);
      if (whereToreplace instanceof GWikiNestableFragment) {
        GWikiNestableFragment nf = (GWikiNestableFragment) whereToreplace;
        boolean replaced = nf.replaceChilds(toreplace, Collections.singletonList(fragment));
        if (replaced == false) {
          LOG.warn("pushThisAbove: Cannot replace fragment in parent");
        }
      } else {
        LOG.warn("pushThisAbove: parent is not nestable");
      }
    }

    @Override
    public void end(GWikiFragment fragment)
    {
      if (GWikiMacroRenderFlags.ContainsTextBlock.isSet(fragment.getRenderModes()) == true
          && GWikiMacroRenderFlags.NoWrapWithP.isSet(fragment.getRenderModes()) == false) {
        GWikiNestableFragment nest = (GWikiNestableFragment) fragment;
        List<GWikiFragment> childs = nest.getChilds();
        int startRepl = -1;
        int endRepl = 0;
        List<GWikiFragment> nchildList = new ArrayList<>();
        for (int i = 0; i < childs.size(); ++i) {
          GWikiFragment cf = childs.get(i);
          boolean nowrap = GWikiMacroRenderFlags.NoWrapWithP.isSet(cf.getRenderModes()) ||
              GWikiMacroRenderFlags.ContainsTextBlock.isSet(cf.getRenderModes());
          if (nowrap) {
            if (startRepl == -1) {
              continue;
            } else {
              nchildList.add(new GWikiFragmentP(childs.subList(startRepl, i)));
              startRepl = -1;
              nchildList.add(cf);
            }
          } else if (startRepl == -1) {
            startRepl = i;
          } else {
            nchildList.add(cf);
          }
        }
        if (startRepl != -1) {
          List<GWikiFragment> slist = childs.subList(startRepl, childs.size());
          nchildList.add(new GWikiFragmentP(slist));
        }
        nest.setChilds(nchildList);
        //        childs.clear();
        //        childs.addAll(nchildList);
      }
      path.remove(path.size() - 1);

    }
  }

}
