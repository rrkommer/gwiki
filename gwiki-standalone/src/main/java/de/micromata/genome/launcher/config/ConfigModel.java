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
