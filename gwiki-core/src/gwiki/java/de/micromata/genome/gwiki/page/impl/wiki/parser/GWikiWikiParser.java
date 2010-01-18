/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   22.12.2009
// Copyright Micromata 22.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiContent;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragementLink;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBr;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBrInLine;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentFixedFont;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHeading;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHr;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentImage;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLi;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentList;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentP;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentParseError;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentTable;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentTextDeco;

public class GWikiWikiParser
{

  protected void parseLi(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    //
    // * x -> * x
    // ** z -> * z
    // 
    char tk = tks.curToken();
    String tag = tks.getStringUntilNotOneOf("-*#");

    GWikiFragment lfrag = ctx.lastFragment();
    GWikiFragmentList listfrag = new GWikiFragmentList(tag);
    if (lfrag instanceof GWikiFragmentList) {
      GWikiFragmentList pl = (GWikiFragmentList) lfrag;
      if (pl.sameType(listfrag) == true) {
        listfrag = (GWikiFragmentList) lfrag;
      } else {
        if (tag.startsWith(pl.getListTag()) == true) {
          pl.addChild(listfrag);
        } else {
          ctx.addFragment(listfrag);
        }
      }

    } else {
      ctx.addFragment(listfrag);
    }
    ctx.pushFragList();
    tk = tks.nextToken(); // *
    tk = tks.skipWs();
    tks.addStopToken('\n');
    parseLine(tks, ctx);
    List<GWikiFragment> childs = ctx.popFragList();
    if (childs.size() == 1 && childs.get(0) instanceof GWikiFragmentList) {
      listfrag.addChilds(childs);
    } else {
      listfrag.addChild(new GWikiFragmentLi(listfrag, childs));
    }
    tks.removeStopToken('\n');

  }

  protected boolean isDecorateStart(GWikiWikiTokens tks)
  {
    char pk = tks.peekToken(-1);
    if (pk != 0 && Character.isLetterOrDigit(pk) == true) {
      return false;
    }
    pk = tks.peekToken(1);
    if (pk == 0) {
      return false;
    }
    if (Character.isWhitespace(pk) == true) {
      return false;
    }
    return true;

  }

  protected boolean isDecorateEnd(GWikiWikiTokens tks)
  {
    char pk = tks.peekToken(-1);
    if (pk == 0 || Character.isWhitespace(pk) == true) {
      return false;
    }
    pk = tks.peekToken(1);
    if (pk == 0) {
      return true;
    }
    if (Character.isLetterOrDigit(pk) == true) {
      return false;
    }
    return true;

  }

  protected String getWordDecorator(char decChar)
  {
    switch (decChar) {
      case '*':
        return "b";
      case '_':
        return "em";
      case '-':
        return "del";
      case '~':
        return "sub";
      case '^':
        return "sup";
      case '+':
        return "u";
      default:
        return "";
    }
  }

  protected void parseDecoratedWord(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    char decChar = tks.curToken();
    char pk = tks.peekToken(-1);
    if (isDecorateStart(tks) == false) {
      ctx.addTextFragement(tks.curTokenString());
      if (tks.hasNext() == true) {
        tks.nextToken();
        parseWord(tks, ctx);
      } else {
        tks.nextToken();
      }
      return;
    }
    // 
    int ltkpos = tks.getTokenPos();
    tks.nextToken();
    char prevDecChar = tks.addStopToken(decChar);
    List<GWikiFragment> nestedList = new ArrayList<GWikiFragment>();
    do {
      ctx.pushFragList();
      parseWords(tks, ctx);
      List<GWikiFragment> nested = ctx.popFragList();
      nestedList.addAll(nested);
      if (prevDecChar == 0) {
        tks.removeStopToken(decChar);
      }

      char ct = tks.curToken();
      if (ct == '\n' || ct == 0) {
        ctx.addTextFragement(Character.toString(decChar));
        tks.setTokenPos(ltkpos + 1);
        return;
      }
      // char ct = tks.nextToken();
      if (ct == decChar && isDecorateEnd(tks) == true) {
        String tag = getWordDecorator(decChar);
        GWikiFragmentTextDeco frag = new GWikiFragmentTextDeco(decChar, "<" + tag + ">", "</" + tag + ">", nestedList);
        ctx.addFragment(frag);
        tks.nextToken();
        return;
      }
      if (tks.hasNext() == false) {
        ctx.addTextFragement(Character.toString(decChar));
        tks.setTokenPos(ltkpos + 1);
        return;
      }
    } while (true);
  }

  protected GWikiMacroFragment parseMacroHead(MacroAttributes ma, GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {

    // {macroName}
    // {macroName:defaultArg}
    // {macroName:arg=value} //
    // {macroName:arg=value|arg2=value2}
    GWikiMacroFragment mf = ctx.createMacro(ma);
    char tk = tks.curToken();
    if (tk == '}') {
      tks.nextToken();
      return mf;
    }
    if (tk != ':') {
      return null;// TODO error

    }
    tk = tks.nextToken();
    do {
      tk = tks.curToken();
      String label = tks.getStringUntilOneOf("|}=");
      tk = tks.curToken();
      if (tk == '}' || tk == '|') {
        ma.getArgs().setStringValue(MacroAttributes.DEFAULT_VALUE_KEY, label);
        tks.nextToken();
        if (tk == '}') {
          return mf;
        }

      } else if (tk == '=') {
        tk = tks.nextToken();
        String val;
        if (tk == '"') {
          tks.nextToken();
          val = tks.parseStringQuoted();
        } else {
          val = tks.getStringUntilOneOf("|}");
        }
        ma.getArgs().setStringValue(label, val);
      }
      tk = tks.curToken();
      if (tk == '}') {
        tks.nextToken();
        return mf;
      } else if (tk == '|') {
        tks.nextToken();
      } else {
        // TODO error
      }
    } while (tks.eof() == false);
    return null;
  }

  protected List<GWikiFragment> removeWsTokensFromEnd(List<GWikiFragment> childs)
  {
    for (int i = childs.size() - 1; i >= 0; --i) {
      GWikiFragment frag = childs.get(i);
      if (frag instanceof GWikiFragmentP || frag instanceof GWikiFragmentBr) {
        childs.remove(i);
        continue;
      }
      if (frag instanceof GWikiFragmentText) {
        GWikiFragmentText t = (GWikiFragmentText) frag;
        String s = t.getSource();
        if (StringUtils.isBlank(s) == true) {
          childs.remove(i);
          continue;
        }
      }
      break;
    }
    return childs;
  }

  protected void parseMacro(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    int curTokePos = tks.getTokenPos();
    char tk = tks.nextToken();
    String macroName = tks.curTokenString();
    MacroAttributes ma = new MacroAttributes(macroName);
    tk = tks.nextToken();
    // tks.nextToken();
    GWikiMacroFragment frag = parseMacroHead(ma, tks, ctx);
    if (frag == null) {
      ctx.addTextFragement("{");
      tks.setTokenPos(curTokePos + 1);
      return;
    }
    if (frag.getMacro().hasBody() == false) {
      if (frag.getMacro() instanceof GWikiCompileTimeMacro) {
        Collection<GWikiFragment> nfrags = ((GWikiCompileTimeMacro) frag.getMacro()).getFragments(frag, tks, ctx);
        ctx.addFragments(nfrags);
      } else if (frag.getMacro() instanceof GWikiRuntimeMacro) {
        ctx.addFragment(frag);
      } else {
        ctx.addFragment(new GWikiFragmentParseError("Macro is neither Compile nor Runtime Macro: " + frag.getMacro().getClass().getName()));
      }
      return;
    }
    tk = tks.curToken();
    int tkn = (int) tk;
    int startToken = tks.getTokenPos();
    if (GWikiMacroRenderFlags.TrimTextContent.isSet(frag.getMacro().getRenderModes()) == true) {
      tk = tks.skipWsNl();

    }
    if (frag.getMacro().evalBody() == true) {
      ctx.pushFragList();
      do {
        tks.addStopToken('{');
        parseBody(tks, ctx);
        tk = tks.curToken(true);
        if (tk == '{') {
          if (tks.peekToken(1, true) == '{') {
            tks.removeStopToken('{');
            parseWords(tks, ctx);
            tks.addStopToken('{');
            continue;
          }
          int tkss = tks.getTokenPos();
          tks.nextToken();
          String nmacroName = tks.curTokenString();
          if (nmacroName.equals(macroName) == false) {
            tks.setTokenPos(tkss);
            tks.removeStopToken('{');
            parseMacro(tks, ctx);
            continue;
          }
          tks.nextToken();
          MacroAttributes nma = new MacroAttributes(nmacroName);
          GWikiMacroFragment nmf = parseMacroHead(nma, tks, ctx);
          if (nmf.getAttrs().getArgs().isEmpty() == true) {
            tks.removeStopToken('{');
            break;
          }
          tks.setTokenPos(tkss);
          tks.removeStopToken('{');
          parseMacro(tks, ctx);
          continue;
        } else {
          String source = frag.getSource();
          ctx.addFragment(new GWikiFragmentParseError("Missing macro end for  " + frag.getMacro().getClass().getName() + "; " + source));
          return;
        }
      } while (true);

      // GWikiWikiTokens tnks = new GWikiWikiTokens(tks, endToken);
      // tnks.setTokenPos(tnks.getTokenPos() - 1);
      // parseText(tnks, ctx);
      List<GWikiFragment> childs = ctx.popFragList();
      if (GWikiMacroRenderFlags.TrimTextContent.isSet(frag.getMacro().getRenderModes()) == true) {
        childs = removeWsTokensFromEnd(childs);
      }
      frag.addChilds(childs);
      ma.setChildFragment(new GWikiFragmentChildContainer(frag.getChilds()));
    } else {
      int endToken = tks.findToken("{", frag.getAttrs().getCmd(), "}");
      if (endToken == -1) {
        ctx
            .addFragment(new GWikiFragmentParseError("Missing macro end for  " + frag.getMacro().getClass().getName() + "; " + ma.getBody()));
        return;
      }
      String body = tks.getTokenString(startToken, endToken);
      if (GWikiMacroRenderFlags.TrimTextContent.isSet(frag.getMacro().getRenderModes()) == true) {
        body = StringUtils.trim(body);
      }
      frag.getAttrs().setBody(body);
      tks.setTokenPos(endToken + 3);
    }
    if (frag.getMacro() instanceof GWikiCompileTimeMacro) {
      ctx.addFragments(((GWikiCompileTimeMacro) frag.getMacro()).getFragments(frag, tks, ctx));
    } else if (frag.getMacro() instanceof GWikiRuntimeMacro) {
      ctx.addFragment(frag);
    } else {
      ctx.addFragment(new GWikiFragmentParseError("Macro is neither Compile nor Runtime Macro: " + frag.getMacro().getClass().getName()));
    }

  }

  protected void parseFixedFontDecorator(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    tks.nextToken();
    int oldPos = tks.getTokenPos();
    List<GWikiFragment> nested = parseWordsUntil(tks, ctx, "}");

    if (tks.curToken(true) != '}' || tks.peekToken(1, true) != '}') {
      ctx.addTextFragement("{{");
      tks.setTokenPos(oldPos);
      return;
    }
    GWikiFragmentFixedFont frag = new GWikiFragmentFixedFont(nested);
    ctx.addFragment(frag);
    tks.nextToken();
    tks.nextToken();
  }

  /**
   * parse text until one of the given characters.
   * 
   * curToken is either one of given character or eof.
   * 
   * @param oneOf
   * @return
   */
  public List<GWikiFragment> parseWordsUntil(GWikiWikiTokens tks, GWikiWikiParserContext ctx, String oneOf)
  {
    return parseWordsUntil(tks, ctx, oneOf.toCharArray());
  }

  public List<GWikiFragment> parseWordsUntil(GWikiWikiTokens tks, GWikiWikiParserContext ctx, char[] oneOf)
  {
    char[] prevStopWords = new char[oneOf.length];
    for (int i = 0; i < oneOf.length; ++i) {
      prevStopWords[i] = tks.addStopToken(oneOf[i]);
    }
    ctx.pushFragList();
    parseWords(tks, ctx);
    List<GWikiFragment> ret = ctx.popFragList();
    for (int i = 0; i < oneOf.length; ++i) {
      if (prevStopWords[i] == 0) {
        tks.removeStopToken(oneOf[i]);
      }
    }
    return ret;
  }

  protected void parseLink(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    int oldPos = tks.getTokenPos();
    tks.nextToken();
    char barStop = tks.removeStopToken('|');
    List<GWikiFragment> frags = parseWordsUntil(tks, ctx, "|]\n");
    List<GWikiFragment> titelFrags = null;

    char ct = tks.curToken();
    if (ct == '|') {
      titelFrags = frags;
      tks.nextToken();
      frags = parseWordsUntil(tks, ctx, "|]\n");
      ct = tks.curToken();
    }
    if (ct != ']') {
      tks.setTokenPos(oldPos);
      ctx.addTextFragement("[");
      tks.nextToken();
      tks.addStopToken(barStop);
      return;
    }
    tks.nextToken();
    if (frags.size() != 1 || (frags.get(0) instanceof GWikiFragmentText) == false) {
      ctx.addFragment(new GWikiFragmentParseError("expect  text as link target"));
      tks.addStopToken(barStop);
      return;
    }
    String target = ((GWikiFragmentText) frags.get(0)).getSource();
    GWikiFragementLink link = new GWikiFragementLink(target);
    if (titelFrags != null) {
      link.addChilds(titelFrags);
    }
    ctx.addFragment(link);
    tks.addStopToken(barStop);
  }

  protected void parseImage(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    char tk = tks.nextToken();
    int oldPos = tks.getTokenPos();
    // tks.addStopToken('!');
    String link = tks.getStringUntilOneOf("!\n", true);
    tk = tks.curToken();
    if (link == null || tk != '!') {
      ctx.addTextFragement("!");
      tks.setTokenPos(oldPos);
      return;
    }
    tk = tks.nextToken();
    GWikiFragmentImage img = new GWikiFragmentImage(link);
    ctx.addFragment(img);
  }

  protected void parseWord(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    char tk = tks.curToken();
    switch (tk) {
      case 0:
        return;
      case '+':
      case '^':
      case '~':
      case '*':
      case '_':
      case '-': {
        parseDecoratedWord(tks, ctx);
        break;
      }
      case '\\': {
        char nt = tks.nextTokenIgnoreStops();
        if (nt == '\\' && tks.peekToken(1, true) == '\n') {
          ctx.addFragment(new GWikiFragmentBrInLine());
          tk = tks.nextTokenIgnoreStops();
          tk = tks.nextTokenIgnoreStops();
        } else {
          ctx.addTextFragement(tks.curTokenString(true));
          tk = tks.nextToken();
        }

        break;
      }
      case '[':
        parseLink(tks, ctx);
        break;
      case '!':
        parseImage(tks, ctx);
        break;
      case '{':
        if (tks.peekToken(1) == '{') {
          tk = tks.nextToken();
          parseFixedFontDecorator(tks, ctx);
        } else {
          parseMacro(tks, ctx);
        }
        break;
      default:
        ctx.addTextFragement(tks.curTokenString());
        tk = tks.nextToken();
        break;
    }
  }

  protected void parseWords(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    char tk = tks.curToken();
    if (tk == '\n') {
      return;
    }
    do {
      parseWord(tks, ctx);
      tk = tks.curToken();
      if (tk == '\n') {
        break;
      }
      // if (tks.hasNext() == false) {
      // return;
      // }
      //
      // tks.nextToken();
    } while (tk != 0);
  }

  protected boolean isSentenceTerminator(char c)
  {
    return ".;!?:".indexOf(c) != -1;
  }

  protected void parseLineText(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    do {
      int prePos = tks.getTokenPos();
      parseWords(tks, ctx);
      int epos = tks.getTokenPos();
      if (tks.eof() == true || tks.hasNext() == false || tks.peekToken(0) == '\n') {
        break;
      }
      if (prePos == epos) {
        break;
      }
    } while (true);
    char preToken = tks.peekToken(-1);
    char curToken = tks.curToken();
    char nextToken = tks.peekToken(1);

    if (nextToken == '\n') {
      nextToken = tks.nextToken();
      ctx.addFragment(new GWikiFragmentP());
    } else if (isSentenceTerminator(preToken)) {
      ctx.addFragment(new GWikiFragmentBr());
    } else if (curToken == '\n') {
      if (tks.peekToken(-1) != '\\') {
        ctx.addFragment(new GWikiFragmentBr());
      }
    }

  }

  protected void parseLineHeadingText(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    char tk = tks.curToken();
    String l = tks.curTokenString();
    if (l.length() == 2 && l.charAt(0) == 'h' && Character.isDigit(l.charAt(1)) == true) {
      if (tks.peekToken(1) == '.') {
        GWikiFragmentHeading hf = new GWikiFragmentHeading(Integer.valueOf(l.substring(1, 2)), "");
        tk = tks.nextToken();
        tk = tks.nextToken();
        tk = tks.skipWs();
        ctx.pushFragList();
        parseWords(tks, ctx);
        hf.addChilds(ctx.popFragList());
        ctx.addFragment(hf);
        ctx.addTextFragement("\n");
        return;
      }
    }
    parseLineText(tks, ctx);
  }

  public GWikiFragmentTable.Cell parseTableCell(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    String style = "td";
    if (tks.peekToken(1, true) == '|') {
      tks.nextToken();
      style = "th";
    }
    char tk = tks.nextToken();
    if (tk == '\n' || tk == 0) {
      if (tk == '\n') {
        tks.nextToken();
      }
      return null;
    }

    ctx.pushFragList();
    tks.addStopToken('|');
    tks.addStopToken('\n');
    parseLiLine(tks, ctx);
    tks.removeStopToken('|');
    tks.removeStopToken('\n');
    GWikiFragmentChildContainer ct = new GWikiFragmentChildContainer(ctx.popFragList());
    return new GWikiFragmentTable.Cell(style, ct);
    // return new Pair<String, GWikiFragmentChildContainer>(style, ct);
  }

  public GWikiFragmentTable.Row parseTableLine(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    GWikiFragmentTable.Row row = new GWikiFragmentTable.Row();

    do {
      GWikiFragmentTable.Cell cell = parseTableCell(tks, ctx);
      if (cell == null) {
        return row;
      }
      row.addCell(cell);
      char tk = tks.curToken();
      if (tk == '\n') {
        tks.nextToken();
        return row;
      }
    } while (tks.eof() == false);
    return null;
  }

  public void parseTable(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    char tk;
    int startToken = tks.getTokenPos();
    GWikiFragmentTable table = new GWikiFragmentTable();
    // List<List<Pair<String, GWikiFragmentChildContainer>>> tbl = new ArrayList<List<Pair<String, GWikiFragmentChildContainer>>>();
    do {
      GWikiFragmentTable.Row tb = parseTableLine(tks, ctx);
      if (tb == null) {
        if (table.getRowSize() == 0) {
          tks.setTokenPos(startToken);
          tk = tks.curToken();
          ctx.addTextFragement(tks.curTokenString());
          tk = tks.nextToken();
          if (tk == '|') {
            ctx.addTextFragement(tks.curTokenString());
            tk = tks.nextToken();
          }
          parseLine(tks, ctx);
          return;
        }
        break;
      }
      table.addRow(tb);
      tk = tks.curToken();
      if (tk != '|') {
        break;
      }
    } while (tks.eof() == false);
    // table.setTable(tbl);
    ctx.addFragment(table);

  }

  public void parseLiLine(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    char ct = tks.curToken();
    switch (ct) {
      case '#':
      case '*':
      case '-': {
        char lookahead = tks.peekToken(1);
        int oldPos = tks.getTokenPos();
        String l = tks.getStringUntilOneOf("\n", true);
        if ("----".equals(l) == true) {
          ctx.addFragment(new GWikiFragmentHr());
          // ct = tks.nextToken();
          return;
        } else {
          tks.setTokenPos(oldPos);
        }
        if (Character.isWhitespace(lookahead) == true || "#*-".indexOf(lookahead) != -1) {
          parseLi(tks, ctx);
        } else {
          parseLineHeadingText(tks, ctx);
        }
        return;
      }
      default:
        parseLineHeadingText(tks, ctx);
        break;

    }
  }

  public void parseLine(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    char ct = tks.curToken();
    switch (ct) {
      case '\n': {
        char nt = tks.peekToken(1);
        if (nt == '\n') {
          tks.nextToken();
          ctx.addFragment(new GWikiFragmentP());
        } else {
          ctx.addFragment(new GWikiFragmentBr());
        }

        break;
      }
      case '|':
        parseTable(tks, ctx);
        break;
      default:
        parseLiLine(tks, ctx);
        break;
    }
  }

  public GWikiContent parse(GWikiContext wikiContext, String text)
  {
    GWikiWikiParserContext parseContext = new GWikiWikiParserContext();
    parseContext.getMacroFactories().putAll(wikiContext.getWikiWeb().getWikiConfig().getWikiMacros(wikiContext));
    String ntext = StringUtils.replace(text, "\n\r", "\n");
    ntext = StringUtils.replace(ntext, "\r\n", "\n");
    parseFrags(ntext, parseContext);
    return new GWikiContent(parseContext.popFragList());

  }

  /**
   * Parse body. Different to parseText no nextToken will be called initialilly
   * 
   * @param tks
   * @param ctx
   */
  public void parseBody(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    while (tks.eof() == false) {
      parseLine(tks, ctx);
      if (tks.eof() == true) {
        break;
      }
      tks.nextToken();
    }
  }

  public void parseText(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    while (tks.hasNext() == true) {
      tks.nextToken();
      parseLine(tks, ctx);
      if (tks.eof() == true) {
        break;
      }
    }
  }

  public void parseFrags(String text, GWikiWikiParserContext ctx)
  {
    ctx.pushFragList();
    String delimiter = "\n \t \\-*_|{}=#+^~[]!.:?;,\"";
    GWikiWikiTokens tks = new GWikiWikiTokens(delimiter, text);
    parseText(tks, ctx);
  }
}
