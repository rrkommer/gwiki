package de.micromata.genome.util.matcher;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.util.matcher.string.SimpleWildcardMatcherFactory;
import de.micromata.genome.util.text.RegExpToken;
import de.micromata.genome.util.text.TextSplitterUtils;
import de.micromata.genome.util.text.Token;
import de.micromata.genome.util.text.TokenResult;

/**
 * A rule which contains a list of rules devided by divider.
 * 
 * Each rule starts with - or +, which mark the following rule if matches or not
 * 
 * @author roger
 * 
 */
public class BooleanListRulesFactory<T> implements MatcherFactory<T>
{
  public static final int TK_UNMATCHED = 0;

  public static final int TK_COMMA = 1;

  public static final int TK_BO = 2;

  public static final int TK_BC = 3;

  public static final int TK_AND = 4;

  public static final int TK_OR = 5;

  public static final int TK_PLUS = 6;

  public static final int TK_MINUS = 7;

  public static final int TK_HASH = 8;

  public static final int TK_NOT = 9;

  public static final int TK_BOBC_PRIO = 3;

  public static final int TK_COMMA_PRIO = 1;

  public static final int TK_ANDOR_PRIO = 2;

  private static final Token[] DefaultToken = new Token[] { //
  new RegExpToken(TK_COMMA, "^[ \\t]*(,)[ \\t]*(.*)"), //
      new RegExpToken(TK_BO, "^[ \\t]*(\\()[\\t ]*(.*)"), //
      new RegExpToken(TK_BC, "^[ \\t]*(\\))[ \\t]*(.*)"), //
      new RegExpToken(TK_AND, "^[ \\t]*(\\&\\&)[ \\t]*(.*)"), //
      new RegExpToken(TK_OR, "^[ \\t]*(\\|\\|)[ \\t]*(.*)"), //
      new RegExpToken(TK_PLUS, "^[ \\t]*(\\+)[ \\t]*(.*)"), //
      new RegExpToken(TK_MINUS, "^[ \\t]*(\\-)[ \\t]*(.*)"), //
      new RegExpToken(TK_NOT, "^[ \\t]*(\\!)[ \\t]*(.*)") //
  };

  // private String[] dividers = new String[] { ", ", " ,", ",", " (", "(", "( ", " )", ") ", " and ", " or "};

  private char escapeChar = '\\';

  private MatcherFactory<T> elementFactory = new SimpleWildcardMatcherFactory<T>();

  public BooleanListRulesFactory()
  {

  }

  public BooleanListRulesFactory(MatcherFactory<T> elementFactory)
  {
    this.elementFactory = elementFactory;
  }

  // public BooleanListRulesFactory(MatcherFactory<T> elementFactory, String... dividers)
  // {
  // this.elementFactory = elementFactory;
  // this.dividers = dividers;
  // }
  private static class TokenResultList
  {
    public List<TokenResult> tokenResults;

    public int position;

    public String pattern;

    public TokenResultList(List<TokenResult> tokens, int position, String pattern)
    {
      this.tokenResults = tokens;
      this.position = position;
      this.pattern = pattern;
    }

    public TokenResult curToken()
    {
      return tokenResults.get(position);
    }

    public TokenResult nextToken()
    {
      ++position;
      if (position >= tokenResults.size())
        return null;
      return tokenResults.get(position);
    }

    public boolean eof()
    {
      return position >= tokenResults.size();
    }

    public String restOfTokenString()
    {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < tokenResults.size(); ++i) {
        sb.append(tokenResults.get(i).getConsumed());
      }
      return sb.toString();
    }
  }

  public Matcher<T> createMatcher(String pattern)
  {
    // List<BooleanListMatcher<T>> blistStack = new ArrayList<BooleanListMatcher<T>>();
    // blistStack.add(new BooleanListMatcher<T>(new ArrayList<Matcher<T>>()));
    List<TokenResult> tokenResults = TextSplitterUtils.parseStringTokens(pattern, DefaultToken, escapeChar, true, true);
    TokenResultList tkl = new TokenResultList(tokenResults, 0, pattern);
    Matcher<T> ret = consume(tkl);
    if (tkl.eof() == false) {
      throw new InvalidMatcherGrammar("unconsumed tokens. pattern: " + pattern + "; rest: " + tkl.restOfTokenString());
    }
    return ret;
  }

  private Matcher<T> consume(TokenResultList tokens)
  {
    return consumeAndOr(tokens);
  }

  private Matcher<T> consumeBracket(TokenResultList tks)
  {
    if (tks.eof() == true)
      return null;
    TokenResult tk = tks.curToken();
    if (tk.getTokenType() == TK_BO) {
      tks.nextToken();
      Matcher<T> m = consume(tks);
      tk = tks.curToken();
      if (tk.getTokenType() != TK_BC) {
        throw new InvalidMatcherGrammar("grammar has no matching close bracket: " + tks.pattern);
      }
      tks.nextToken();
      return m;
    }
    return consumePlusMinus(tks);
  }

  private Matcher<T> consumeAndOr(TokenResultList tks)
  {
    Matcher<T> left = consumeList(tks);
    if (left == null)
      return left;
    if (tks.eof() == true)
      return left;
    TokenResult tk = tks.curToken();
    if (tk.getTokenType() == TK_AND || tk.getTokenType() == TK_OR) {
      tks.nextToken();
      Matcher<T> right = consumeAndOr(tks);
      if (right == null) {
        throw new InvalidMatcherGrammar("Missing right express of <expr>[&&||\\|]<expr>: "
            + tks.pattern
            + "; rest: "
            + tks.restOfTokenString());
      }
      if (tk.getTokenType() == TK_AND)
        return new AndMatcher<T>(left, right);
      return new OrMatcher<T>(left, right);
    }
    return left;
  }

  private Matcher<T> consumeList(TokenResultList tks)
  {

    Matcher<T> left = consumeBracket(tks);
    if (left == null)
      return null;
    if (tks.eof() == true)
      return left;
    List<Matcher<T>> elements = new ArrayList<Matcher<T>>();
    elements.add(left);

    do {
      TokenResult tk = tks.curToken();
      if (tk.getTokenType() != TK_COMMA)
        break;
      tks.nextToken();
      left = consumeBracket(tks);
      if (left == null) {
        break;
      }
      elements.add(left);
    } while (left != null && tks.eof() == false);
    if (elements.size() == 1)
      return elements.get(0);
    return new BooleanListMatcher<T>(elements);
  }

  private Matcher<T> consumePlusMinus(TokenResultList tokens)
  {
    if (tokens.eof() == true)
      return null;
    TokenResult tk = tokens.curToken();
    if (tk.getTokenType() == TK_NOT) {
      tokens.nextToken();
      Matcher<T> nt = consumeList(tokens);
      return new NotMatcher<T>(nt);
    }
    if (tk.getTokenType() == TK_PLUS || tk.getTokenType() == TK_MINUS) {
      tokens.nextToken();
      Matcher<T> n = consumeListElement(tokens);
      if (tk.getTokenType() == TK_HASH) {
        return new HashmarkMatcher<T>(n);
      }
      return new TreeStateMatcher<T>(n, tk.getTokenType() == TK_PLUS);
    }
    return consumeListElement(tokens);
  }

  private Matcher<T> consumeListElement(TokenResultList tokens)
  {

    if (tokens.eof() == true)
      return null;
    TokenResult tk = tokens.curToken();
    if (tk.getTokenType() != TK_UNMATCHED) {
      throw new InvalidMatcherGrammar("Excepting element. Got: " + tk.getConsumed() + "; pattern: " + tokens.pattern);
    }
    // TODO ggf. problematisch, wenn intern auch escaped wird
    String elText = TextSplitterUtils.unescape(tk.getConsumed(), escapeChar);
    Matcher<T> m = elementFactory.createMatcher(elText);
    tokens.nextToken();
    return m;
  }

  public String getRuleString(Matcher<T> matcher)
  {

    if (matcher instanceof BooleanListMatcher) {
      List<Matcher<T>> ruleList = ((BooleanListMatcher<T>) matcher).getMatcherList();
      boolean isFirst = true;
      StringBuilder sb = new StringBuilder();
      for (Matcher<T> r : ruleList) {
        if (isFirst == false) {
          sb.append(",");

        }
        sb.append(getRuleString(r));
        isFirst = false;
      }
      return sb.toString();
    }
    if (matcher instanceof NotMatcher) {
      return "!" + getRuleString(((NotMatcher<T>) matcher).getNested());
    }
    if (matcher instanceof AndMatcher) {
      AndMatcher<T> m = (AndMatcher<T>) matcher;
      return "(" + getRuleString(m.getLeftMatcher()) + ") && (" + getRuleString(m.getRightMatcher()) + ")";
    }
    if (matcher instanceof OrMatcher) {
      OrMatcher<T> m = (OrMatcher<T>) matcher;
      return "(" + getRuleString(m.getLeftMatcher()) + ") || (" + getRuleString(m.getRightMatcher()) + ")";
    }
    if (matcher instanceof TreeStateMatcher) {
      TreeStateMatcher<T> m = (TreeStateMatcher<T>) matcher;
      String sign = "+";
      if (m.isValue() == false)
        sign = "-";
      return sign + "(" + getRuleString(m.getNested()) + ")";
    }
    return TextSplitterUtils.escape(elementFactory.getRuleString(matcher), escapeChar, '&', ',', '|', '!', '(', ')', '+', '-');
  }

  public char getEscapeChar()
  {
    return escapeChar;
  }

  public void setEscapeChar(char escapeChar)
  {
    this.escapeChar = escapeChar;
  }

  public MatcherFactory<T> getElementFactory()
  {
    return elementFactory;
  }

  public void setElementFactory(MatcherFactory<T> elementFactory)
  {
    this.elementFactory = elementFactory;
  }

}
