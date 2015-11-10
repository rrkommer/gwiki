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
 * Base class for xml nodes.
 * 
 * XmlBuilder fuer das Bauen von XML-Dateien.
 * 
 * Der XmlBuilder ist nicht streamingfeahig, sondern baut im Speicher eine Baum von XmlBuilder-Nodes auf um diese am Ende als XML zu
 * serialisieren.
 * 
 * Der XmlBuilder wird in der Regel ueber statische Methoden verwendet, die mit static import direkt importiert werden konnen.
 * 
 * 
 * 
 * @author roger@micromata.de
 * 
 */
public abstract class XmlNode
{
  /**
   * to render XML to stream.
   * 
   * @param renderer
   * @throws IOException if writing output throws {@link IOException}
   */
  public abstract void toXml(XmlRenderer renderer) throws IOException;

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    PrittyXmlRenderer renderer = new PrittyXmlRenderer(sb);
    try {
      toXml(renderer);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return sb.toString();
  }
}
