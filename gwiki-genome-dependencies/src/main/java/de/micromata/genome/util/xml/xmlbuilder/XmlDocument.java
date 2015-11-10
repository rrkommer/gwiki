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

/**
 * Document node.
 * 
 * @author roger@micromata.de
 * 
 */
public class XmlDocument extends XmlWithChilds
{
  /**
   * attributes. Must be even.
   */
  private String[] attributes;

  public XmlDocument(XmlNode... childs)
  {
    super(childs);
  }

  public XmlDocument(String[]... attrs)
  {
    attributes = XmlElement.join(attrs);
  }

  public XmlDocument(String[][] attrs, XmlNode... childs)
  {
    super(childs);
    attributes = XmlElement.join(attrs);
  }

  @Override
  public void toXml(XmlRenderer sb) throws IOException
  {
    sb.code("<?xml");
    XmlElement.renderAttributes(attributes, sb);
    sb.code("?>");
    toStringChilds(sb);
  }

}
