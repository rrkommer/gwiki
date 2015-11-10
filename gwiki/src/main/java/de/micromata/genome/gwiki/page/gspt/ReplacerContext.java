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

package de.micromata.genome.gwiki.page.gspt;

import java.util.HashMap;
import java.util.Map;

/**
 * Context for Replacer
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class ReplacerContext
{
  public static final String SCRIPTFILENAME = "de.micromata.web.gspt.ReplacerContext.scriptFileName";

  public static final String COMPILECONTEXT = "de.micromata.web.gspt.ReplacerContext.compileContext";

  private Map<String, Object> attributes = new HashMap<String, Object>();

  public ReplacerContext()
  {

  }

  public static ReplacerContext createReplacer(String fileName, Map<String, Object> compileContext)
  {
    ReplacerContext ctx = new ReplacerContext();
    ctx.getAttributes().putAll(compileContext);
    ctx.setAttribute(SCRIPTFILENAME, fileName);
    ctx.setAttribute(COMPILECONTEXT, compileContext);
    return ctx;
  }

  public Object getAttribute(String key)
  {
    return attributes.get(key);
  }

  public void setAttribute(String key, Object value)
  {
    attributes.put(key, value);
  }

  public void removeAttribute(String key)
  {
    attributes.remove(key);
  }

  public Map<String, Object> getAttributes()
  {
    return attributes;
  }

  public void setAttributes(Map<String, Object> attributes)
  {
    this.attributes = attributes;
  }
}
