/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    jens@micromata.de
// Created   21.08.2008
// Copyright Micromata 21.08.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.commons.io.IOUtils;

/**
 * Implementation of BodyContent.
 * 
 * @author roger
 * 
 */
public class BodyContentImpl extends BodyContent implements BodyFlusher
{
  StringWriter sw = new StringWriter();

  protected BodyContentImpl(JspWriter e)
  {
    super(e);
  }

  public BodyContentImpl()
  {
    super(new NullJspWriter());
  }

  @Override
  public Reader getReader()
  {
    return new StringReader(sw.toString());
  }

  @Override
  public String getString()
  {
    return sw.toString();
  }

  @Override
  public void writeOut(Writer out) throws IOException
  {
    IOUtils.copy(getReader(), out);
  }

  @Override
  public void clear() throws IOException
  {
    sw = new StringWriter();
  }

  @Override
  public void clearBuffer() throws IOException
  {
    clear();
  }

  @Override
  public void clearBody()
  {
    sw = new StringWriter();
  }

  @Override
  public void flush() throws IOException
  {
    flushBody();
    // super.flush(); // otherwise java.io.IOException: Illegal to flush within a custom tag
  }

  public void flushBody() throws IOException
  {
    if ((getEnclosingWriter() instanceof NullJspWriter) == false) {
      write(sw.getBuffer().toString());
      sw = new StringWriter();
    }
  }

  @Override
  public int getBufferSize()
  {
    return 4096;
  }

  @Override
  public boolean isAutoFlush()
  {
    return false;
  }

  @Override
  public void close() throws IOException
  {
    flush();
    sw.close();
  }

  @Override
  public int getRemaining()
  {
    return 4096;
  }

  @Override
  public void newLine() throws IOException
  {
    sw.append("\n");
  }

  @Override
  public void print(boolean arg0) throws IOException
  {
    sw.append(Boolean.toString(arg0));
  }

  @Override
  public void print(char arg0) throws IOException
  {
    sw.append(Character.toString(arg0));
  }

  @Override
  public void print(int arg0) throws IOException
  {
    sw.append(Integer.toString(arg0));
  }

  @Override
  public void print(long arg0) throws IOException
  {
    sw.append(Long.toString(arg0));
  }

  @Override
  public void print(float arg0) throws IOException
  {
    sw.append(Float.toString(arg0));
  }

  @Override
  public void print(double arg0) throws IOException
  {
    sw.append(Double.toString(arg0));
  }

  @Override
  public void print(char[] arg0) throws IOException
  {
    sw.append(new String(arg0));
  }

  @Override
  public void print(String arg0) throws IOException
  {
    sw.append(arg0);
  }

  @Override
  public void print(Object arg0) throws IOException
  {
    if (arg0 != null)
      sw.append(arg0.toString());
  }

  @Override
  public void println() throws IOException
  {
    sw.append("\n");
  }

  @Override
  public void println(boolean arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void println(char arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void println(int arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void println(long arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void println(float arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void println(double arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void println(char[] arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void println(String arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void println(Object arg0) throws IOException
  {
    print(arg0);
    println();
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException
  {
    sw.write(cbuf, off, len);
  }
}
