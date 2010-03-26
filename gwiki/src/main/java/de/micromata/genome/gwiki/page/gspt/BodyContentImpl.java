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
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class BodyContentImpl extends BodyContent implements BodyFlusher
{
  protected StringWriter sw = new StringWriter();

  public BodyContentImpl(JspWriter e)
  {
    super(e);
  }

  public BodyContentImpl(JspWriter e, int bufferSize, boolean autoFlush)
  {
    this(e);
    this.autoFlush = autoFlush;
    this.bufferSize = bufferSize;

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
    if (bufferSize == NO_BUFFER) {
      getEnclosingWriter().clear();
    } else {
      sw = new StringWriter();
    }
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
    if (bufferSize == NO_BUFFER) {
      getEnclosingWriter().flush();
    } else {
      flushBody();
    }

  }

  public void flushBody() throws IOException
  {
    if ((getEnclosingWriter() instanceof NullJspWriter) == false) {
      this.getEnclosingWriter().write(sw.getBuffer().toString());
      sw = new StringWriter();

    }
  }

  @Override
  public int getBufferSize()
  {
    return UNBOUNDED_BUFFER;
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

  public void write(String s) throws IOException
  {
    if (bufferSize == NO_BUFFER) {
      getEnclosingWriter().write(s);
    } else {
      sw.append(s);
    }
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException
  {
    if (bufferSize == NO_BUFFER) {
      write(cbuf, off, len);
    } else {
      sw.write(cbuf, off, len);
    }
  }

  @Override
  public void newLine() throws IOException
  {
    write("\n");
  }

  @Override
  public void print(boolean arg0) throws IOException
  {
    write(Boolean.toString(arg0));
  }

  @Override
  public void print(char arg0) throws IOException
  {
    write(Character.toString(arg0));
  }

  @Override
  public void print(int arg0) throws IOException
  {
    write(Integer.toString(arg0));
  }

  @Override
  public void print(long arg0) throws IOException
  {
    write(Long.toString(arg0));
  }

  @Override
  public void print(float arg0) throws IOException
  {
    write(Float.toString(arg0));
  }

  @Override
  public void print(double arg0) throws IOException
  {
    write(Double.toString(arg0));
  }

  @Override
  public void print(char[] arg0) throws IOException
  {
    write(new String(arg0));
  }

  @Override
  public void print(String arg0) throws IOException
  {
    write(arg0);
  }

  @Override
  public void print(Object arg0) throws IOException
  {
    if (arg0 != null) {
      write(arg0.toString());
    }
  }

  @Override
  public void println() throws IOException
  {
    write("\n");
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

}
