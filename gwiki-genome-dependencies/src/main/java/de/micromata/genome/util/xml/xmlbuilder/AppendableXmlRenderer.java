////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

/////////////////////////////////////////////////////////////////////////////
//
// Project Genome Core
//
// Author    roger@micromata.de
// Created   13.06.2009
// Copyright Micromata 13.06.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.xml.xmlbuilder;

import java.io.IOException;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * 
 * @author roger@micromata.de
 * 
 */
public class AppendableXmlRenderer implements XmlRenderer
{
  protected final Appendable appender;

  public AppendableXmlRenderer(final Appendable appender)
  {
    this.appender = appender;
  }

  public XmlRenderer code(String code) throws IOException
  {
    appender.append(code);
    return this;
  }

  public XmlRenderer nl() throws IOException
  {
    return this;
  }

  public XmlRenderer text(String code) throws IOException
  {
    appender.append(StringEscapeUtils.escapeXml(code));
    return this;
  }

  public XmlRenderer elementBeginOpen() throws IOException
  {
    appender.append("<");
    return this;
  }

  public XmlRenderer elementBeginEndClosed() throws IOException
  {
    appender.append("/>");
    return this;
  }

  public XmlRenderer elementBeginEndOpen() throws IOException
  {
    appender.append(">");
    return this;
  }

  public XmlRenderer elementEndClose() throws IOException
  {
    appender.append(">");
    return this;
  }

  public XmlRenderer elementEndOpen() throws IOException
  {
    appender.append("</");
    return this;
  }
}
