/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   22.12.2009
// Copyright Micromata 22.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.utils;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.util.types.Converter;

/**
 * Build a diff view for a line.
 * 
 * @author roger
 * 
 */
public class WordDiffBuilder extends DiffBuilder
{
  protected List<String> parseText(String text)
  {
    String splitTokens = ".:,!?-_ \t\n";
    List<String> tks = Converter.parseStringTokens(text, splitTokens, true);
    List<String> ret = new ArrayList<String>();
    int idx = 0;
    for (String tk : tks) {
      if (tk.length() == 1 && splitTokens.contains(tk) && ret.size() > 0) {
        ret.set(idx - 1, ret.get(idx - 1) + tk);
      } else {
        ret.add(tk);
        ++idx;
      }

    }
    return ret;
  }
}
