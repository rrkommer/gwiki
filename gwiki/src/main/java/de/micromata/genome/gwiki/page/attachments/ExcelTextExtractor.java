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

import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * Extracts text from a excel sheet.
 * 
 * @author roger
 * 
 */
public class ExcelTextExtractor extends TextExtractorBase
{
  public ExcelTextExtractor(String fileName, InputStream data)
  {
    super(fileName, data);
  }

  public String extractText()
  {
    try {
      HSSFWorkbook wb = new HSSFWorkbook(data);
      ExcelExtractor extr = new ExcelExtractor(wb);
      String text = extr.getText();
      text = TextExtractorUtils.reworkWordText(text);
      return text;
    } catch (IOException ex) {
      throw new RuntimeIOException("Failure to extract word from " + fileName + "; " + ex.getMessage(), ex);
    }
  }

}
