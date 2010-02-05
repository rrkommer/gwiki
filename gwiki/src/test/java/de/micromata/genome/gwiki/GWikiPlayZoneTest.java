/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   26.11.2009
// Copyright Micromata 26.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;

import de.micromata.genome.gwiki.page.attachments.TextExtractorUtils;
import de.micromata.genome.util.runtime.RuntimeIOException;

public class GWikiPlayZoneTest extends TestCase
{
  public String getTextExtract(String file)
  {
    try {
      String s = TextExtractorUtils.getTextExtract(file, new FileInputStream(new File(file)));
      return s;
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }

  }

  public void getWordText()
  {
    String text;
    String wordFile = "C:\\Users\\roger\\d\\dhl\\DHL-POP\\DHL-ParcelOnlinePostage-Doc\\doc\\DocRepository\\DeliverableItems\\SRS\\34-DHL-SRS-POP-0.4.8-WebServicesShoppingCart.doc";
    // text = getTextExtract(wordFile);
    // System.out.println(text);
    String execFile = "C:\\Users\\roger\\d\\dhl\\DHL-POP\\DHL-ParcelOnlinePostage-Doc\\doc\\DocRepository\\DeliverableItems\\Handbuecher\\FehlermeldungenAppletPrinter.xls";
    // text = getTextExtract(execFile);
    // System.out.println(text);

    String pptFile = "c:\\users\\roger\\d\\dhl\\dhl-pop\\dhl-parcelonlinepostage-doc\\doc\\DocRepository\\IT-Specifications\\Architektur\\2006-12-04_DHL-POP_Webservices_Workshop.ppt";
    // text = getTextExtract(pptFile);

    String pdfFile = "c:\\users\\roger\\d\\dhl\\dhl-pop\\dhl-parcelonlinepostage-doc\\doc\\DocRepository\\Specifications\\dhl\\globuss\\Einlieferungsliste_im_3D-Flatfile-Format_Version_1.1.pdf";
    text = getTextExtract(pdfFile);
    System.out.println(text);
    try {
      FileOutputStream fout = new FileOutputStream(new File("tmp/pdf2html.html"));
      IOUtils.copy(new StringReader(text), fout, "UTF-8");
      IOUtils.closeQuietly(fout);
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  public void NOtestTextExtract()
  {
    getWordText();
  }

  public void textExtract()
  {

    // try {
    // // String wordFile =
    // //
    // "C:\\Users\\roger\\d\\dhl\\DHL-POP\\DHL-ParcelOnlinePostage-Doc\\doc\\DocRepository\\DeliverableItems\\SRS\\34-DHL-SRS-POP-0.4.8-WebServicesShoppingCart.doc";
    // String wordFile =
    // "C:\\Users\\roger\\d\\dhl\\DHL-POP\\DHL-ParcelOnlinePostage-Doc\\doc\\DocRepository\\DeliverableItems\\SRS\\33-DHL-POP-SRS-0.3-SAP_Fibu.doc";
    // FileInputStream fis = new FileInputStream(wordFile);
    // POIFSFileSystem fileSystem = new POIFSFileSystem(fis);
    // // Firstly, get an extractor for the Workbook
    // POIOLE2TextExtractor oleTextExtractor = ExtractorFactory.createExtractor(fileSystem);
    // // Then a List of extractors for any embedded Excel, Word, PowerPoint
    // // or Visio objects embedded into it.
    // POITextExtractor[] embeddedExtractors = ExtractorFactory.getEmbededDocsTextExtractors(oleTextExtractor);
    // for (POITextExtractor textExtractor : embeddedExtractors) {
    // // If the embedded object was an Excel spreadsheet.
    // if (textExtractor instanceof ExcelExtractor) {
    // ExcelExtractor excelExtractor = (ExcelExtractor) textExtractor;
    // System.out.println(excelExtractor.getText());
    // }
    // // A Word Document
    // else if (textExtractor instanceof WordExtractor) {
    // WordExtractor wordExtractor = (WordExtractor) textExtractor;
    // String text = wordExtractor.getText();
    // System.out.println("Text: " + text);
    // String[] paragraphText = wordExtractor.getParagraphText();
    // for (String paragraph : paragraphText) {
    // System.out.println(paragraph);
    // }
    // // Display the document's header and footer text
    // System.out.println("Footer text: " + wordExtractor.getFooterText());
    // System.out.println("Header text: " + wordExtractor.getHeaderText());
    // }
    // // PowerPoint Presentation.
    // else if (textExtractor instanceof PowerPointExtractor) {
    // PowerPointExtractor powerPointExtractor = (PowerPointExtractor) textExtractor;
    // System.out.println("Text: " + powerPointExtractor.getText());
    // System.out.println("Notes: " + powerPointExtractor.getNotes());
    // }
    // // Visio Drawing
    // else if (textExtractor instanceof VisioTextExtractor) {
    // VisioTextExtractor visioTextExtractor = (VisioTextExtractor) textExtractor;
    // System.out.println("Text: " + visioTextExtractor.getText());
    // }
    // }
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // }
  }

  public void NotestIt()
  {
    textExtract();
  }
}
