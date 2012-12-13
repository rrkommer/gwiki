////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
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

package de.micromata.genome.gwiki.model;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.ObjectUtils;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Base implementation for GWikiLogging.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiLoggingBase extends GWikiLoggingAdapter
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
        sb.append(" ").append(GLogAttributeNames.PageId.name()).append(": ").append(ctx.getWikiElement().getElementInfo().getId())
            .append("\n");
      }
    }
    if (ex != null) {
      StringWriter sout = new StringWriter();
      PrintWriter pout = new PrintWriter(sout);
      ex.printStackTrace(pout);
      sb.append(" ").append(GLogAttributeNames.TechReasonException.name()).append(": ").append(sout.getBuffer().toString());
    }
  }

  public void reinitConfig()
  {
    // nothing here
  }
}
