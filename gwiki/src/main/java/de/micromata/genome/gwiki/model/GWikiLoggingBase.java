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

package de.micromata.genome.gwiki.model;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

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
    if (keyValues.length > 0) {
      sb.append("|");
    }
    for (int i = 0; i + 1 < keyValues.length; ++i) {
      Object k = keyValues[i];
      String key = "";
      if ((k instanceof String) == true) {
        key = (String) k;
      } else if ((k instanceof GLogAttributeName) == true) {
        GLogAttributeName kn = (GLogAttributeName) k;
        key = kn.name();
      } else {
        ++i;
        continue;
      }

      sb.append(" ").append(key).append(": ").append(ObjectUtils.toString(keyValues[i + 1])).append("\n");
      ++i;
    }
    if (ctx != null) {
      if (ctx.getCurrentElement() != null) {
        sb.append(" ").append(GLogAttributeNames.PageId.name()).append(": ").append(ctx.getCurrentElement().getElementInfo().getId())
            .append("\n");
      }
      String userName = ctx.getWikiWeb().getAuthorization().getCurrentUserName(ctx);
      if (StringUtils.isNotBlank(userName) == true) {
        sb.append(" userName: ").append(userName).append("\n");
      }

    }
    if (ex != null) {
      StringWriter sout = new StringWriter();
      PrintWriter pout = new PrintWriter(sout);
      ex.printStackTrace(pout);
      sb.append(" ").append(GLogAttributeNames.TechReasonException.name()).append(": ").append(sout.getBuffer().toString());
    }
  }

  @Override
  public void reinitConfig()
  {
    // nothing here
  }
}
