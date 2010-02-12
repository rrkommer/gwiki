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

import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * Extracts text from a ms word file.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class WordTextExtractor extends TextExtractorBase
{
  public WordTextExtractor(String fileName, InputStream data)
  {
    super(fileName, data);
  }

  public String extractText()
  {
    try {
      WordExtractor extr = new WordExtractor(data);
      String text = extr.getText();
      text = TextExtractorUtils.reworkWordText(text);
      return text;
    } catch (IOException ex) {
      throw new RuntimeIOException("Failure to extract word from " + fileName + "; " + ex.getMessage(), ex);
    }
  }

}
