package de.micromata.genome.launcher.config;

/**
 * A config Model, which can 'cast' to another ConfigModel.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public interface CastableConfigModel extends ConfigModel
{

  /**
   * Cast to.
   *
   * @param <T> the generic type
   * @param other the other
   * @return null if not castable.
   */
  <T extends ConfigModel> T castTo(Class<T> other);
}
