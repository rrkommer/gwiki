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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.Map;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.gspt.GenomeTemplateUtils;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * Implements a macro, which embedds a groovy script.
 * 
 * Needs GWIKI_DEVELOPER right to safe.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiGroovyScriptMacro implements GWikiMacro, GWikiRuntimeMacro
{

  private static final long serialVersionUID = 5567718564219668702L;

  private Script script = null;

  protected void compileScript(MacroAttributes attrs, GWikiContext ctx)
  {
    GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader());
    Map<String, Object> m = GenomeTemplateUtils.createBinding(ctx);
    for (Map.Entry<String, Object> me : m.entrySet()) {
      shell.setVariable(me.getKey(), me.getValue());
    }
    script = shell.parse(attrs.getBody());
  }

  protected Map<String, Object> createBinding(MacroAttributes attrs, GWikiContext ctx)
  {
    Map<String, Object> binding = GenomeTemplateUtils.createBinding(ctx);
    binding.put("out", ctx.getCreatePageContext().getOut());
    return binding;
  }

  public boolean render(MacroAttributes attrs, GWikiContext ctx)
  {
    if (script == null) {
      compileScript(attrs, ctx);
    }
    script.setBinding(new Binding(createBinding(attrs, ctx)));
    script.run();
    return true;
  }

  public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException
  {
    ctx.getWikiWeb().getAuthorization().ensureAllowTo(ctx, GWikiAuthorizationRights.GWIKI_DEVELOPER.name());
  }

  public boolean evalBody()
  {
    return false;
  }

  public int getRenderModes()
  {
    return 0;
  }

  public boolean hasBody()
  {
    return true;
  }

}
