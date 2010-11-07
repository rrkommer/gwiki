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

package de.micromata.genome.gwiki.page.attachments;

import java.io.InputStream;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Utitilies to extreact text.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class TextExtractorUtils
{

  public static String showBinary(String text)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < text.length(); ++i) {
      char c = text.charAt(i);
      int ic = (int) c;
      if (ic > 21) {
        sb.append(c);
      } else {
        if (c == '\n') {
          sb.append(c);
        } else {
          sb.append("<" + ic + ">");
        }
      }
    }
    return sb.toString();
  }

  /**
   * 
   * @param fileName
   * @param is
   * @return null if not found
   */
  public static TextExtractor getTextExtractor(GWikiContext ctx, String fileName)
  {
    String lcn = fileName.toLowerCase();
    int idx = lcn.lastIndexOf('.');
    if (idx == -1) {
      return null;
    }
    String ext = lcn.substring(idx);
    return ctx.getWikiWeb().getFilter().getTextExtractors().get(ext);
  }

  public static String getTextExtract(GWikiContext ctx, String fileName, InputStream is)
  {
    TextExtractor ex = getTextExtractor(ctx, fileName);
    if (ex == null) {
      return "";
    }
    return ex.extractText(fileName, is);
  }
}
