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
import java.io.StringWriter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import de.micromata.genome.util.runtime.RuntimeIOException;

public class PdfTextExtractor extends TextExtractorBase
{

  public PdfTextExtractor(String fileName, InputStream data)
  {
    super(fileName, data);
  }

  public String extractText()
  {
    try {
      PDDocument doc = PDDocument.load(data);
      PDFTextStripper st = new PDFTextStripper("UTF-8");
      // PDFText2HTML st = new PDFText2HTML("UTF-8");
      StringWriter sout = new StringWriter();
      st.writeText(doc, sout);
      doc.close();
      return sout.getBuffer().toString();
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

}
