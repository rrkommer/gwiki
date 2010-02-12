////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

package de.micromata.genome.gwiki.page.search.expr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.utils.ClassUtils;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.EqualsMatcher;
import de.micromata.genome.util.matcher.InvalidMatcherGrammar;
import de.micromata.genome.util.matcher.LessThanMatcher;
import de.micromata.genome.util.matcher.LessThanOrEqualMatcher;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.MoreThanMatcher;
import de.micromata.genome.util.matcher.MoreThanOrEqualMatcher;
import de.micromata.genome.util.matcher.NotMatcher;
import de.micromata.genome.util.matcher.string.ContainsIgnoreCaseMatcher;
import de.micromata.genome.util.matcher.string.EqualsWithBoolMatcher;
import de.micromata.genome.util.text.RegExpToken;
import de.micromata.genome.util.text.TextSplitterUtils;
import de.micromata.genome.util.text.Token;
import de.micromata.genome.util.text.TokenResult;

public class SearchExpressionParser
{
  public static final int TK_UNMATCHED = 0;

  public static final int TK_SPACE = 1;

  public static final int TK_BO = 2;

  public static final int TK_BC = 3;

  public static final int TK_AND = 4;

  public static final int TK_OR = 5;

  public static final int TK_PLUS = 6;

  public static final int TK_MINUS = 7;

  public static final int TK_HASH = 8;

  public static final int TK_NOT = 9;

  public static final int TK_COMMAND = 10;

  public static final int TK_QUOTE = 11;

  public static final int TK_ORDERBY = 12;

  public static final int TK_COMMA = 13;

  public static final int TK_CONTAINING = 14;

  public static final int TK_EQUALS = 15;

  public static final int TK_NOTEQUAL = 16;

  public static final int TK_LESS = 17;

  public static final int TK_LESSOREQUAL = 18;

  public static final int TK_MORE = 19;

  public static final int TK_MOREOREQUAL = 20;

  public static final int TK_LIKE = 21;

  public static final int TK_BOBC_PRIO = 3;

  public static final int TK_COMMA_PRIO = 1;

  public static final int TK_ANDOR_PRIO = 2;

  private static String afterandOr = "[ \\(\\t]+";

  private static final Token[] DefaultToken = new Token[] { //

  new RegExpToken(TK_BO, "(\\()(.*)"), //
      new RegExpToken(TK_BC, "(\\))(.*)"), //
      new RegExpToken(TK_QUOTE, "^[ \\t]*\\\"(.*?)\\\"(.*)"), //
      new RegExpToken(TK_CONTAINING, "(\\~)(.*)"), //
      new RegExpToken(TK_AND, "(\\&\\&)(.*)"), //
      new RegExpToken(TK_AND, "(and)(" + afterandOr + ".*)"), //
      new RegExpToken(TK_OR, "(\\|\\|)(" + afterandOr + ".*)"), //
      new RegExpToken(TK_OR, "(or)(" + afterandOr + ".*)"), //
      new RegExpToken(TK_PLUS, "(\\+)([ \\t]+.*)"), //
      new RegExpToken(TK_MINUS, "(\\-)([ \\t]+.*)"), //
      new RegExpToken(TK_MOREOREQUAL, "(\\>\\=)(.*)"), //
      new RegExpToken(TK_LESSOREQUAL, "(\\<\\=)(.*)"), //
      new RegExpToken(TK_NOTEQUAL, "(\\!\\=)(.*)"), //
      new RegExpToken(TK_EQUALS, "(\\=)(.*)"), //
      new RegExpToken(TK_LESS, "(\\<)(.*)"), //
      new RegExpToken(TK_MORE, "(\\>)(.*)"), //
      new RegExpToken(TK_LIKE, "(like)(.*)"), //

      new RegExpToken(TK_NOT, "(\\!)([ \\t]+.*)"), //
      new RegExpToken(TK_NOT, "(not)(" + afterandOr + ".*)"), //

      new RegExpToken(TK_ORDERBY, "^+(order by)(" + afterandOr + ".*)"), //
      new RegExpToken(TK_COMMAND, "^[ \\t]*(\\:)(.*)"), //

      new RegExpToken(TK_COMMA, "^[ \\t]*(\\,)[ \\t]*(.*)"), //
      new RegExpToken(TK_SPACE, "^[ \\t]*([ ])[ \\t]*(.*)"), //

  };

  private static final Map<String, Class< ? extends SearchExpressionCommand>> buildInCommandExpressions = new HashMap<String, Class< ? extends SearchExpressionCommand>>();
  static {
    buildInCommandExpressions.put("parentpageid", SearchExpressionCommandParentPageId.class);
    buildInCommandExpressions.put("childs", SearchExpressionComandChilds.class);
    buildInCommandExpressions.put("space", SearchExpressionComandWikiSpace.class);
    buildInCommandExpressions.put("pageid", SearchExpressionComandPageIdMatcher.class);
    buildInCommandExpressions.put("prop", SearchExpressionPropSelektorCommand.class);
  }

  public static class TokenResultList
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

    private boolean contains(int search, int... tks)
    {
      for (int i : tks) {
        if (search == i)
          return true;
      }
      return false;
    }

    public void skipping(int... tks)
    {
      while (position < tokenResults.size() && contains(tokenResults.get(position).getTokenType(), tks) == true) {
        ++position;
      }
    }

    public TokenResult nextTokenSkipping(int... tks)
    {
      ++position;
      while (position < tokenResults.size() && contains(tokenResults.get(position).getTokenType(), tks) == true) {
        ++position;
      }
      if (position >= tokenResults.size())
        return null;
      return tokenResults.get(position);
    }

    /**
     * return -1 if eof
     * 
     * @param pos
     * @return
     */
    public int lookAheadTokenType(int pos)
    {
      if (position + pos >= tokenResults.size())
        return -1;
      return tokenResults.get(position + pos).getTokenType();
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

  private char escapeChar = '\\';

  private Map<String, Class< ? extends SearchExpressionCommand>> commandExpressions = new HashMap<String, Class< ? extends SearchExpressionCommand>>();

  public SearchExpressionParser()
  {
    commandExpressions.putAll(buildInCommandExpressions);
  }

  protected SearchExpression consumeElement(String text)
  {
    if (text.startsWith("{") == true && text.endsWith("}") == true) {

    }
    return new SearchExpressionTextContains(text);
  }

  protected SearchExpression consumeListElement(TokenResultList tokens)
  {
    if (tokens.eof() == true)
      return null;
    TokenResult tk = tokens.curToken();
    if (tk.getTokenType() != TK_UNMATCHED && tk.getTokenType() != TK_QUOTE) {
      return null;
      // throw new InvalidMatcherGrammar("Excepting element. Got: " + tk.getConsumed() + "; pattern: " + tokens.pattern);
    }
    String elText;

    // TODO ggf. problematisch, wenn intern auch escaped wird
    elText = TextSplitterUtils.unescape(tk.getConsumed(), escapeChar);

    SearchExpression m = consumeElement(elText);
    tokens.nextTokenSkipping(TK_SPACE);
    return m;
  }

  protected SearchExpression consumeCommand(TokenResultList tks)
  {
    if (tks.eof() == true)
      return null;
    SearchExpression left = consumeListElement(tks);
    if (tks.eof() == true)
      return left;
    TokenResult tk = tks.curToken();
    if (tk.getTokenType() != TK_COMMAND) {
      return left;
    }
    if ((left instanceof SearchExpressionTextContains) == false) {
      throw new InvalidMatcherGrammar("Excepting command string. Got: " + tk.getConsumed() + "; pattern: " + tks.pattern);
    }
    String command = ((SearchExpressionTextContains) left).getText();
    command = command.toLowerCase();
    if (commandExpressions.containsKey(command) == false) {
      tk = tks.nextToken();

      SearchExpression right = new SearchExpressionTextContains(command.toUpperCase());
      SearchExpression ret = ClassUtils.createInstance(SearchExpressionPropSelektorCommand.class, new Class[] { String.class,
          SearchExpression.class}, "prop", right);
      tks.skipping(TK_SPACE);
      return ret;
    } else {
      tks.nextTokenSkipping(TK_SPACE);
      SearchExpression right = consumeListElement(tks);
      SearchExpression ret = ClassUtils.createInstance(commandExpressions.get(command),
          new Class[] { String.class, SearchExpression.class}, command, right);
      return ret;
    }
  }

  protected SearchExpression consumePlusMinus(TokenResultList tokens)
  {
    if (tokens.eof() == true)
      return null;
    TokenResult tk = tokens.curToken();
    if (tk.getTokenType() == TK_NOT) {
      tokens.nextTokenSkipping(TK_SPACE);
      SearchExpression nt = consumeList(tokens);
      return new SearchExpressionNotIn(nt);
    }
    if (tk.getTokenType() == TK_MINUS) {
      tokens.nextTokenSkipping(TK_SPACE);
      SearchExpression n = consumeCommand(tokens);
      return new SearchExpressionNotIn(n);
    }
    if (tk.getTokenType() == TK_PLUS) {
      tokens.nextTokenSkipping(TK_SPACE);
      SearchExpression n = consumeCommand(tokens);
      return new SearchExpressionExact(n);

    }
    return consumeCommand(tokens);
  }

  protected SearchExpression consumeBracket(TokenResultList tks)
  {
    if (tks.eof() == true)
      return null;
    TokenResult tk = tks.curToken();
    if (tk.getTokenType() == TK_BO) {
      tks.nextTokenSkipping(TK_SPACE);
      SearchExpression m = consume(tks);
      tk = tks.curToken();
      if (tk.getTokenType() != TK_BC) {
        throw new InvalidMatcherGrammar("grammar has no matching close bracket: " + tks.pattern);
      }
      tks.nextTokenSkipping(TK_SPACE);
      return m;
    }
    return consumePlusMinus(tks);
  }

  protected SearchExpression consumeList(TokenResultList tks)
  {

    SearchExpression left = consumeBracket(tks);
    if (left == null)
      return null;
    if (tks.eof() == true)
      return left;
    List<SearchExpression> elements = new ArrayList<SearchExpression>();
    elements.add(left);

    do {
      // TokenResult tk = tks.curToken();
      // if (tk.)
      // if (tk.getTokenType() != TK_SPACE)
      // break;
      // tks.nextTokenSkipping(TK_SPACE);
      left = consumeBracket(tks);
      if (left == null) {
        break;
      }
      elements.add(left);
    } while (left != null && tks.eof() == false);
    if (elements.size() == 1)
      return elements.get(0);
    return new SearchExpressionWeakOrList(elements);
  }

  protected Matcher<String> createComparator(int tktype, String text)
  {
    switch (tktype) {
      case TK_CONTAINING:
        return new ContainsIgnoreCaseMatcher<String>(text);
      case TK_EQUALS:
        return new EqualsWithBoolMatcher(text);
      case TK_LESS:
        return new LessThanMatcher<String>(text);
      case TK_MORE:
        return new MoreThanMatcher<String>(text);
      case TK_MOREOREQUAL:
        return new MoreThanOrEqualMatcher<String>(text);
      case TK_LESSOREQUAL:
        return new LessThanOrEqualMatcher<String>(text);
      case TK_NOTEQUAL:
        return new NotMatcher<String>(new EqualsMatcher<String>(text));
      case TK_LIKE:
        return new BooleanListRulesFactory<String>().createMatcher(text);
      default:
        throw new InvalidMatcherGrammar("Unkown comparator");
    }
  }

  protected SearchExpression consumeCompare(TokenResultList tks)
  {
    SearchExpression left = consumeList(tks);
    if (tks.eof() == true || left == null) {
      return left;
    }
    TokenResult tk = tks.curToken();
    SearchExpression rex;
    int cmptk = tk.getTokenType();
    switch (cmptk) {
      case TK_CONTAINING:
      case TK_EQUALS:
      case TK_LESS:
      case TK_MORE:
      case TK_MOREOREQUAL:
      case TK_LESSOREQUAL:
      case TK_NOTEQUAL:
      case TK_LIKE: {
        tks.nextTokenSkipping(TK_SPACE);
        rex = consumeListElement(tks);
        if (rex == null) {
          throw new InvalidMatcherGrammar("missing right comparator argument");
        }
        if ((left instanceof SearchExpressionFieldSelektor) == false) {
          throw new InvalidMatcherGrammar("expect field selector on left side of comparator. got: " + left.toString());
        }
        if ((rex instanceof SearchExpressionText) == false) {
          throw new InvalidMatcherGrammar("expect field selector on right side of comparator. got: " + rex.toString());
        }

        Matcher<String> mt = createComparator(cmptk, ((SearchExpressionText) rex).getText());

        SearchExpressionComparator comp = new SearchExpressionComparator(mt, (SearchExpressionFieldSelektor) left,
            (SearchExpressionText) rex);
        return comp;
      }
    }
    return left;
  }

  protected SearchExpression consumeAndOr(TokenResultList tks)
  {
    SearchExpression left = consumeCompare(tks);
    if (left == null)
      return left;
    if (tks.eof() == true)
      return left;
    TokenResult tk = tks.curToken();
    if (tk.getTokenType() == TK_AND || tk.getTokenType() == TK_OR) {
      tks.nextTokenSkipping(TK_SPACE);
      SearchExpression right = consumeAndOr(tks);
      if (right == null) {
        throw new InvalidMatcherGrammar("Missing right express of <expr>[&&||\\|]<expr>: "
            + tks.pattern
            + "; rest: "
            + tks.restOfTokenString());
      }
      if (tk.getTokenType() == TK_AND)
        return new SearchExpressionAnd(left, right);
      return new SearchExpressionOr(left, right);
    }
    return left;
  }

  protected SearchResultComparatorBase consumeThisComparator(TokenResultList tks)
  {
    SearchExpression se = consumeCommand(tks);
    if ((se instanceof SearchExpressionFieldSelektor) == false) {
      throw new InvalidMatcherGrammar("Need text selector. Got: " + se);
    }
    SearchExpressionFieldSelektor fs = (SearchExpressionFieldSelektor) se;
    if (se instanceof SearchExpressionTextContains) {
      String criteria = ((SearchExpressionTextContains) se).getText();
      if (StringUtils.equalsIgnoreCase(criteria, "relevance") == true) {
        return new SearchResultComparatorRelevance();
      }
      throw new InvalidMatcherGrammar("Unknown order criteria: " + criteria);
    } else {
      return new SearchResultComparatorField(fs);
    }
  }

  protected SearchResultComparatorBase createComparator(TokenResultList tks, SearchResultComparatorBase last)
  {
    // order by modby, modat, title, page

    SearchResultComparatorBase src = consumeThisComparator(tks);
    tks.skipping(TK_SPACE);
    if (tks.eof() == true) {
      return src;
    }

    if (tks.eof() || tks.curToken().getTokenType() == TK_COMMA) {
      return src;
    }
    if (StringUtils.equalsIgnoreCase(tks.curToken().getConsumed(), "DESC") == true) {
      src.setDesc(true);
      tks.nextTokenSkipping(TK_SPACE);
    } else if (StringUtils.equalsIgnoreCase(tks.curToken().getConsumed(), "ASC") == true) {
      tks.nextTokenSkipping(TK_SPACE);
    } else {
      throw new InvalidMatcherGrammar("expect ',' for the next order expression");
    }
    return src;

    // if (tks.eof() == true)
    // return src;
    // if (tks.curToken().getTokenType() == TK_COMMA) {
    // tks.nextToken();
    // return src;
    // }
    // throw new InvalidMatcherGrammar("expect ',' or end of expression for the next order expression");

  }

  protected SearchExpression consumeOrderBy(TokenResultList tks)
  {
    SearchExpression ex = consumeAndOr(tks);
    if (tks.eof() == true) {
      return ex;
    }
    TokenResult tk = tks.curToken();
    if (tk.getTokenType() != TK_ORDERBY)
      return ex;
    tks.nextTokenSkipping(TK_SPACE);
    if (tks.eof() == true) {
      throw new InvalidMatcherGrammar("order by need criteria");
    }

    SearchExpressionOrderBy order = new SearchExpressionOrderBy(ex);
    SearchResultComparatorBase last = null;

    while (tks.eof() == false) {
      tks.skipping(TK_SPACE, TK_COMMA);
      if (tks.eof() == true)
        break;
      SearchResultComparatorBase c = createComparator(tks, last);
      if (c == null)
        break;
      if (last != null) {
        last.setNextComparator(c);
      } else {
        order.addComparator(c);
      }
      last = c;
      if (tks.eof() == true) {
        break;
      }
      if (tks.lookAheadTokenType(0) == TK_COMMA) {
        tks.nextTokenSkipping(TK_SPACE);
        continue;
      }
      throw new InvalidMatcherGrammar("expect ',' or end of expression for the next order expression");
    }
    return order;
  }

  protected SearchExpression consume(TokenResultList tkl)
  {
    return consumeOrderBy(tkl);
  }

  public SearchExpression parse(String pattern)
  {
    pattern = StringUtils.trim(pattern);
    List<TokenResult> tokenResults = TextSplitterUtils.parseStringTokens(pattern, DefaultToken, escapeChar, true, true);
    TokenResultList tkl = new TokenResultList(tokenResults, 0, pattern);
    SearchExpression ret = consume(tkl);
    if (tkl.eof() == false) {
      throw new InvalidMatcherGrammar("unconsumed tokens. pattern: " + pattern + "; rest: " + tkl.restOfTokenString());
    }
    return ret;
  }

  public Map<String, Class< ? extends SearchExpressionCommand>> getCommandExpressions()
  {
    return commandExpressions;
  }

  public void setCommandExpressions(Map<String, Class< ? extends SearchExpressionCommand>> commandExpressions)
  {
    this.commandExpressions = commandExpressions;
  }

}
