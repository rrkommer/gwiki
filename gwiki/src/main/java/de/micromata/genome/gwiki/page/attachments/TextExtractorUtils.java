/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   04.12.2009
// Copyright Micromata 04.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.attachments;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hwpf.extractor.WordExtractor;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * Utitilies to extreact text.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class TextExtractorUtils
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

  public static String getTextExtractFromWord(GWikiContext wikiContext, InputStream is)
  {
    try {
      WordExtractor extr = new WordExtractor(is);
      String text = extr.getText();
      // String[] docArray = extr.getParagraphText();
      // String text = StringUtils.join(docArray, "\n");
      text = reworkWordText(text);
      // text = showBinary(text);
      return text;
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  public static TextExtractor getTextExtractor(String fileName, InputStream is)
  {
    String lcn = fileName.toLowerCase();
    if (lcn.endsWith(".doc") == true) {
      return new WordTextExtractor(fileName, is);
    } else if (lcn.endsWith(".xls") == true) {
      return new ExcelTextExtractor(fileName, is);
    } else if (lcn.endsWith(".ppt") == true) {
      return new PowerPointTextExtractor(fileName, is);
    } else if (lcn.endsWith(".pdf") == true) {
      return new PdfTextExtractor(fileName, is);
    } else if (lcn.endsWith(".txt") == true) {
      return new TxtTextExtractor(fileName, is);
    } else if (lcn.endsWith(".xml") == true) {
      return new XmlTextExtractor(fileName, is);
    } else if (lcn.endsWith(".html") == true || fileName.endsWith(".htm") == true) {
      return new XmlTextExtractor(fileName, is);
    }
    return null;
  }

  public static String getTextExtract(String fileName, InputStream is)
  {
    TextExtractor ex = getTextExtractor(fileName, is);
    if (ex == null) {
      return "";
    }
    return ex.extractText();
  }
}
