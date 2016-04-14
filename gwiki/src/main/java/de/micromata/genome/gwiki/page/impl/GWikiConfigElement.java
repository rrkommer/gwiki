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

package de.micromata.genome.gwiki.page.impl;

import java.util.Map;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiAbstractElement;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiXmlConfigArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * XML file configuration item.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiConfigElement extends GWikiAbstractElement
{

  private static final long serialVersionUID = -3877389856566990574L;

  @SuppressWarnings("rawtypes")
  private GWikiXmlConfigArtefakt< ? > config = new GWikiXmlConfigArtefakt();

  public GWikiConfigElement(GWikiElementInfo elementInfo)
  {
    super(elementInfo);
  }

  public void collectParts(Map<String, GWikiArtefakt< ? >> map)
  {
    map.put("", config);
    super.collectParts(map);

  }

  public GWikiArtefakt< ? > getMainPart()
  {
    return config;
  }

  public void serve(GWikiContext ctx)
  {
    AuthorizationFailedException.failRight(ctx, "INVALID_PAGE");
  }

  public GWikiXmlConfigArtefakt< ? > getConfig()
  {
    return config;
  }

  public void setConfig(GWikiXmlConfigArtefakt< ? > config)
  {
    this.config = config;
  }

}
