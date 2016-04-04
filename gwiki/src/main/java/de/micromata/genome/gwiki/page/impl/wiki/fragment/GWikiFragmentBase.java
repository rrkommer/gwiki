////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;

public abstract class GWikiFragmentBase implements GWikiFragment
{

  private static final long serialVersionUID = -1842371131960720605L;

  @Override
  public void getSource(StringBuilder sb, GWikiFragment parent, GWikiFragment previous, GWikiFragment next)
  {
    getSource(sb);
  }

  @Override
  public abstract void getSource(StringBuilder sb);

  @Override
  public String getSource()
  {
    StringBuilder sb = new StringBuilder();
    getSource(sb);
    return sb.toString();
  }

  public static void appendPrevNlIfNeeded(StringBuilder sb, GWikiFragment parent, GWikiFragment previous,
      GWikiFragment frag)
  {
    if (GWikiMacroRenderFlags.NewLineBeforeStart.isSet(frag.getRenderModes()) == true) {
      if (sb.length() != 0 && sb.charAt(sb.length() - 1) != '\n') {
        sb.append("\n");
      }
    }
  }

  public static void appendAfterNlIfNeeded(StringBuilder sb, GWikiFragment frag, GWikiFragment after)
  {

  }

  public static String trimWhitespaceNl(String text)
  {
    if (text == null) {
      return text;
    }
    int startOffset = 0;
    for (startOffset = 0; startOffset < text.length(); ++startOffset) {
      if (Character.isWhitespace(text.charAt(startOffset)) == false) {
        break;
      }
    }
    String ret = text.substring(startOffset);

    int endOffset = ret.length() - 1;
    for (; endOffset > 0; --endOffset) {
      if (Character.isWhitespace(ret.charAt(endOffset)) == false) {
        break;
      }
    }
    ret = ret.substring(0, endOffset + 1);
    return ret;
  }

  @Override
  public String toString()
  {
    return getSource();
  }

  @Override
  public void iterate(GWikiFragmentVisitor visitor)
  {
    visitor.begin(this);
    visitor.end(this);
  }

  @Override
  public boolean requirePrepareHeader(GWikiContext ctx)
  {
    return false;
  }

  @Override
  public void prepareHeader(GWikiContext ctx)
  {

  }
}
