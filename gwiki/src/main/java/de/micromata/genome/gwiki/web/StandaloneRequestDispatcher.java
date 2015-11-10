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

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Standalone servlet request dispatcher for batch processing and unit tests.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class StandaloneRequestDispatcher implements RequestDispatcher
{
  private String url;

  public StandaloneRequestDispatcher(String url)
  {
    this.url = url;
  }

  public void forward(ServletRequest req, ServletResponse res) throws ServletException, IOException
  {
    getStandaloneRequest(req).setForwardUrl(this.url);
  }

  public void include(ServletRequest req, ServletResponse res) throws ServletException, IOException
  {
    getStandaloneRequest(req).addIncludedUrl(this.url);
  }

  public StandaloneHttpServletRequest getStandaloneRequest(ServletRequest request)
  {
    while (request != null & !(request instanceof StandaloneHttpServletRequest)) {
      request = ((HttpServletRequestWrapper) request).getRequest();
    }

    return (StandaloneHttpServletRequest) request;
  }
}
