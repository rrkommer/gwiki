package de.micromata.genome.util.text;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class TextSplitterUtils
{

  public static class TKInput
  {
    private String text;

    private char escapeChar;

    private boolean returnUnescaped;

    private int position;

    public int length;

    public TKInput(String text, char escapeChar, boolean returnUnescaped)
    {
      this.text = text;
      this.position = 0;
      this.length = text.length();
      this.escapeChar = escapeChar;
      this.returnUnescaped = returnUnescaped;
    }

    final public boolean eof()
    {
      return position >= length;
    }

    public String rest()
    {
      return text.substring(position);
    }

    public boolean lookup(Token tk)
    {
      // if (escapeChar == 0)
      return tk.match(rest());

      // int tki = 0;
      // int tkil = tk.length();
      // for (int i = position; i < length && tki < tkil; ++i, ++tki) {
      // char c = text.charAt(i);
      // if (c == escapeChar) {
      // if (i + 1 >= length)
      // return false;
      // c = text.charAt(i);
      // }
      // if (c != tk.charAt(tki))
      // return false;
      // }
      // return tki == tkil;
    }

    public Token isToken(Token[] tokens)
    {
      for (Token tk : tokens) {
        if (lookup(tk) == true)
          return tk;
      }
      return null;
    }

    public TokenResult read(Token[] tokens)
    {
      Token tk = isToken(tokens);
      if (tk != null) {
        TokenResult tkn = tk.consume(rest(), escapeChar);
        position += tkn.getConsumedLength();
        return tkn;
      }
      StringBuilder sb = null;
      int lastSafedPos = position;
      for (; position < length; ++position) {
        char c = text.charAt(position);
        if (c == escapeChar) {
          if (position + 1 >= length) {
            throw new RuntimeException("Escape character at end of input. escpe=" + escapeChar + "; text: " + text);
          }
          if (returnUnescaped == false && sb == null) {
            sb = new StringBuilder();
            sb.append(text.substring(lastSafedPos, position));
            lastSafedPos = position;
          }
          ++position;
          c = text.charAt(position);

        } else if (isToken(tokens) != null) {
          break;
        }
        if (sb != null)
          sb.append(c);
      }
      if (sb != null)
        return new UnmatchedToken(sb.toString());
      return new UnmatchedToken(text.substring(lastSafedPos, position));
    }
  }

  public static List<String> parseStringTokens(String text, String[] tokens, char escapeChar, boolean returnDelimiter)
  {
    return parseStringTokens(text, tokens, escapeChar, returnDelimiter, false);
  }

  /**
   * 
   * @param text
   * @param tokens used as RegExp
   * @param escapeChar
   * @param returnDelimiter
   * @param returnUnescaped
   * @return
   */
  public static List<String> parseStringTokens(String text, String[] tokens, char escapeChar, boolean returnDelimiter,
      boolean returnUnescaped)
  {
    Token[] tks = new Token[tokens.length];
    for (int i = 0; i < tokens.length; ++i) {
      String p = tokens[i];
      p = Pattern.quote(p);
      p = "^(" + p + ")(.*)";
      tks[i] = new RegExpToken(i + 1, p);
    }
    List<TokenResult> tksr = parseStringTokens(text, tks, escapeChar, returnDelimiter, returnUnescaped);
    List<String> ret = new ArrayList<String>();
    for (TokenResult tkr : tksr) {
      ret.add(tkr.getConsumed());
    }
    return ret;
  }

  public static List<String> parseStringTokens(String text, char[] tokens, char escapeChar, boolean returnDelimiter)
  {
    return parseStringTokens(text, tokens, escapeChar, returnDelimiter, false);
  }

  public static List<String> parseStringTokens(String text, char[] tokens, char escapeChar, boolean returnDelimiter, boolean returnUnescaped)
  {
    Token[] tks = new Token[tokens.length];
    for (int i = 0; i < tokens.length; ++i) {
      tks[i] = new CharToken(i + 1, tokens[i]);
    }
    List<TokenResult> tksr = parseStringTokens(text, tks, escapeChar, returnDelimiter, returnUnescaped);
    List<String> ret = new ArrayList<String>();
    for (TokenResult tkr : tksr) {
      ret.add(tkr.getConsumed());
    }
    return ret;
  }

  public static List<TokenResult> parseStringTokens(String text, Token[] tokens, char escapeChar, boolean returnDelimiter)
  {
    return parseStringTokens(text, tokens, escapeChar, returnDelimiter, false);
  }

  public static String unescape(String text, char escapeChar)
  {
    if (text.indexOf(escapeChar) == -1)
      return text;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < text.length(); ++i) {
      char c = text.charAt(i);
      if (c == escapeChar) {
        ++i;
        if (i >= text.length())
          throw new RuntimeException("Escape character '" + escapeChar + "' at end of text: " + text);
        sb.append(text.charAt(i));
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  public static List<TokenResult> parseStringTokens(String text, Token[] tokens, char escapeChar, boolean returnDelimiter,
      boolean returnUnescaped)
  {
    List<TokenResult> result = new ArrayList<TokenResult>();
    if (StringUtils.isEmpty(text) == true) {
      return result;
    }
    TKInput tkinp = new TKInput(text, escapeChar, returnUnescaped);
    while (tkinp.eof() == false) {
      TokenResult tk = tkinp.read(tokens);
      if (tk == null)
        break;
      if ((tk instanceof UnmatchedToken) == false && returnDelimiter == false)
        continue;

      result.add(tk);
    }
    return result;
  }

  // public static List<String> parseStringTokens(String text, char[] tokens, char escapeChar, boolean returnDelimiter)
  // {
  // String[] stk = new String[tokens.length];
  // for (int i = 0; i < tokens.length; ++i) {
  // stk[i] = Character.toString(tokens[i]);
  // }
  // return parseStringTokens(text, stk, escapeChar, returnDelimiter);
  // }

  /**
   * Verwendet einen StringTokenizer und liefert das Ergebnis als Liste
   */
  public static List<String> parseStringTokenWOD(String text, char... tokens)
  {
    return parseStringTokens(text, tokens, false);
  }

  public static List<String> parseStringTokenWD(String text, char... tokens)
  {
    return parseStringTokens(text, tokens, true);
  }

  public static List<String> parseStringTokens(String text, char[] tokens, boolean returnDelimiter)
  {
    List<String> result = new ArrayList<String>();
    String t = new String(tokens);
    StringTokenizer st = new StringTokenizer(text, t, returnDelimiter);
    while (st.hasMoreTokens() == true) {
      result.add(st.nextToken());
    }
    return result;
  }

  /**
   * return first found index of search which is not escaped
   * 
   * @param text
   * @param search
   * @param escapeChar
   * @return
   */
  public static int getUnescapedIndexOf(String text, char search, char escapeChar)
  {
    for (int i = 0; i < text.length(); ++i) {
      char c = text.charAt(i);
      if (c == escapeChar)
        ++i;
      else if (c == search)
        return i;
    }
    return -1;
  }

  public static int getUnescapedIndexOf(String text, char search)
  {
    return getUnescapedIndexOf(text, search, '\\');
  }

  public static List<Integer> findTokenPos(String text, char search, char escapeChar)
  {
    List<Integer> ret = new ArrayList<Integer>();
    for (int i = 0; i < text.length(); ++i) {
      char c = text.charAt(i);
      if (c == escapeChar)
        ++i;
      else if (c == search)
        ret.add(i);
    }
    return ret;
  }

  public static String unescape(String text, char escapeChar, char... potentialEscaped)
  {
    if (text.indexOf(escapeChar) == -1)
      return text;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < text.length(); ++i) {
      char c = text.charAt(i);
      if (c == escapeChar) {
        ++i;
        if (text.length() > i && ArrayUtils.contains(potentialEscaped, text.charAt(i)) == true) {
          sb.append(text.charAt(i));
        } else {
          sb.append(escapeChar).append(text.charAt(i));
        }
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  public static String escape(String text, char escapeChar, char... toEscaped)
  {
    if (StringUtils.indexOfAny(text, toEscaped) == -1)
      return text;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < text.length(); ++i) {
      char c = text.charAt(i);
      if (ArrayUtils.contains(toEscaped, c) == true) {
        sb.append(escapeChar);
      }
      sb.append(c);
    }
    return sb.toString();
  }
}
