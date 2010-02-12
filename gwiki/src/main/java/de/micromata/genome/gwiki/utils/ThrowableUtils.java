/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   03.11.2009
// Copyright Micromata 03.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Utils for handling throwables.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class ThrowableUtils
{
  public static String getExceptionStacktraceForHtml(Throwable ex)
  {
    if (ex == null) {
      return StringEscapeUtils.escapeHtml("<no exception set>");
    }
    StringWriter sout = new StringWriter();
    PrintWriter pout = new PrintWriter(sout);
    ex.printStackTrace(pout);
    return StringEscapeUtils.escapeHtml(sout.getBuffer().toString());
  }
}
