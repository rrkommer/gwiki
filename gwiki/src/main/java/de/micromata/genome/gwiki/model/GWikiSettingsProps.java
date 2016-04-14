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

package de.micromata.genome.gwiki.model;

import de.micromata.genome.gwiki.utils.EqualsInternalizator;
import de.micromata.genome.gwiki.utils.Internalizator;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSettingsProps extends GWikiProps
{

  private static final long serialVersionUID = 6048099531218172064L;

  static Internalizator<String> KEYSTORE = new EqualsInternalizator<String>();

  public GWikiSettingsProps()
  {
    super(KEYSTORE);
  }

  public GWikiSettingsProps(GWikiSettingsProps other)
  {
    super(other);
  }

  public GWikiSettingsProps(GWikiProps other)
  {
    super(other);
  }
}
