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

/**
 * Create a diff inside a line.
 * 
 * @author roger@micromata.de
 * 
 */
public class CharacterDiffBuilder extends DiffBuilder
{
  public String normalize(String text)
  {
    // text = text.replaceAll("\\s", "");
    return text;
  }

  public boolean equalLine(String left, String right)
  {
    return left.equals(right);
  }

  public boolean isIgnore(String line)
  {
    return false;
  }

  protected List<String> parseText(String text)
  {
    //String[] ret = text.split("[]");
    List<String> ret = new ArrayList<String>(text.length());
    for (int i = 0; i < text.length(); ++i) {
      ret.add(Character.toString(text.charAt(i)));
    }
    return ret;
  }
}
