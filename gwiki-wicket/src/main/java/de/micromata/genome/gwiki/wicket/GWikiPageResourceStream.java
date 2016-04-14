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

package de.micromata.genome.gwiki.wicket;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import org.apache.wicket.util.resource.AbstractStringResourceStream;
import org.apache.wicket.util.time.Time;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;

/**
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPageResourceStream extends AbstractStringResourceStream
{

  private static final long serialVersionUID = 2976761416648716062L;

  protected String path;

  protected Locale locale;

  protected Charset charSet;

  public GWikiPageResourceStream(String path)
  {
    this.path = path;
  }

  public void close() throws IOException
  {
  }

  public String getContentType()
  {
    // TODO
    return "text/html";
    // return element.getElementInfo().getProps().getStringValue(GWikiPropKeys.);
    // return element.getElementInfo().getProps().getStringValue(GWikiPropKeys.);
  }

  public Locale getLocale()
  {
    return locale;
  }

  public void setLocale(Locale locale)
  {
    this.locale = locale;
  }

  /**
   * @see org.apache.wicket.util.watch.IModifiable#lastModifiedTime()
   */
  public Time lastModifiedTime()
  {
    GWikiElementInfo ei = GWikiWeb.getWiki().findElementInfo(path);
    if (ei == null) {
      return Time.now();
    }
    long time = ei.getModifiedAt().getTime();
    return Time.milliseconds(time);
  }

  public String getString()
  {
    GWikiStandaloneContext wikiContext = GWikiStandaloneContext.create();
    try {
      GWikiContext.setCurrent(wikiContext);
      GWikiElement el = wikiContext.getWikiWeb().findElement(path);
      if (el == null) {
        return "";
      }
      el.serve(wikiContext);
      String ret = wikiContext.getOutString();
      return ret;
    } finally {
      GWikiContext.setCurrent(null);
    }
  }

  public void setCharset(Charset charset)
  {
    this.charSet = charset;
  }

}
