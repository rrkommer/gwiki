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

package de.micromata.genome.gwiki.page;

/**
 * Utitilies for dealing with GWikiContext.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiContextUtils
{
  public static void renderRequiredJs(GWikiContext wikiContext)
  {
    for (String s : wikiContext.getRequiredJs()) {
      wikiContext.append("<script type=\"text/javascript\" src=\"" + wikiContext.localUrl(s) + "\"></script>\n");
    }
  }

  public static void renderRequiredCss(GWikiContext wikiContext)
  {
    for (String s : wikiContext.getRequiredCss()) {
      wikiContext.append("<link rel=\"stylesheet\" type=\"text/css\"  src=\"" + wikiContext.localUrl(s) + "\"/>\n");
    }
  }
}
