/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
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
