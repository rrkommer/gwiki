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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;

/**
 * Hide the body from output, so content is only visible in wiki source.
 * 
 * @author roger
 * 
 */
@MacroInfo(info = "Hide the body from output, so content is only visible in wiki source.")
public class GWikiHiddenMacro extends GWikiMacroBean implements GWikiBodyMacro, GWikiRuntimeMacro
{

  private static final long serialVersionUID = 8748922669997778824L;

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    return true;
  }
}
