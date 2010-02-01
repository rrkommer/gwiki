/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   24.10.2009
// Copyright Micromata 24.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.types.TimeInMillis;
import de.micromata.genome.util.web.MimeUtils;

public class StaticFileServlet extends HttpServlet
{

  private static final long serialVersionUID = -3926571626495438910L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    String uri = req.getPathInfo();
    String servletp = req.getServletPath();
    String respath = servletp + uri;
    if (uri == null) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    InputStream is = getServletContext().getResourceAsStream(respath);
    if (is == null) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    long nt = new Date().getTime() + TimeInMillis.DAY;
    String mime = MimeUtils.getMimeTypeFromFile(respath);
    if (StringUtils.equals(mime, "application/x-shockwave-flash")) {
      resp.setHeader("Cache-Control", "cache, must-revalidate");
      resp.setHeader("Pragma", "public");
    }
    resp.setDateHeader("Expires", nt);
    resp.setHeader("Cache-Control", "max-age=86400, public");
    if (mime != null) {
      resp.setContentType(mime);
    }

    byte[] data = IOUtils.toByteArray(is);
    IOUtils.closeQuietly(is);
    resp.setContentLength((int) data.length);
    IOUtils.write(data, resp.getOutputStream());
  }
}
