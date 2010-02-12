/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   05.12.2009
// Copyright Micromata 05.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.ObjectUtils;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Base implementation for GWikiLogging.
 * 
 * @author roger
 * 
 */
public abstract class GWikiLoggingBase implements GWikiLogging
{

  protected String renderLog(String message, GWikiContext ctx, Throwable ex, Object... keyValues)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(message);
    renderLogAttrs(sb, ctx, ex, keyValues);
    return sb.toString();
  }

  protected void renderLogAttrs(StringBuilder sb, GWikiContext ctx, Throwable ex, Object... keyValues)
  {

    for (int i = 0; i + 1 < keyValues.length; ++i) {
      Object k = keyValues[i];
      if ((k instanceof GLogAttributeName) == false) {
        ++i;
        continue;
      }
      GLogAttributeName kn = (GLogAttributeName) k;
      sb.append(" ").append(kn.name()).append(": ").append(ObjectUtils.toString(keyValues[i + 1])).append("\n");
      ++i;
    }
    if (ctx != null) {
      if (ctx.getWikiElement() != null) {
        sb.append(" ").append(GLogAttributeNames.PageId.name()).append(": ").append(ctx.getWikiElement().getElementInfo().getId()).append(
            "\n");
      }
    }
    if (ex != null) {
      StringWriter sout = new StringWriter();
      PrintWriter pout = new PrintWriter(sout);
      ex.printStackTrace(pout);
      sb.append(" ").append(GLogAttributeNames.TechReasonException.name()).append(": ").append(sout.getBuffer().toString());
    }
  }
}
