//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.page.gspt.jdkrepl;

/*
 * @(#)PrintWriter.java 1.37 04/07/16
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Formatter;
import java.util.IllegalFormatException;
import java.util.Locale;

import de.micromata.genome.gwiki.page.gspt.BodyFlusher;
import de.micromata.genome.util.bean.PrivateBeanUtils;

/**
 * This is patched version of the jdk PrintWriter implmentation. Different to the orignal class, the defaultNewLine will not initialized
 * every time on construction, but only once, when class is loaded. The reason is, when using PrintWriter in multi threaded context every
 * construction of PrintWriter will be fully synchronized because of reading system properties.
 * 
 * Original docu:
 * 
 * Print formatted representations of objects to a text-output stream. This class implements all of the <tt>print</tt> methods found in
 * {@link PrintStream}. It does not contain methods for writing raw bytes, for which a program should use unencoded byte streams.
 * 
 * <p>
 * Unlike the {@link PrintStream} class, if automatic flushing is enabled it will be done only when one of the <tt>println</tt>,
 * <tt>printf</tt>, or <tt>format</tt> methods is invoked, rather than whenever a newline character happens to be output. These methods use
 * the platform's own notion of line separator rather than the newline character.
 * 
 * <p>
 * Methods in this class never throw I/O exceptions, although some of its constructors may. The client may inquire as to whether any errors
 * have occurred by invoking {@link #checkError checkError()}.
 * 
 * @version 1.37, 07/16/04
 * @author Frank Yellin
 * @author Mark Reinhold
 * @since JDK1.1
 */

public class PrintWriterPatched extends Writer implements BodyFlusher
{
  public static class BufferedWriterBody extends BufferedWriter implements BodyFlusher
  {

    public BufferedWriterBody(Writer out, int sz)
    {
      super(out, sz);
    }

    public BufferedWriterBody(Writer out)
    {
      super(out);
    }

    public void flushBody() throws IOException
    {
      PrivateBeanUtils.invokeMethod(this, "flushBuffer");
    }
  }

  public static String defaultNewLine = (String) System.getProperty("line.separator"); // java.security.AccessController.doPrivileged(new

  // sun.security.action.GetPropertyAction("line.separator"));

  /**
   * The underlying character-output stream of this <code>PrintWriter</code>.
   * 
   * @since 1.2
   */
  protected Writer out;

  private boolean autoFlush = false;

  private boolean trouble = false;

  private Formatter formatter;

  /**
   * Line separator string. This is the value of the line.separator property at the moment that the stream was created.
   */
  private String lineSeparator;

  /**
   * Create a new PrintWriter, without automatic line flushing.
   * 
   * @param out A character-output stream
   */
  public PrintWriterPatched(Writer out)
  {
    this(out, false);
  }

  public PrintWriterPatched(Writer out, boolean autoFlush, String lineSeparator)
  {
    super(out);
    this.out = out;
    this.autoFlush = autoFlush;
    this.lineSeparator = lineSeparator;
  }

  /**
   * Create a new PrintWriter.
   * 
   * @param out A character-output stream
   * @param autoFlush A boolean; if true, the <tt>println</tt>, <tt>printf</tt>, or <tt>format</tt> methods will flush the output buffer
   */
  public PrintWriterPatched(Writer out, boolean autoFlush)
  {
    this(out, autoFlush, defaultNewLine);
  }

  private static Writer getStandardWriter(OutputStream os)
  {
    try {
      return new OutputStreamWriter(os, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);

    }
  }

  /**
   * Create a new PrintWriter, without automatic line flushing, from an existing OutputStream. This convenience constructor creates the
   * necessary intermediate OutputStreamWriter, which will convert characters into bytes using the default character encoding.
   * 
   * @param out An output stream
   * 
   * @see java.io.OutputStreamWriter#OutputStreamWriter(java.io.OutputStream)
   */
  public PrintWriterPatched(OutputStream out)
  {
    this(out, false);
  }

  /**
   * Create a new PrintWriter from an existing OutputStream. This convenience constructor creates the necessary intermediate
   * OutputStreamWriter, which will convert characters into bytes using the default character encoding.
   * 
   * @param out An output stream
   * @param autoFlush A boolean; if true, the <tt>println</tt>, <tt>printf</tt>, or <tt>format</tt> methods will flush the output buffer
   * 
   * @see java.io.OutputStreamWriter#OutputStreamWriter(java.io.OutputStream)
   */
  public PrintWriterPatched(OutputStream out, boolean autoFlush)
  {
    this(new BufferedWriter(getStandardWriter(out)), autoFlush);
  }

  /**
   * Creates a new PrintWriter, without automatic line flushing, with the specified file name. This convenience constructor creates the
   * necessary intermediate {@link java.io.OutputStreamWriter OutputStreamWriter}, which will encode characters using the
   * {@linkplain java.nio.charset.Charset#defaultCharset default charset} for this instance of the Java virtual machine.
   * 
   * @param fileName The name of the file to use as the destination of this writer. If the file exists then it will be truncated to zero
   *          size; otherwise, a new file will be created. The output will be written to the file and is buffered.
   * 
   * @throws FileNotFoundException If the given string does not denote an existing, writable regular file and a new regular file of that
   *           name cannot be created, or if some other error occurs while opening or creating the file
   * @throws UnsupportedEncodingException
   * 
   * @throws SecurityException If a security manager is present and {@link SecurityManager#checkWrite checkWrite(fileName)} denies write
   *           access to the file
   * 
   * @since 1.5
   */
  public PrintWriterPatched(String fileName) throws FileNotFoundException
  {
    this(new BufferedWriter(getStandardWriter(new FileOutputStream(fileName))), false);
  }

  /**
   * Creates a new PrintWriter, without automatic line flushing, with the specified file name and charset. This convenience constructor
   * creates the necessary intermediate {@link java.io.OutputStreamWriter OutputStreamWriter}, which will encode characters using the
   * provided charset.
   * 
   * @param fileName The name of the file to use as the destination of this writer. If the file exists then it will be truncated to zero
   *          size; otherwise, a new file will be created. The output will be written to the file and is buffered.
   * 
   * @param csn The name of a supported {@linkplain java.nio.charset.Charset charset}
   * 
   * @throws FileNotFoundException If the given string does not denote an existing, writable regular file and a new regular file of that
   *           name cannot be created, or if some other error occurs while opening or creating the file
   * 
   * @throws SecurityException If a security manager is present and {@link SecurityManager#checkWrite checkWrite(fileName)} denies write
   *           access to the file
   * 
   * @throws UnsupportedEncodingException If the named charset is not supported
   * 
   * @since 1.5
   */
  public PrintWriterPatched(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException
  {
    this(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), csn)), false);
  }

  /**
   * Creates a new PrintWriter, without automatic line flushing, with the specified file. This convenience constructor creates the necessary
   * intermediate {@link java.io.OutputStreamWriter OutputStreamWriter}, which will encode characters using the
   * {@linkplain java.nio.charset.Charset#defaultCharset default charset} for this instance of the Java virtual machine.
   * 
   * @param file The file to use as the destination of this writer. If the file exists then it will be truncated to zero size; otherwise, a
   *          new file will be created. The output will be written to the file and is buffered.
   * 
   * @throws FileNotFoundException If the given file object does not denote an existing, writable regular file and a new regular file of
   *           that name cannot be created, or if some other error occurs while opening or creating the file
   * 
   * @throws SecurityException If a security manager is present and {@link SecurityManager#checkWrite checkWrite(file.getPath())} denies
   *           write access to the file
   * 
   * @since 1.5
   */
  public PrintWriterPatched(File file) throws FileNotFoundException
  {
    this(new BufferedWriter(getStandardWriter(new FileOutputStream(file))), false);
  }

  /**
   * Creates a new PrintWriter, without automatic line flushing, with the specified file and charset. This convenience constructor creates
   * the necessary intermediate {@link java.io.OutputStreamWriter OutputStreamWriter}, which will encode characters using the provided
   * charset.
   * 
   * @param file The file to use as the destination of this writer. If the file exists then it will be truncated to zero size; otherwise, a
   *          new file will be created. The output will be written to the file and is buffered.
   * 
   * @param csn The name of a supported {@linkplain java.nio.charset.Charset charset}
   * 
   * @throws FileNotFoundException If the given file object does not denote an existing, writable regular file and a new regular file of
   *           that name cannot be created, or if some other error occurs while opening or creating the file
   * 
   * @throws SecurityException If a security manager is present and {@link SecurityManager#checkWrite checkWrite(file.getPath())} denies
   *           write access to the file
   * 
   * @throws UnsupportedEncodingException If the named charset is not supported
   * 
   * @since 1.5
   */
  public PrintWriterPatched(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException
  {
    this(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), csn)), false);
  }

  /** Check to make sure that the stream has not been closed */
  private void ensureOpen() throws IOException
  {
    if (out == null)
      throw new IOException("Stream closed");
  }

  /**
   * Flush the stream.
   * 
   * @see #checkError()
   */
  public void flush()
  {
    try {
      synchronized (lock) {
        ensureOpen();
        out.flush();
      }
    } catch (IOException x) {
      trouble = true;
    }
  }

  public void flushBody() throws IOException
  {
    if (out instanceof BodyFlusher) {
      ((BodyFlusher) out).flushBody();
    }
  }

  /**
   * Close the stream.
   * 
   * @see #checkError()
   */
  public void close()
  {
    try {
      synchronized (lock) {
        if (out == null)
          return;
        out.close();
        out = null;
      }
    } catch (IOException x) {
      trouble = true;
    }
  }

  /**
   * Flush the stream if it's not closed and check its error state. Errors are cumulative; once the stream encounters an error, this routine
   * will return true on all successive calls.
   * 
   * @return True if the print stream has encountered an error, either on the underlying output stream or during a format conversion.
   */
  public boolean checkError()
  {
    if (out != null)
      flush();
    return trouble;
  }

  /** Indicate that an error has occurred. */
  protected void setError()
  {
    trouble = true;
    try {
      throw new IOException();
    } catch (IOException x) {
    }
  }

  /*
   * Exception-catching, synchronized output operations, which also implement the write() methods of Writer
   */

  /**
   * Write a single character.
   * 
   * @param c int specifying a character to be written.
   */
  public void write(int c)
  {
    try {
      synchronized (lock) {
        ensureOpen();
        out.write(c);
      }
    } catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
    } catch (IOException x) {
      trouble = true;
    }
  }

  /**
   * Write A Portion of an array of characters.
   * 
   * @param buf Array of characters
   * @param off Offset from which to start writing characters
   * @param len Number of characters to write
   */
  public void write(char buf[], int off, int len)
  {
    try {
      synchronized (lock) {
        ensureOpen();
        out.write(buf, off, len);
      }
    } catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
    } catch (IOException x) {
      trouble = true;
    }
  }

  /**
   * Write an array of characters. This method cannot be inherited from the Writer class because it must suppress I/O exceptions.
   * 
   * @param buf Array of characters to be written
   */
  public void write(char buf[])
  {
    write(buf, 0, buf.length);
  }

  /**
   * Write a portion of a string.
   * 
   * @param s A String
   * @param off Offset from which to start writing characters
   * @param len Number of characters to write
   */
  public void write(String s, int off, int len)
  {
    try {
      synchronized (lock) {
        ensureOpen();
        out.write(s, off, len);
      }
    } catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
    } catch (IOException x) {
      trouble = true;
    }
  }

  /**
   * Write a string. This method cannot be inherited from the Writer class because it must suppress I/O exceptions.
   * 
   * @param s String to be written
   */
  public void write(String s)
  {
    if (s == null) {
      return;
    }
    write(s, 0, s.length());
  }

  private void newLine()
  {
    try {
      synchronized (lock) {
        ensureOpen();
        out.write(lineSeparator);
        if (autoFlush)
          out.flush();
      }
    } catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
    } catch (IOException x) {
      trouble = true;
    }
  }

  /* Methods that do not terminate lines */

  /**
   * Print a boolean value. The string produced by <code>{@link
   * java.lang.String#valueOf(boolean)}</code> is translated into bytes according to the
   * platform's default character encoding, and these bytes are written in exactly the manner of the <code>{@link
   * #write(int)}</code> method.
   * 
   * @param b The <code>boolean</code> to be printed
   */
  public void print(boolean b)
  {
    write(b ? "true" : "false");
  }

  /**
   * Print a character. The character is translated into one or more bytes according to the platform's default character encoding, and these
   * bytes are written in exactly the manner of the <code>{@link
   * #write(int)}</code> method.
   * 
   * @param c The <code>char</code> to be printed
   */
  public void print(char c)
  {
    write(c);
  }

  /**
   * Print an integer. The string produced by <code>{@link
   * java.lang.String#valueOf(int)}</code> is translated into bytes according to the platform's
   * default character encoding, and these bytes are written in exactly the manner of the <code>{@link #write(int)}</code> method.
   * 
   * @param i The <code>int</code> to be printed
   * @see java.lang.Integer#toString(int)
   */
  public void print(int i)
  {
    write(String.valueOf(i));
  }

  /**
   * Print a long integer. The string produced by <code>{@link
   * java.lang.String#valueOf(long)}</code> is translated into bytes according to the
   * platform's default character encoding, and these bytes are written in exactly the manner of the <code>{@link #write(int)}</code>
   * method.
   * 
   * @param l The <code>long</code> to be printed
   * @see java.lang.Long#toString(long)
   */
  public void print(long l)
  {
    write(String.valueOf(l));
  }

  /**
   * Print a floating-point number. The string produced by <code>{@link
   * java.lang.String#valueOf(float)}</code> is translated into bytes according to the
   * platform's default character encoding, and these bytes are written in exactly the manner of the <code>{@link #write(int)}</code>
   * method.
   * 
   * @param f The <code>float</code> to be printed
   * @see java.lang.Float#toString(float)
   */
  public void print(float f)
  {
    write(String.valueOf(f));
  }

  /**
   * Print a double-precision floating-point number. The string produced by <code>{@link java.lang.String#valueOf(double)}</code> is
   * translated into bytes according to the platform's default character encoding, and these bytes are written in exactly the manner of the
   * <code>{@link
   * #write(int)}</code> method.
   * 
   * @param d The <code>double</code> to be printed
   * @see java.lang.Double#toString(double)
   */
  public void print(double d)
  {
    write(String.valueOf(d));
  }

  /**
   * Print an array of characters. The characters are converted into bytes according to the platform's default character encoding, and these
   * bytes are written in exactly the manner of the <code>{@link #write(int)}</code> method.
   * 
   * @param s The array of chars to be printed
   * 
   * @throws NullPointerException If <code>s</code> is <code>null</code>
   */
  public void print(char s[])
  {
    write(s);
  }

  /**
   * Print a string. If the argument is <code>null</code> then the string <code>"null"</code> is printed. Otherwise, the string's characters
   * are converted into bytes according to the platform's default character encoding, and these bytes are written in exactly the manner of
   * the <code>{@link #write(int)}</code> method.
   * 
   * @param s The <code>String</code> to be printed
   */
  public void print(String s)
  {
    if (s == null) {
      s = "null";
    }
    write(s);
  }

  /**
   * Print an object. The string produced by the <code>{@link
   * java.lang.String#valueOf(Object)}</code> method is translated into bytes according to the
   * platform's default character encoding, and these bytes are written in exactly the manner of the <code>{@link #write(int)}</code>
   * method.
   * 
   * @param obj The <code>Object</code> to be printed
   * @see java.lang.Object#toString()
   */
  public void print(Object obj)
  {
    write(String.valueOf(obj));
  }

  /* Methods that do terminate lines */

  /**
   * Terminate the current line by writing the line separator string. The line separator string is defined by the system property
   * <code>line.separator</code>, and is not necessarily a single newline character (<code>'\n'</code>).
   */
  public void println()
  {
    newLine();
  }

  /**
   * Print a boolean value and then terminate the line. This method behaves as though it invokes <code>{@link #print(boolean)}</code> and
   * then <code>{@link #println()}</code>.
   * 
   * @param x the <code>boolean</code> value to be printed
   */
  public void println(boolean x)
  {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * Print a character and then terminate the line. This method behaves as though it invokes <code>{@link #print(char)}</code> and then
   * <code>{@link
   * #println()}</code>.
   * 
   * @param x the <code>char</code> value to be printed
   */
  public void println(char x)
  {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * Print an integer and then terminate the line. This method behaves as though it invokes <code>{@link #print(int)}</code> and then
   * <code>{@link
   * #println()}</code>.
   * 
   * @param x the <code>int</code> value to be printed
   */
  public void println(int x)
  {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * Print a long integer and then terminate the line. This method behaves as though it invokes <code>{@link #print(long)}</code> and then
   * <code>{@link #println()}</code>.
   * 
   * @param x the <code>long</code> value to be printed
   */
  public void println(long x)
  {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * Print a floating-point number and then terminate the line. This method behaves as though it invokes <code>{@link #print(float)}</code>
   * and then <code>{@link #println()}</code>.
   * 
   * @param x the <code>float</code> value to be printed
   */
  public void println(float x)
  {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * Print a double-precision floating-point number and then terminate the line. This method behaves as though it invokes <code>{@link
   * #print(double)}</code>
   * and then <code>{@link #println()}</code>.
   * 
   * @param x the <code>double</code> value to be printed
   */
  public void println(double x)
  {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * Print an array of characters and then terminate the line. This method behaves as though it invokes <code>{@link #print(char[])}</code>
   * and then <code>{@link #println()}</code>.
   * 
   * @param x the array of <code>char</code> values to be printed
   */
  public void println(char x[])
  {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * Print a String and then terminate the line. This method behaves as though it invokes <code>{@link #print(String)}</code> and then
   * <code>{@link #println()}</code>.
   * 
   * @param x the <code>String</code> value to be printed
   */
  public void println(String x)
  {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * Print an Object and then terminate the line. This method behaves as though it invokes <code>{@link #print(Object)}</code> and then
   * <code>{@link #println()}</code>.
   * 
   * @param x the <code>Object</code> value to be printed
   */
  public void println(Object x)
  {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * A convenience method to write a formatted string to this writer using the specified format string and arguments. If automatic flushing
   * is enabled, calls to this method will flush the output buffer.
   * 
   * <p>
   * An invocation of this method of the form <tt>out.printf(format,
   * args)</tt> behaves in exactly the same way as the invocation
   * 
   * <pre>
   * out.format(format, args)
   * </pre>
   * 
   * @param format A format string as described in <a href="../util/Formatter.html#syntax">Format string syntax</a>.
   * 
   * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the
   *          extra arguments are ignored. The number of arguments is variable and may be zero. The maximum number of arguments is limited
   *          by the maximum dimension of a Java array as defined by the <a href="http://java.sun.com/docs/books/vmspec/">Java Virtual
   *          Machine Specification</a>. The behaviour on a <tt>null</tt> argument depends on the <a
   *          href="../util/Formatter.html#syntax">conversion</a>.
   * 
   * @throws IllegalFormatException If a format string contains an illegal syntax, a format specifier that is incompatible with the given
   *           arguments, insufficient arguments given the format string, or other illegal conditions. For specification of all possible
   *           formatting errors, see the <a href="../util/Formatter.html#detail">Details</a> section of the formatter class specification.
   * 
   * @throws NullPointerException If the <tt>format</tt> is <tt>null</tt>
   * 
   * @return This writer
   * 
   * @since 1.5
   */
  public PrintWriterPatched printf(String format, Object... args)
  {
    return format(format, args);
  }

  /**
   * A convenience method to write a formatted string to this writer using the specified format string and arguments. If automatic flushing
   * is enabled, calls to this method will flush the output buffer.
   * 
   * <p>
   * An invocation of this method of the form <tt>out.printf(l, format,
   * args)</tt> behaves in exactly the same way as the invocation
   * 
   * <pre>
   * out.format(l, format, args)
   * </pre>
   * 
   * @param l The {@linkplain java.util.Locale locale} to apply during formatting. If <tt>l</tt> is <tt>null</tt> then no localization is
   *          applied.
   * 
   * @param format A format string as described in <a href="../util/Formatter.html#syntax">Format string syntax</a>.
   * 
   * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the
   *          extra arguments are ignored. The number of arguments is variable and may be zero. The maximum number of arguments is limited
   *          by the maximum dimension of a Java array as defined by the <a href="http://java.sun.com/docs/books/vmspec/">Java Virtual
   *          Machine Specification</a>. The behaviour on a <tt>null</tt> argument depends on the <a
   *          href="../util/Formatter.html#syntax">conversion</a>.
   * 
   * @throws IllegalFormatException If a format string contains an illegal syntax, a format specifier that is incompatible with the given
   *           arguments, insufficient arguments given the format string, or other illegal conditions. For specification of all possible
   *           formatting errors, see the <a href="../util/Formatter.html#detail">Details</a> section of the formatter class specification.
   * 
   * @throws NullPointerException If the <tt>format</tt> is <tt>null</tt>
   * 
   * @return This writer
   * 
   * @since 1.5
   */
  public PrintWriterPatched printf(Locale l, String format, Object... args)
  {
    return format(l, format, args);
  }

  /**
   * Writes a formatted string to this writer using the specified format string and arguments. If automatic flushing is enabled, calls to
   * this method will flush the output buffer.
   * 
   * <p>
   * The locale always used is the one returned by {@link java.util.Locale#getDefault() Locale.getDefault()}, regardless of any previous
   * invocations of other formatting methods on this object.
   * 
   * @param format A format string as described in <a href="../util/Formatter.html#syntax">Format string syntax</a>.
   * 
   * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the
   *          extra arguments are ignored. The number of arguments is variable and may be zero. The maximum number of arguments is limited
   *          by the maximum dimension of a Java array as defined by the <a href="http://java.sun.com/docs/books/vmspec/">Java Virtual
   *          Machine Specification</a>. The behaviour on a <tt>null</tt> argument depends on the <a
   *          href="../util/Formatter.html#syntax">conversion</a>.
   * 
   * @throws IllegalFormatException If a format string contains an illegal syntax, a format specifier that is incompatible with the given
   *           arguments, insufficient arguments given the format string, or other illegal conditions. For specification of all possible
   *           formatting errors, see the <a href="../util/Formatter.html#detail">Details</a> section of the Formatter class specification.
   * 
   * @throws NullPointerException If the <tt>format</tt> is <tt>null</tt>
   * 
   * @return This writer
   * 
   * @since 1.5
   */
  public PrintWriterPatched format(String format, Object... args)
  {
    try {
      synchronized (lock) {
        ensureOpen();
        if ((formatter == null) || (formatter.locale() != Locale.getDefault()))
          formatter = new Formatter(this);
        formatter.format(Locale.getDefault(), format, args);
        if (autoFlush)
          out.flush();
      }
    } catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
    } catch (IOException x) {
      trouble = true;
    }
    return this;
  }

  /**
   * Writes a formatted string to this writer using the specified format string and arguments. If automatic flushing is enabled, calls to
   * this method will flush the output buffer.
   * 
   * @param l The {@linkplain java.util.Locale locale} to apply during formatting. If <tt>l</tt> is <tt>null</tt> then no localization is
   *          applied.
   * 
   * @param format A format string as described in <a href="../util/Formatter.html#syntax">Format string syntax</a>.
   * 
   * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the
   *          extra arguments are ignored. The number of arguments is variable and may be zero. The maximum number of arguments is limited
   *          by the maximum dimension of a Java array as defined by the <a href="http://java.sun.com/docs/books/vmspec/">Java Virtual
   *          Machine Specification</a>. The behaviour on a <tt>null</tt> argument depends on the <a
   *          href="../util/Formatter.html#syntax">conversion</a>.
   * 
   * @throws IllegalFormatException If a format string contains an illegal syntax, a format specifier that is incompatible with the given
   *           arguments, insufficient arguments given the format string, or other illegal conditions. For specification of all possible
   *           formatting errors, see the <a href="../util/Formatter.html#detail">Details</a> section of the formatter class specification.
   * 
   * @throws NullPointerException If the <tt>format</tt> is <tt>null</tt>
   * 
   * @return This writer
   * 
   * @since 1.5
   */
  public PrintWriterPatched format(Locale l, String format, Object... args)
  {
    try {
      synchronized (lock) {
        ensureOpen();
        if ((formatter == null) || (formatter.locale() != l))
          formatter = new Formatter(this, l);
        formatter.format(l, format, args);
        if (autoFlush)
          out.flush();
      }
    } catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
    } catch (IOException x) {
      trouble = true;
    }
    return this;
  }

  /**
   * Appends the specified character sequence to this writer.
   * 
   * <p>
   * An invocation of this method of the form <tt>out.append(csq)</tt> behaves in exactly the same way as the invocation
   * 
   * <pre>
   * out.write(csq.toString())
   * </pre>
   * 
   * <p>
   * Depending on the specification of <tt>toString</tt> for the character sequence <tt>csq</tt>, the entire sequence may not be appended.
   * For instance, invoking the <tt>toString</tt> method of a character buffer will return a subsequence whose content depends upon the
   * buffer's position and limit.
   * 
   * @param csq The character sequence to append. If <tt>csq</tt> is <tt>null</tt>, then the four characters <tt>"null"</tt> are appended to
   *          this writer.
   * 
   * @return This writer
   * 
   * @since 1.5
   */
  public PrintWriterPatched append(CharSequence csq)
  {
    if (csq == null)
      write("null");
    else
      write(csq.toString());
    return this;
  }

  /**
   * Appends a subsequence of the specified character sequence to this writer.
   * 
   * <p>
   * An invocation of this method of the form <tt>out.append(csq, start,
   * end)</tt> when <tt>csq</tt> is not <tt>null</tt>, behaves in exactly the same way as the invocation
   * 
   * <pre>
   * out.write(csq.subSequence(start, end).toString())
   * </pre>
   * 
   * @param csq The character sequence from which a subsequence will be appended. If <tt>csq</tt> is <tt>null</tt>, then characters will be
   *          appended as if <tt>csq</tt> contained the four characters <tt>"null"</tt>.
   * 
   * @param start The index of the first character in the subsequence
   * 
   * @param end The index of the character following the last character in the subsequence
   * 
   * @return This writer
   * 
   * @throws IndexOutOfBoundsException If <tt>start</tt> or <tt>end</tt> are negative, <tt>start</tt> is greater than <tt>end</tt>, or
   *           <tt>end</tt> is greater than <tt>csq.length()</tt>
   * 
   * @since 1.5
   */
  public PrintWriterPatched append(CharSequence csq, int start, int end)
  {
    CharSequence cs = (csq == null ? "null" : csq);
    write(cs.subSequence(start, end).toString());
    return this;
  }

  /**
   * Appends the specified character to this writer.
   * 
   * <p>
   * An invocation of this method of the form <tt>out.append(c)</tt> behaves in exactly the same way as the invocation
   * 
   * <pre>
   * out.write(c)
   * </pre>
   * 
   * @param c The 16-bit character to append
   * 
   * @return This writer
   * 
   * @since 1.5
   */
  public PrintWriterPatched append(char c)
  {
    write(c);
    return this;
  }
}
