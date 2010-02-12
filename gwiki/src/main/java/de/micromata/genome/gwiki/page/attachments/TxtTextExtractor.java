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

import org.apache.commons.io.IOUtils;

import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * Extracts text from a text file.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class TxtTextExtractor extends TextExtractorBase
{

  public TxtTextExtractor(String fileName, InputStream data)
  {
    super(fileName, data);
  }

  public String extractText()
  {
    try {
      StringWriter sout = new StringWriter();
      IOUtils.copy(data, sout, "ISO-8859-1");
      return sout.toString();
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

}
