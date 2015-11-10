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

package de.micromata.genome.gwiki.model.matcher;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.matcher.Matcher;

/**
 * Matcher to match agains a property inside a properties file.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiElementPropMatcher extends GWikiElementMatcherBase
{

  private static final long serialVersionUID = 4885823399615447244L;

  protected String propName;

  protected Matcher<String> propValueMatcher;

  public GWikiElementPropMatcher(GWikiContext wikiContex, String propName, Matcher<String> propValueMatcher)
  {
    super(wikiContex);
    this.propName = propName;
    this.propValueMatcher = propValueMatcher;

  }

  @Override
  public boolean match(GWikiElementInfo ei)
  {
    return propValueMatcher.match(ei.getProps().getStringValue(propName));
  }

}
