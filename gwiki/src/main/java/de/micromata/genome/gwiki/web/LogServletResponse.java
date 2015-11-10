////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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
