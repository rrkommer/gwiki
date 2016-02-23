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
