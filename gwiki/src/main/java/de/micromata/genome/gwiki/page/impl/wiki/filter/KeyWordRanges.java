/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   18.11.2009
// Copyright Micromata 18.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.filter;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gwiki.model.GWikiElementInfo;

public class KeyWordRanges extends ArrayList<KeyWordRange>
{

  private static final long serialVersionUID = -8223981452656169961L;

  protected boolean intersect(int s1, int e1, int s2, int e2)
  {
    if (e2 <= s1) {
      return false;
    }
    if (e1 <= s2) {
      return false;
    }
    return true;
  }

  public void addIntersect(int start, int end, List<GWikiElementInfo> links)
  {
    for (int i = 0; i < size(); ++i) {
      KeyWordRange r = get(i);
      if (intersect(start, end, r.start, r.end) == false) {
        //
        continue;
      } else {
        int ns = Math.min(start, r.start);
        int ne = Math.max(end, r.end);
        List<GWikiElementInfo> ml = new ArrayList<GWikiElementInfo>();
        ml.addAll(r.links);
        ml.addAll(links);
        set(i, new KeyWordRange(ns, ne, ml));
        return;
      }
    }
    add(new KeyWordRange(start, end, links));
  }
}
