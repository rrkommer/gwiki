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

import java.io.InputStream;

/**
 * Base implementation for a TextExtractor.
 * 
 * @author roger
 * 
 */
public abstract class TextExtractorBase implements TextExtractor
{
  protected String fileName;

  protected InputStream data;

  public TextExtractorBase(String fileName, InputStream data)
  {
    this.fileName = fileName;
    this.data = data;
  }

  public String getFileName()
  {
    return fileName;
  }

  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }

  public InputStream getData()
  {
    return data;
  }

  public void setData(InputStream data)
  {
    this.data = data;
  }

}
