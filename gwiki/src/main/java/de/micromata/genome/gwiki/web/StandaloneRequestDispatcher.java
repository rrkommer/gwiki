/////////////////////////////////////////////////////////////////////////////
//
// Project   genome-gwiki-standalone
//
// Author    roger@micromata.de
// Created   28.12.2009
// Copyright Micromata 28.12.2009
//
/////////////////////////////////////////////////////////////////////////////
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
 * @author roger
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
