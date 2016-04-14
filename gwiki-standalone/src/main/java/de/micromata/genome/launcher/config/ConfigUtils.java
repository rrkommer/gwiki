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

import java.lang.reflect.Field;
import java.util.List;

import de.micromata.genome.util.bean.FieldMatchers;
import de.micromata.genome.util.bean.PrivateBeanUtils;
import de.micromata.genome.util.collections.SortedProperties;
import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.genome.util.runtime.config.ALocalSettingsPath;

/**
 * Utilities to manage configuration mappings
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class ConfigUtils
{
  public static void initFromLocalSettings(Object bean, LocalSettings localSettings)
  {
    List<Field> fields = PrivateBeanUtils.findAllFields(bean.getClass(),
        FieldMatchers.hasAnnotation(ALocalSettingsPath.class));
    for (Field field : fields) {
      ALocalSettingsPath lsp = field.getAnnotation(ALocalSettingsPath.class);
      PrivateBeanUtils.writeField(bean, field, localSettings.get(lsp.key(), lsp.defaultValue()));
    }
  }

  public static void toProperties(Object bean, SortedProperties ret)
  {
    List<Field> fields = PrivateBeanUtils.findAllFields(bean.getClass(),
        FieldMatchers.hasAnnotation(ALocalSettingsPath.class));
    for (Field field : fields) {
      ALocalSettingsPath lsp = field.getAnnotation(ALocalSettingsPath.class);
      String val = (String) PrivateBeanUtils.readField(bean, field);
      ret.put(lsp.key(), val);
    }
  }
}
