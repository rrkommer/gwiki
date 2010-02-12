/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.11.2009
// Copyright Micromata 21.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.tools.confluence.imp;

import org.dom4j.Element;

/**
 * Holds an Confluence attachment.
 * 
 * @author roger
 * 
 */
public class Attachment extends ConfluenceElement
{
  protected String fileName;

  protected String contentType;

  protected String content;

  protected String fileSize;

  protected String parent;

  public Attachment(Element el)
  {
    super(el);
  }

  @Override
  public void parse()
  {
    parent = selectText("property[@name=\"content\"]/id/child::text()");
    fileName = selectText("property[@name=\"fileName\"]/child::text()");
    contentType = selectText("property[@name=\"contentType\"]/child::text()");
    fileSize = selectText("property[@name=\"fileSize\"]/child::text()");
    version = selectText("property[@name=\"attachmentVersion\"]/child::text()");
  }

  public String getFileName()
  {
    return fileName;
  }

  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }

  public String getContentType()
  {
    return contentType;
  }

  public void setContentType(String contentType)
  {
    this.contentType = contentType;
  }

  public String getContent()
  {
    return content;
  }

  public void setContent(String content)
  {
    this.content = content;
  }

  public String getFileSize()
  {
    return fileSize;
  }

  public void setFileSize(String fileSize)
  {
    this.fileSize = fileSize;
  }

  public String getParent()
  {
    return parent;
  }

  public void setParent(String parent)
  {
    this.parent = parent;
  }

}
