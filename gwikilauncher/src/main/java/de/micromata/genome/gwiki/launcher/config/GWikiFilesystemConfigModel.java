package de.micromata.genome.gwiki.launcher.config;

import java.util.Arrays;
import java.util.List;

import de.micromata.genome.gwiki.spi.storage.LsFileSystemFactoryBean;
import de.micromata.genome.util.runtime.config.ALocalSettingsPath;
import de.micromata.genome.util.runtime.config.AbstractCompositLocalSettingsConfigModel;
import de.micromata.genome.util.runtime.config.JdbcLocalSettingsConfigModel;
import de.micromata.genome.util.runtime.config.JndiLocalSettingsConfigModel;
import de.micromata.genome.util.validation.ValContext;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiFilesystemConfigModel extends AbstractCompositLocalSettingsConfigModel
{
  public static final String FILESYSTEM = LsFileSystemFactoryBean.LOCAL_FILE_SYSTEM;
  public static final String DATABASE = LsFileSystemFactoryBean.JPA_FILE_SYSTEM;

  @ALocalSettingsPath(key = "gwiki.storage.type", defaultValue = FILESYSTEM)
  private String storageType;

  @ALocalSettingsPath(key = "gwiki.storage.filesystem.path", defaultValue = "./gwiki")
  private String storageFilePath;

  private JdbcLocalSettingsConfigModel jdbcConfigModel = new JdbcLocalSettingsConfigModel("gwiki",
      "jdbc for gwiki", new JndiLocalSettingsConfigModel("gwiki", JndiLocalSettingsConfigModel.DataType.DataSource,
          "java:/comp/env/genome/jdbc/dsWeb"));

  @Override
  public void validate(ValContext ctx)
  {
    // TODO RK
  }

  public JdbcLocalSettingsConfigModel getJdbcConfigModel()
  {
    return jdbcConfigModel;
  }

  public boolean isJdbcStorageType()
  {
    return "Database".equals(storageType);
  }

  public void setJdbcStorageType(boolean jdbc)
  {
    storageType = jdbc ? DATABASE : FILESYSTEM;
  }

  public List<String> getAvailableFileSystemTypes()
  {
    return Arrays.asList(FILESYSTEM, DATABASE);
  }

  public String getStorageType()
  {
    return storageType;
  }

  public void setStorageType(String storageType)
  {
    this.storageType = storageType;
  }

  public String getStorageFilePath()
  {
    return storageFilePath;
  }

  public void setStorageFilePath(String storageFilePath)
  {
    this.storageFilePath = storageFilePath;
  }

  public void setJdbcConfigModel(JdbcLocalSettingsConfigModel jdbcConfigModel)
  {
    this.jdbcConfigModel = jdbcConfigModel;
  }

}
