/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    jens@micromata.de
// Created   18.08.2008
// Copyright Micromata 18.08.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * JspWriter just discarging written content.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class NullJspWriter extends JspWriter
{
  public NullJspWriter()
  {
    super(1, false);
  }

  @Override
  public void clear() throws IOException
  {
  }

  @Override
  public void clearBuffer() throws IOException
  {
  }

  @Override
  public void close() throws IOException
  {
  }

  @Override
  public void flush() throws IOException
  {
  }

  @Override
  public int getRemaining()
  {
    return 0;
  }

  @Override
  public void newLine() throws IOException
  {

  }

  @Override
  public void print(boolean arg0) throws IOException
  {
  }

  @Override
  public void print(char arg0) throws IOException
  {
  }

  @Override
  public void print(int arg0) throws IOException
  {

  }

  @Override
  public void print(long arg0) throws IOException
  {

  }

  @Override
  public void print(float arg0) throws IOException
  {

  }

  @Override
  public void print(double arg0) throws IOException
  {

  }

  @Override
  public void print(char[] arg0) throws IOException
  {

  }

  @Override
  public void print(String arg0) throws IOException
  {

  }

  @Override
  public void print(Object arg0) throws IOException
  {

  }

  @Override
  public void println() throws IOException
  {

  }

  @Override
  public void println(boolean arg0) throws IOException
  {

  }

  @Override
  public void println(char arg0) throws IOException
  {

  }

  @Override
  public void println(int arg0) throws IOException
  {

  }

  @Override
  public void println(long arg0) throws IOException
  {

  }

  @Override
  public void println(float arg0) throws IOException
  {

  }

  @Override
  public void println(double arg0) throws IOException
  {
  }

  @Override
  public void println(char[] arg0) throws IOException
  {
  }

  @Override
  public void println(String arg0) throws IOException
  {
  }

  @Override
  public void println(Object arg0) throws IOException
  {

  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException
  {

  }

}