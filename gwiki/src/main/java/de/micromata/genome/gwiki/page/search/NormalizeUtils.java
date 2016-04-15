//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.page.search;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static utils to normalize search tokens.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class NormalizeUtils
{

  /**
   * The non alphanum pattern.
   */
  private static ThreadLocal<Pattern> NON_ALPHANUM_PATTERN = new ThreadLocal<Pattern>()
  {
    @Override
    protected Pattern initialValue()
    {
      return Pattern.compile("[^A-Za-z0-9]");
    }
  };

  /**
   * Clean string.
   *
   * @param string the string
   * @return the string
   */
  public static String cleanString(final String string)
  {
    if (string == null) {
      return null;
    }
    final int len = string.length();
    final StringBuilder ret = new StringBuilder(len + 5);
    for (int idx = 0; idx < len; idx++) {
      final char c = string.charAt(idx);
      switch (c) {
        case '\u00e4':
          ret.append("ae");
          break;
        case '\u00f6':
          ret.append("oe");
          break;
        case '\u00fc':
          ret.append("ue");
          break;
        case '\u00c4':
          ret.append("AE");
          break;
        case '\u00d6':
          ret.append("OE");
          break;
        case '\u00dc':
          ret.append("UE");
          break;
        case '\u00df':
          ret.append("SS");
          break;
        default:
          ret.append(c);
      }
    }
    return ret.toString();
  }

  /**
   * See normalize(name, true);.
   *
   * @param Name the name
   * @return the string
   */
  public static String normalize(final String Name)
  {
    return normalize(Name, true);
  }

  /**
   * Normalize.
   *
   * @param Name the name
   * @param uppercase the uppercase
   * @return the string
   */
  public static String normalize(final String Name, boolean uppercase)
  {
    if (Name == null) {
      return null;
    }
    String ret = cleanString(Name);
    if (uppercase == true) {
      ret = ret.toUpperCase();
    }
    // ret = ret.replaceAll("[^A-Z0-9\\ ]", "");
    final Matcher matcher = NON_ALPHANUM_PATTERN.get().matcher(ret);
    ret = matcher.replaceAll("");
    return ret;
  }

  /**
   * The standard filename.
   */
  private static ThreadLocal<Pattern> STANDARD_FILENAME = new ThreadLocal<Pattern>()
  {
    @Override
    protected Pattern initialValue()
    {
      return Pattern.compile("[^A-Za-z0-9/\\.\\-_\\+]");
    }
  };

  /**
   * Normalize to path.
   *
   * @param Name the name
   * @return the string
   */
  public static String normalizeToPath(final String Name)
  {
    if (Name == null) {
      return null;
    }
    String ret = cleanString(Name);
    // ret = ret.replaceAll("[^A-Z0-9\\ ]", "");
    final Matcher matcher = STANDARD_FILENAME.get().matcher(ret);
    ret = matcher.replaceAll("");
    return ret;
  }

  /**
   * The link target.
   */
  private static ThreadLocal<Pattern> LINK_TARGET = new ThreadLocal<Pattern>()
  {
    @Override
    protected Pattern initialValue()
    {
      return Pattern.compile("[^A-Za-z0-9#/\\.\\-_]");
    }
  };

  /**
   * Normalize to target.
   *
   * @param Name the name
   * @return the string
   */
  public static String normalizeToTarget(final String Name)
  {
    if (Name == null) {
      return null;
    }
    String ret = cleanString(Name);
    final Matcher matcher = LINK_TARGET.get().matcher(ret);
    ret = matcher.replaceAll("");
    return ret;
  }
}
