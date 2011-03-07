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

package de.micromata.genome.gwiki.plugin.confluenceimporter_1_0.confluence;

import org.dom4j.Element;

/**
 * Holds an Confluence attachment.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
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
