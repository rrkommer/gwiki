/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   06.12.2009
// Copyright Micromata 06.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki;

import java.util.regex.Pattern;

import junit.framework.TestCase;

import com.uwyn.jhighlight.tools.StringUtils;

public class KeywordMatcherTest extends TestCase
{
  // public String tranformToRegExp(String matcher)
  // {
  // StringBuilder sb = new StringBuilder();
  // List<String> tks = Converter.parseStringTokens(matcher, "()|", true);
  // for (String tk : tks) {
  // char c = tk.charAt(0);
  // switch (c) {
  // case '(':
  // sb.append("(");
  // break;
  // case ')':
  // sb.append("){0,1}");
  // break;
  // case '|':
  // sb.append("|");
  // break;
  // default:
  // sb.append(tk);
  // break;
  // }
  // }
  // return sb.toString();
  // }

  // boolean match(String matcher, String text)
  // {
  // String regex = "spring(((e){0,1}?)((n){0,1}|(r){0,1})){0,1}";
  // Pattern p = Pattern.compile(regex);
  // Matcher m = p.matcher(text);
  // boolean found = m.matches();
  // return found;
  // }
  //
  // boolean match(String matcher, String text)
  // {
  //
  // List<String> tks = Converter.parseStringTokens(matcher, "()|", true);
  // for (String tk : tks) {
  // char c = tk.charAt(0);
  // switch (c) {
  // case '(':
  //
  // break;
  // case ')':
  // sb.append("){0,1}");
  // break;
  // case '|':
  // sb.append("|");
  // break;
  // default:
  // sb.append(tk);
  // break;
  // }
  // }
  // return true;
  // }

  public boolean match(String matcher, String text)
  {
    String rm = StringUtils.replace(matcher, ")", "){0,1}");
    return Pattern.compile(rm).matcher(text).matches();
  }

  public void testMatch()
  {
    // spring((e)((n)|(r)))
    // spring(((e){0,1}?))((n){0,1}|(r){0,1}){0,1}
    String matcher = "spring(e(n|r))";
    // assertTrue(match(matcher, "spring"));
    // assertTrue(match(matcher, "springe"));
    assertTrue(match(matcher, "springen"));
    assertTrue(match(matcher, "springer"));
    assertFalse(match(matcher, "springex"));
    assertFalse(match(matcher, "springnr"));
    assertFalse(match(matcher, "springr"));

    matcher = "spring(e(nd|r))";
    assertTrue(match(matcher, "springer"));
    assertTrue(match(matcher, "springend"));
  }
}
