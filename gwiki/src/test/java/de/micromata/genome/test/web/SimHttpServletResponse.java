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

package de.micromata.genome.test.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import de.micromata.genome.gwiki.web.StandaloneServletOutputStream;

/**
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SimHttpServletResponse implements HttpServletResponse// extends MockHttpServletResponse
{
  private List<Cookie> cookies = new ArrayList<Cookie>();

  private Map<String, List<Object>> headers = new HashMap<String, List<Object>>();

  private int status = 200;

  private String errorMessage;

  private String redirectUrl;

  private String characterEncoding = "UTF-8";

  private String contentType = "text/html";

  private int contentLength = 0;

  private Locale locale = Locale.getDefault();

  private StandaloneServletOutputStream simOut = new StandaloneServletOutputStream();

  private PrintWriter writer = new PrintWriter(simOut, true);

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie)
   */
  public void addCookie(Cookie cookie)
  {
    cookies.add(cookie);

  }

  protected <T> void addHeaderIntern(String name, T value)
  {
    List<Object> hd = headers.get(name);
    if (hd == null) {
      hd = new ArrayList<Object>();
      headers.put(name, hd);
    }
    hd.add(value);
  }

  protected <T> void setHeaderIntern(String name, T value)
  {
    headers.remove(name);
    addHeaderIntern(name, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
   */
  public void addDateHeader(String name, long date)
  {
    addHeaderIntern(name, date);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
   */
  public void addHeader(String name, String value)
  {
    addHeaderIntern(name, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String, int)
   */
  public void addIntHeader(String name, int value)
  {
    addHeaderIntern(name, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
   */
  public boolean containsHeader(String name)
  {
    return headers.containsKey(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
   */
  public String encodeRedirectURL(String url)
  {
    return url;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl(java.lang.String)
   */
  public String encodeRedirectUrl(String url)
  {
    return encodeRedirectURL(url);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
   */
  public String encodeURL(String url)
  {
    return url;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#encodeUrl(java.lang.String)
   */
  public String encodeUrl(String url)
  {
    return encodeURL(url);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#sendError(int)
   */
  public void sendError(int sc) throws IOException
  {
    status = sc;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#sendError(int, java.lang.String)
   */
  public void sendError(int sc, String msg) throws IOException
  {
    status = sc;
    errorMessage = msg;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
   */
  public void sendRedirect(String location) throws IOException
  {
    this.redirectUrl = location;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
   */
  public void setDateHeader(String name, long date)
  {
    setHeaderIntern(name, date);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
   */
  public void setHeader(String name, String value)
  {
    setHeaderIntern(name, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
   */
  public void setIntHeader(String name, int value)
  {
    setHeaderIntern(name, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#setStatus(int)
   */
  public void setStatus(int sc)
  {
    status = sc;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletResponse#setStatus(int, java.lang.String)
   */
  public void setStatus(int sc, String sm)
  {
    status = sc;
    errorMessage = sm;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#flushBuffer()
   */
  public void flushBuffer() throws IOException
  {
    // nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#getBufferSize()
   */
  public int getBufferSize()
  {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#getCharacterEncoding()
   */
  public String getCharacterEncoding()
  {
    return characterEncoding;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#getContentType()
   */
  public String getContentType()
  {
    return contentType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#getLocale()
   */
  public Locale getLocale()
  {
    return locale;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#getOutputStream()
   */
  public ServletOutputStream getOutputStream() throws IOException
  {
    return simOut;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#getWriter()
   */
  public PrintWriter getWriter() throws IOException
  {
    return writer;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#isCommitted()
   */
  public boolean isCommitted()
  {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#reset()
   */
  public void reset()
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#resetBuffer()
   */
  public void resetBuffer()
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#setBufferSize(int)
   */
  public void setBufferSize(int size)
  {

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#setCharacterEncoding(java.lang.String)
   */
  public void setCharacterEncoding(String charset)
  {
    characterEncoding = charset;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#setContentLength(int)
   */
  public void setContentLength(int len)
  {
    contentLength = len;

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
   */
  public void setContentType(String type)
  {
    contentType = type;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
   */
  public void setLocale(Locale loc)
  {
    locale = loc;
  }

  public List<Cookie> getCookies()
  {
    return cookies;
  }

  public void setCookies(List<Cookie> cookies)
  {
    this.cookies = cookies;
  }

  public Map<String, List<Object>> getHeaders()
  {
    return headers;
  }

  public void setHeaders(Map<String, List<Object>> headers)
  {
    this.headers = headers;
  }

  public String getErrorMessage()
  {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage)
  {
    this.errorMessage = errorMessage;
  }

  public String getRedirectUrl()
  {
    return redirectUrl;
  }

  public void setRedirectUrl(String redirectUrl)
  {
    this.redirectUrl = redirectUrl;
  }

  public int getStatus()
  {
    return status;
  }

  public int getContentLength()
  {
    return contentLength;
  }

  public void setWriter(PrintWriter writer)
  {
    this.writer = writer;
  }

  public StandaloneServletOutputStream getSimOut()
  {
    return simOut;
  }

  public void setSimOut(StandaloneServletOutputStream simOut)
  {
    this.simOut = simOut;
  }

}
