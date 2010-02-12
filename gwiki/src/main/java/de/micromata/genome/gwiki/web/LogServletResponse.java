/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   15.11.2009
// Copyright Micromata 15.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.web;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import com.bradmcevoy.http.ServletResponse;

/**
 * Servlet response wrapper to log responses.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class LogServletResponse extends ServletResponse
{
  public StringBuilder sb = new StringBuilder();

  public LogServletResponse(HttpServletResponse r)
  {
    super(r);
  }

  @Override
  public void sendRedirect(String url)
  {
    sb.append("REDIRECTTO: ").append(url).append("\n");
    ;
    super.sendRedirect(url);
  }

  @Override
  protected void setAnyDateHeader(Header name, Date date)
  {
    sb.append(name.code).append(": ").append(date).append("\n");
    super.setAnyDateHeader(name, date);
  }

  @Override
  public void setNonStandardHeader(String name, String value)
  {
    sb.append(name).append(": ").append(value).append("\n");
    super.setNonStandardHeader(name, value);
  }

  @Override
  public void setStatus(Status status)
  {
    sb.append("STATUS: " + status.name()).append("\n");
    ;
    super.setStatus(status);
  }

  @Override
  public void setResponseHeader(Header header, String value)
  {
    sb.append(header.code).append(": ").append(value).append("\n");
    ;
    super.setResponseHeader(header, value);
  }

}
