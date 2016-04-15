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

package de.micromata.genome.gwiki.plugin.msotextextractor_1_0;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hwpf.extractor.WordExtractor;

import de.micromata.genome.gwiki.page.attachments.TextExtractor;
import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * Extracts text from a ms word file.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class WordTextExtractor implements TextExtractor
{
  /**
   * &lt;19&gt; HYPERLINK "" \l "ShowCouponInfoRequestType" &lt;20&gt; &lt;19&gt; HYPERLINK "" \l "ItemType"
   * &lt;20&gt;sc:ItemType&lt;21&gt;
   * 
   * &lt;11&gt;Attribute&lt;13&gt; siehe Abschnitt &lt;19&gt; REF _Ref234057110 \r \h
   * &lt;1&gt;&lt;20&gt;2.6.3.2&lt;21&gt;
   * 
   * &lt;19&gt; REF _Ref234386510 \h &lt;1&gt;&lt;20&gt;Element LoadBuyedShoppingCartRequest&lt;21&gt;&lt;13&gt;
   * 
   * 
   * 3.26 Element LoadBuyedShoppingCartRequest &lt;19&gt; PAGEREF _Toc237087386 \h
   * &lt;1&gt;&lt;20&gt;31&lt;21&gt;&lt;13&gt;
   * 
   * &lt;19&gt; HYPERLINK "" \l "ShoppingCartCheckoutViaPaymentResponseT"
   * &lt;20&gt;ws:ShoppingCartCheckoutViaPaymentResponseType&lt;21&gt;
   * 
   * @param text
   * @return
   */
  public static String reworkWordText(String text)
  {
    StringBuilder sb = new StringBuilder();
    char lc = 0;
    for (int i = 0; i < text.length(); ++i) {
      char c = text.charAt(i);

      int ic = c;
      if (ic > 21) {
        sb.append(c);
        lc = c;
        continue;
      }
      switch (c) {
        case '\n':
        case '\t':
          sb.append(c);
          break;
        case 1: //
        case 19: // !!
        case 20: // nl-Zeichen
          // sb.append("<" + ic + ">");
          break;
        case 7: //
          if (lc == 7) {
            sb.append("|\n|");
          } else {
            sb.append("|");
          }
          break;
        case '\b':
        case '\f':
        case '\r':
          // break;
        default:
          // System.out.println("c: " + c + ": " + (int) c);
          // sb.append("<" + ic + ">");
          break;
      }
      lc = c;
    }
    return sb.toString();
  }

  @Override
  public String extractText(String fileName, InputStream data)
  {
    try {
      WordExtractor extr = new WordExtractor(data);
      String text = extr.getText();
      text = reworkWordText(text);
      return text;
    } catch (IOException ex) {
      throw new RuntimeIOException("Failure to extract word from " + fileName + "; " + ex.getMessage(), ex);
    }
  }

}
