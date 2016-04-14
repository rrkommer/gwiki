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

package de.micromata.genome.launcher.config;

import de.micromata.genome.gwiki.jetty.ValidationContext;
import de.micromata.genome.util.collections.SortedProperties;
import de.micromata.genome.util.runtime.LocalSettings;

/**
 * Self validation config model.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
@Deprecated
public interface ConfigModel
{
  /**
   * Validate the model.
   * 
   * @param ctx
   */
  public void validate(ValidationContext ctx);

  /**
   * store the configuration into local settings.
   * 
   * @param props
   */
  public default void toProperties(SortedProperties props)
  {
    ConfigUtils.toProperties(this, props);
  }

  /**
   * load the configuration from local settings.
   * 
   * @param localSettings
   */
  public default void fromLocalSettings(LocalSettings localSettings)
  {
    ConfigUtils.initFromLocalSettings(this, localSettings);
  }
}
