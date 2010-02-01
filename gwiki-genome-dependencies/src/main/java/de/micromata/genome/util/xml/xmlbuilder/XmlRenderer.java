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
 * Interface to render XML
 * 
 * @author roger@micromata.de
 * 
 */
public interface XmlRenderer
{
  /**
   * Just pass through code.
   * 
   * @param code
   * @return
   */
  XmlRenderer code(String code) throws IOException;

  /**
   * Text will be escaped.s
   * 
   * @param code
   * @return
   */
  XmlRenderer text(String code) throws IOException;

  /**
   * for new Line
   * 
   * @return
   */
  XmlRenderer elementBeginOpen() throws IOException;
  XmlRenderer elementBeginEndOpen() throws IOException;
  XmlRenderer elementBeginEndClosed() throws IOException;
  XmlRenderer elementEndOpen() throws IOException;
  XmlRenderer elementEndClose() throws IOException;
}
