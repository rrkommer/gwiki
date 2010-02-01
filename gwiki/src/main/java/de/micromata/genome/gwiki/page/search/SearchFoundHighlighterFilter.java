/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   10.12.2009
// Copyright Micromata 10.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilterEvent;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPage;
import de.micromata.genome.gwiki.page.search.expr.SearchHilightHtmlFilter;
import de.micromata.genome.util.runtime.RuntimeIOException;
import de.micromata.genome.util.types.Converter;
import de.micromata.genome.util.types.Holder;

public class SearchFoundHighlighterFilter implements GWikiServeElementFilter
{

  public Void filter(GWikiFilterChain<Void, GWikiServeElementFilterEvent, GWikiServeElementFilter> chain, GWikiServeElementFilterEvent event)
  {
    String words = event.getWikiContext().getRequestParameter("_gwhiwords");
    if (StringUtils.isEmpty(words) == true) {
      return chain.nextFilter(event);
    }
    GWikiElement el = event.getElement();
    if (el == null || (el instanceof GWikiWikiPage) == false) {
      return chain.nextFilter(event);
    }
    // el.getElementInfo().get

    HttpServletResponse resp = event.getWikiContext().getResponse();
    //String contentType = resp.getContentType();
    // if (StringUtils.equals(contentType, "text/html") == false) {
    // return chain.nextFilter(event);
    // }
    final StringWriter sout = new StringWriter();
    final PrintWriter pout = new PrintWriter(sout);
    final Holder<Boolean> skip = new Holder<Boolean>(Boolean.FALSE);
    HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(resp) {

      @Override
      public void sendRedirect(String location) throws IOException
      {
        skip.set(Boolean.TRUE);
        super.sendRedirect(location);
      }

      @Override
      public ServletOutputStream getOutputStream() throws IOException
      {
        skip.set(Boolean.TRUE);
        return super.getOutputStream();
      }

      @Override
      public PrintWriter getWriter() throws IOException
      {
        return pout;
      }

      @Override
      public void resetBuffer()
      {
        sout.getBuffer().setLength(0);
      }
    };
    event.getWikiContext().setResponse(wrapper);
    chain.nextFilter(event);
    if (skip.get() == Boolean.TRUE) {
      return null;
    }
    try {
      PrintWriter pr = resp.getWriter();
      String orgString = sout.getBuffer().toString();
      if (StringUtils.containsIgnoreCase(orgString, "<html") == false) {
        pr.print(orgString);
        return null;
      }

      StringWriter filteredContent = new StringWriter();
      SearchHilightHtmlFilter filter = new SearchHilightHtmlFilter(filteredContent, Converter.parseStringTokens(words, ", ", false));
      filter.doFilter(orgString);
      // System.out.println("\n\nOrig:\n" + sout.getBuffer().toString() + "\n\nFiltered:\n" + filteredContent.getBuffer().toString());

      
//      pr
//          .println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
      pr.print(filteredContent.getBuffer().toString());
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
    return null;
  }
}
