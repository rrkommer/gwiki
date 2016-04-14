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

import de.micromata.genome.gdbfs.MimeUtils;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * @deprecated the static content will also be provided by the GWikiServlet.
 * 
 */
@Deprecated
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
    resp.setContentLength(data.length);
    IOUtils.write(data, resp.getOutputStream());
  }
}
