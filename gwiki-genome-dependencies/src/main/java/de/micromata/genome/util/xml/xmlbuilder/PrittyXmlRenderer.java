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
 * Print xml with indention.
 * 
 * @author roger@micromata.de
 * 
 */
public class PrittyXmlRenderer extends AppendableXmlRenderer
{
  protected int indent = 0;

  protected boolean needIndent = false;

  protected String indentString = "  ";

  protected boolean lastWasTagEnd = false;

  public PrittyXmlRenderer(Appendable appender)
  {
    super(appender);
  }

  protected void indent() throws IOException
  {
    for (int i = 0; i < indent; ++i) {
      appender.append(indentString);
    }
  }

  public XmlRenderer elementBeginOpen() throws IOException
  {
    if (true || lastWasTagEnd == true) {
      appender.append("\n");
      indent();
    }
    return super.elementBeginOpen();
  }

  public XmlRenderer elementBeginEndClosed() throws IOException
  {
    lastWasTagEnd = true;
    return super.elementBeginEndClosed();
  }

  public XmlRenderer elementBeginEndOpen() throws IOException
  {
    lastWasTagEnd = true;
    ++indent;
    return super.elementBeginEndOpen();
  }

  public XmlRenderer elementEndClose() throws IOException
  {
    lastWasTagEnd = true;
    return super.elementEndClose();
  }

  public XmlRenderer elementEndOpen() throws IOException
  {
    --indent;
    if (lastWasTagEnd == true) {
      appender.append("\n");
      indent();
    }
    return super.elementEndOpen();
  }

  @Override
  public XmlRenderer text(String code) throws IOException
  {
    lastWasTagEnd = false;
    return super.text(code);
  }

  @Override
  public XmlRenderer code(String code) throws IOException
  {
    lastWasTagEnd = false;
    return super.code(code);
  }

}
