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
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBr;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBrInLine;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentFixedFont;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHeading;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHr;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentImage;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLi;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentList;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentP;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentParseError;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentTable;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentTextDeco;

/**
 * Parser implementation for the GWiki syntax.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWikiParser
{
  protected GWikiFragmentList getNestedLiChild(GWikiFragmentList pl)
  {
    if (pl.getChilds().isEmpty() == true) {
      return pl;
    }
    GWikiFragment lifr = pl.getChilds().get(pl.getChilds().size() - 1);
    if ((lifr instanceof GWikiFragmentLi) == false) {
      return pl;
    }
    GWikiFragmentLi lifrl = (GWikiFragmentLi) lifr;
    if (lifrl.getChilds().isEmpty() == true) {
      return pl;
    }
    GWikiFragment nlc = lifrl.getChilds().get(lifrl.getChilds().size() - 1);
    if (nlc instanceof GWikiFragmentList) {
      return getNestedLiChild((GWikiFragmentList) nlc);
    }
    return pl;
  }

  protected GWikiFragmentList findNestedListChild(GWikiFragmentList pl, String tag)
  {
    if (pl.getListTag().equals(tag) == true) {
      return pl;
    }
    if (pl.getChilds().isEmpty() == true) {
      return null;
    }
    GWikiFragment nlc = pl.getChilds().get(pl.getChilds().size() - 1);
    if ((nlc instanceof GWikiFragmentLi) == false) {
      return null;
    }
    GWikiFragmentLi nli = (GWikiFragmentLi) nlc;
    if (nli.getChilds().isEmpty() == true || (nli.getChilds().get(nli.getChilds().size() - 1) instanceof GWikiFragmentList) == false) {
      return null;
    }
    GWikiFragmentList rn = (GWikiFragmentList) nli.getChilds().get(nli.getChilds().size() - 1);
    return findNestedListChild(rn, tag);
  }

  protected GWikiFragmentList findBestNestedListChildN(GWikiFragmentList pl, String tag)
  {
    if (pl.getChilds().isEmpty() == true) {
      return null;
    }
    GWikiFragment lifr = pl.getChilds().get(pl.getChilds().size() - 1);
    if ((lifr instanceof GWikiFragmentLi) == false) {
      return null;
    }
    GWikiFragmentLi nli = (GWikiFragmentLi) lifr;
    if (nli.getChilds().isEmpty() == true) {
      return null;
    }
    if ((nli.getChilds().get(nli.getChilds().size() - 1) instanceof GWikiFragmentList) == false) {
      return null;
    }
    GWikiFragmentList nlc = (GWikiFragmentList) nli.getChilds().get(nli.getChilds().size() - 1);
    return findBestNestedListChild(nlc, tag);
  }

  protected GWikiFragmentList findBestNestedListChild(GWikiFragmentList pl, String tag)
  {
    GWikiFragmentList ret = findBestNestedListChildN(pl, tag);
    if (ret != null) {
      return ret;
    }
    if (tag.startsWith(pl.getListTag()) == true) {
      return pl;
    }
    return null;
  }

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
    try {
      ctx.pushFragStack(listfrag);
      if (lfrag instanceof GWikiFragmentList) {
        GWikiFragmentList pl = (GWikiFragmentList) lfrag;
        GWikiFragmentList prevlist = findNestedListChild(pl, tag);
        if (prevlist != null) {
          listfrag = prevlist;
        } else {
          prevlist = findBestNestedListChild(pl, tag);
          if (prevlist != null) {
            if (prevlist.getChilds().isEmpty() == false
                && prevlist.getChilds().get(prevlist.getChilds().size() - 1) instanceof GWikiFragmentLi) {
              GWikiFragmentLi lc = (GWikiFragmentLi) prevlist.getChilds().get(prevlist.getChilds().size() - 1);
              lc.addChild(listfrag);
              // prevlist.addChild(listfrag);
            } else {
              prevlist.addChild(listfrag);
            }
          } else {
            pl.addChild(listfrag);
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
      if (childs.size() > 0 && childs.get(childs.size() - 1) instanceof GWikiFragmentBr) {
        childs = childs.subList(0, childs.size() - 1);
      }
      if (childs.size() == 1 && childs.get(0) instanceof GWikiFragmentList) {
        listfrag.addChilds(childs);
      } else {
        listfrag.addChild(new GWikiFragmentLi(listfrag, childs));
      }
      tks.removeStopToken('\n');
    } finally {
      ctx.popFragStack();
    }
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
      if (/* frag instanceof GWikiFragmentP || */frag instanceof GWikiFragmentBr) {
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

  protected static boolean isPAllowedInDom(GWikiWikiParserContext ctx)
  {
    for (GWikiFragment frag : ctx.getFragStack()) {
      if (isParagraphLike(frag) == false) {
        return false;
      }
    }
    return true;
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
    ctx.pushFragList();
    try {
      ctx.pushFragStack(frag);
      if (frag.getMacro().hasBody() == false) {
        ctx.addFragments(ctx.popFragList());
        if (frag.getMacro() instanceof GWikiCompileTimeMacro) {
          Collection<GWikiFragment> nfrags = ((GWikiCompileTimeMacro) frag.getMacro()).getFragments(frag, tks, ctx);
          ctx.addFragments(nfrags);
        } else if (frag.getMacro() instanceof GWikiRuntimeMacro) {
          ctx.addFragment(frag);
        } else {
          ctx.addFragment(new GWikiFragmentParseError("Macro is neither Compile nor Runtime Macro: " + macroName, tks
              .getLineNoFromTokenOffset(curTokePos)));
        }
        return;
      } else if (frag.getMacro() instanceof GWikiCompileTimeMacro) {
        // compile time
        Collection<GWikiFragment> nfrags = ((GWikiCompileTimeMacro) frag.getMacro()).getFragments(frag, tks, ctx);
        ctx.addFragments(nfrags);
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
          parseMacroBody(tks, ctx);
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
            ctx.popFragList();
            String source = frag.getSource();
            ctx.addFragment(new GWikiFragmentParseError("Missing macro end for  " + macroName + "; " + source, tks
                .getLineNoFromTokenOffset(curTokePos)));
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
        if (GWikiMacroRenderFlags.ContainsTextBlock.isSet(frag.getMacro().getRenderModes()) == true && isPAllowedInDom(ctx)) {
          // TODO frisst P
          childs = addWrappedP(childs);
          // childs = wrappBodyWithP(ctx, childs);
        }
        frag.addChilds(childs);
        ma.setChildFragment(new GWikiFragmentChildContainer(frag.getChilds()));
      } else {
        int endToken = tks.findToken("{", frag.getAttrs().getCmd(), "}");
        if (endToken == -1) {
          ctx.popFragList();
          ctx.addFragment(new GWikiFragmentParseError("Missing macro end for  " + macroName, tks.getLineNoFromTokenOffset(curTokePos)));
          return;
        }
        String body = tks.getTokenString(startToken, endToken);
        if (GWikiMacroRenderFlags.TrimTextContent.isSet(frag.getMacro().getRenderModes()) == true) {
          body = StringUtils.trim(body);
        }
        frag.getAttrs().setBody(body);
        tks.setTokenPos(endToken + 3);
      }
      ctx.addFragments(ctx.popFragList());
      if (frag.getMacro() instanceof GWikiCompileTimeMacro) {
        // ctx.addFragments(((GWikiCompileTimeMacro) frag.getMacro()).getFragments(frag, tks, ctx));
      } else if (frag.getMacro() instanceof GWikiRuntimeMacro) {
        ctx.addFragment(frag);
      } else {
        ctx.addFragment(new GWikiFragmentParseError("Macro is neither Compile nor Runtime Macro: " + macroName, tks
            .getLineNoFromTokenOffset(curTokePos)));
      }
    } finally {
      ctx.popFragStack();
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
    String s = tks.getStringUntilOneOf("]");
    if (StringUtils.isEmpty(s) == true) {
      tks.setTokenPos(oldPos);
      tks.addStopToken(barStop);
      return;
    }
    GWikiFragmentLink link = new GWikiFragmentLink(s);
    ctx.addFragment(link);
    tks.nextToken();
    // List<GWikiFragment> fl = parseWordsUntil(tks, ctx, "]");
    // String s = fl.toString();
    //    

    if (false) {

      // List<GWikiFragment> frags = parseWordsUntil(tks, ctx, "|]\n");
      // List<GWikiFragment> titelFrags = null;
      //
      // char ct = tks.curToken();
      // if (ct == '|') {
      // titelFrags = frags;
      // tks.nextToken();
      // frags = parseWordsUntil(tks, ctx, "|]\n");
      // ct = tks.curToken();
      // }
      // if (ct != ']') {
      // tks.setTokenPos(oldPos);
      // ctx.addTextFragement("[");
      // tks.nextToken();
      // tks.addStopToken(barStop);
      // return;
      // }
      // tks.nextToken();
      // if (frags.size() != 1 || (frags.get(0) instanceof GWikiFragmentText) == false) {
      // ctx.addFragment(new GWikiFragmentParseError("Expect text as link target", tks.getLineNoFromTokenOffset(oldPos)));
      // tks.addStopToken(barStop);
      // return;
      // }
      // String target = ((GWikiFragmentText) frags.get(0)).getSource();
      // GWikiFragmentLink link = new GWikiFragmentLink(target);
      // if (titelFrags != null) {
      // link.addChilds(titelFrags);
      // }
      // ctx.addFragment(link);
      // tks.addStopToken(barStop);
    }
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
      boolean inFixedFont = false;
      if (tk == 0) {
        if (tks.curToken(true) == '{' && tks.peekToken(1, true) == '{') {
          inFixedFont = true;
          tks.removeStopToken('{');
        } else {
          break;
        }
      }

      parseWord(tks, ctx);
      if (inFixedFont == true) {
        tks.addStopToken('{');
      }
      tk = tks.curToken();
      if (tk == '\n') {
        break;
      }
    } while (true/* tk != 0 */);
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
      // GWikiFragmentP p = new GWikiFragmentP(ctx.popFragList());
      // ctx.pushFragList();
      // ctx.addFragment(p);

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
        try {
          ctx.pushFragStack(hf);
          tk = tks.nextToken();
          tk = tks.nextToken();
          tk = tks.skipWs();
          ctx.pushFragList();
          parseWords(tks, ctx);
          hf.addChilds(ctx.popFragList());
          ctx.addFragment(hf);
          // ctx.addTextFragement("\n");
          tk = tks.skipWsNl(false);
          if (tk != -1) {
            tks.pushBack();
          }
        } finally {
          ctx.popFragStack();
        }
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

  protected void removeLastEmptyCell(GWikiFragmentTable.Row row)
  {
    if (row.cells.isEmpty() == true) {
      return;
    }
    GWikiFragmentTable.Cell cell = row.cells.get(row.cells.size() - 1);
    if (cell.attributes.getChildFragment() == null) {
      return;
    }
    if (cell.attributes.getChildFragment().getChilds().size() != 1) {
      return;
    }
    GWikiFragment frag = cell.attributes.getChildFragment().getChilds().get(0);
    if ((frag instanceof GWikiFragmentText) == false) {
      return;
    }
    GWikiFragmentText tfrag = (GWikiFragmentText) frag;
    if (StringUtils.isBlank(tfrag.getSource()) == false) {
      return;
    }
    row.cells.remove(row.cells.size() - 1);
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
        removeLastEmptyCell(row);
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
    try {
      ctx.pushFragStack(table);
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
    } finally {
      ctx.popFragStack();
    }
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
        if (nt == '\n' || nt == -1) {
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
    // ntext = "\n" + ntext;
    parseFrags(ntext, parseContext);
    return new GWikiContent(parseContext.popFragList());

  }

  /**
   * Parse body. Different to parseText no nextToken will be called initialilly
   * 
   * @param tks
   * @param ctx
   */
  public void parseMacroBody(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    while (tks.eof() == false) {
      parseLine(tks, ctx);
      if (tks.eof() == true) {
        break;
      }
      tks.nextToken();
    }
  }

  public static boolean isParagraphLike(GWikiFragment ff)
  {
    return ff instanceof GWikiFragmentP
        || ff instanceof GWikiFragmentHeading
        || ff instanceof GWikiFragmentTable
        || ff instanceof GWikiFragmentHr
        || ff instanceof GWikiFragmentList
        || ff instanceof GWikiFragmentLi
        || (ff instanceof GWikiMacroFragment && GWikiMacroRenderFlags.NoWrapWithP.isSet(((GWikiMacroFragment) ff).getMacro()
            .getRenderModes()));

  }

  protected List<GWikiFragment> removeBrsAfterParagraph(List<GWikiFragment> l)
  {
    if (l.size() <= 1) {
      return l;
    }
    GWikiFragment ff = l.get(0);
    if (isParagraphLike(ff) == false) {
      return l;
    }
    if (l.size() == 3) {
      if (l.get(2) instanceof GWikiFragmentBr) {
        l = l.subList(0, 2);
      } else {
        return l;
      }
    }
    if (l.get(1) instanceof GWikiFragmentBr) {
      l = l.subList(0, 1);
    }
    return l;
  }

  protected List<GWikiFragment> trimBrs(List<GWikiFragment> l)
  {
    if (l.isEmpty() == true) {
      return l;
    }
    if (l.get(0) instanceof GWikiFragmentBr) {
      l = l.subList(1, l.size());
    }
    if (l.isEmpty() == true) {
      return l;
    }
    if (l.get(l.size() - 1) instanceof GWikiFragmentBr) {
      l = l.subList(0, l.size() - 1);
    }
    return l;
  }

  protected List<GWikiFragment> trimEndP(List<GWikiFragment> l)
  {
    if (l.isEmpty() == true) {
      return l;
    }
    if (l.get(l.size() - 1) instanceof GWikiFragmentP) {
      l = l.subList(0, l.size() - 1);
    }
    return l;
  }

  protected List<GWikiFragment> addWrappedP(List<GWikiFragment> l)
  {
    List<GWikiFragment> ret = new ArrayList<GWikiFragment>();
    int ls = 0;
    for (int i = 0; i < l.size(); ++i) {
      GWikiFragment f = l.get(i);
      if (isParagraphLike(f) == true) {
        if (i > ls) {
          List<GWikiFragment> lp = l.subList(ls, i);
          lp = trimBrs(lp);
          // lp = trimEndP(lp);
          ret.add(new GWikiFragmentP(lp));
        }
        if ((f instanceof GWikiFragmentP) == false) {
          ret.add(f);
        }
        ls = i + 1;
      }
    }
    if (ls < l.size()) {
      List<GWikiFragment> lp = l.subList(ls, l.size());
      lp = trimBrs(lp);
      if (lp.isEmpty() == false) {
        ret.add(new GWikiFragmentP(lp));
      }
    }
    return ret;
  }

  protected void addWrappedP(GWikiWikiParserContext ctx, List<GWikiFragment> l)
  {
    List<GWikiFragment> r = addWrappedP(l);
    ctx.addFragments(r);
  }

  public void convertPs(GWikiWikiParserContext ctx)
  {
    if (isPAllowedInDom(ctx) == false) {
      return;
    }
    List<GWikiFragment> l = ctx.popFragList();
    if (l.isEmpty() == true) {
      ctx.pushFragList(l);
      return;
    }
    // l = removeBrsAfterParagraph(l);
    l = addWrappedP(l);
    ctx.pushFragList(l);
    // int startP = 0;
    // List<GWikiFragment> fl = new ArrayList<GWikiFragment>();
    // for (int i = 0; i < l.size(); ++i) {
    // GWikiFragment f = l.get(i);
    // if (isParagraphLike(f) == true) {
    // if (startP != i) {
    // fl.add();
    // } else {
    // fl.add(f);
    // startP = i;
    // }
    // }
    // }
  }

  public void parseText(GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    // so geht das nicht, da li nicht mehr geht
    // List<GWikiFragment> plist = new ArrayList<GWikiFragment>();
    int startPlIdx = -1;
    while (tks.hasNext() == true) {
      tks.nextToken();
      ctx.pushFragList();
      parseLine(tks, ctx);
      List<GWikiFragment> l = ctx.popFragList();
      if (l.isEmpty() == false) {
        // l = removeBrsAfterParagraph(l);
        // l = addWrappedP(l);
        ctx.addFragments(l);
      }

      //        
      // boolean pprocessed = false;
      //
      // if (l.size() > 0) {
      // boolean wrapP = false;
      // GWikiFragment ff = l.get(0);
      // l = removeBrsAfterParagraph(l);
      // // if (l.size() == 2 && isParagraphLike(ff) == true && l.get(1) instanceof GWikiFragmentBr) {
      // // l = l.subList(0, 1);
      // // }
      // boolean toPList = false;
      // if ((l.size() > 1 || tks.eof()) && isParagraphLike(ff) == false) {
      // toPList = true;
      // }
      // GWikiFragment lf = l.get(l.size() - 1);
      // if (lf instanceof GWikiFragmentP || (tks.eof() && lf instanceof GWikiFragmentBr)) {
      // l = l.subList(0, l.size() - 1);
      // wrapP = true;
      // }
      // if (wrapP == true) {
      // if (l.size() > 0 && l.get(l.size() - 1) instanceof GWikiFragmentBr) {
      // l = l.subList(0, l.size() - 1);
      // }
      // List<GWikiFragment> addList = l;
      // if (startPlIdx != -1) {
      // List<GWikiFragment> plist = ctx.popFragList();
      // List<GWikiFragment> rl = plist.subList(0, startPlIdx);
      // ctx.pushFragList(rl);
      // List<GWikiFragment> ll = plist.subList(startPlIdx, plist.size());
      // addList = new ArrayList<GWikiFragment>();
      // addList.addAll(ll);
      // addList.addAll(l);
      // l = addList;
      // startPlIdx = -1;
      // }
      // addWrappedP(ctx, l);
      //
      // pprocessed = true;
      // } else if (toPList == true) {
      // if (startPlIdx == -1) {
      // startPlIdx = ctx.peek(0).size();
      // }
      // }
      // }
      // if (pprocessed == false) {
      // ctx.addFragments(l);
      // }
      // if (tks.eof() == true) {
      // break;
      // }
    }
    convertPs(ctx);
    // if (startPlIdx != -1) {
    // List<GWikiFragment> plist = ctx.popFragList();
    // int minpidx = Math.min(startPlIdx, plist.size());
    // List<GWikiFragment> rl = plist.subList(0, minpidx);
    // ctx.pushFragList(rl);
    // List<GWikiFragment> ll = plist.subList(minpidx, plist.size());
    // if (ll.size() > 0 && ll.get(ll.size() - 1) instanceof GWikiFragmentBr) {
    // ll = ll.subList(0, ll.size() - 1);
    // }
    // List<GWikiFragment> addList = new ArrayList<GWikiFragment>();
    // addList.addAll(ll);
    // // addList.addAll(l);
    // // l = addList;
    // startPlIdx = -1;
    // ctx.addFragment(new GWikiFragmentP(addList));
    // }
  }

  public void reworkPs(GWikiWikiParserContext ctx)
  {

  }

  public void parseFrags(String text, GWikiWikiParserContext ctx)
  {
    ctx.pushFragList();
    String delimiter = "\n \t \\-*_|{}=#+^~[]!.:?;,\"";
    GWikiWikiTokens tks = new GWikiWikiTokens(delimiter, text);
    parseText(tks, ctx);
    reworkPs(ctx);
  }
}
