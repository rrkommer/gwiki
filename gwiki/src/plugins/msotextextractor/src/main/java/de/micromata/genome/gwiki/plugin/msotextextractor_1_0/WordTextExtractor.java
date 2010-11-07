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
   * <19> HYPERLINK "" \l "ShowCouponInfoRequestType" <20> <19> HYPERLINK "" \l "ItemType" <20>sc:ItemType<21>
   * 
   * <11>Attribute<13> siehe Abschnitt <19> REF _Ref234057110 \r \h <1><20>2.6.3.2<21>
   * 
   * <19> REF _Ref234386510 \h <1><20>Element LoadBuyedShoppingCartRequest<21><13>
   * 
   * 
   * 3.26 Element LoadBuyedShoppingCartRequest <19> PAGEREF _Toc237087386 \h <1><20>31<21><13>
   * 
   * <19> HYPERLINK "" \l "ShoppingCartCheckoutViaPaymentResponseT" <20>ws:ShoppingCartCheckoutViaPaymentResponseType<21>
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

      int ic = (int) c;
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
