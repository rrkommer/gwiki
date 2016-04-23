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

package de.micromata.genome.gwiki.launcher.config;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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

  @ALocalSettingsPath(key = "gwiki.useContextXml")
  private String useContextXml;
  @ALocalSettingsPath(key = "gwiki.contextfile")
  private String contextXml;

  @ALocalSettingsPath(key = "gwiki.storage.type", defaultValue = FILESYSTEM)
  private String storageType;

  @ALocalSettingsPath(key = "gwiki.storage.filesystem.path", defaultValue = "./gwiki")
  private String storageFilePath;

  private JdbcLocalSettingsConfigModel jdbcConfigModel = new JdbcLocalSettingsConfigModel("gwiki",
      "jdbc for gwiki", new JndiLocalSettingsConfigModel("gwiki", JndiLocalSettingsConfigModel.DataType.DataSource,
          "java:comp/env/genome/jdbc/dsWeb"));

  @Override
  public void validate(ValContext ctx)
  {
    if (isTrue(useContextXml) == true) {
      if (StringUtils.isBlank(contextXml) == true) {
        ctx.directError("contextXml", "Please select a GWikiContext.xml file");
      } else if (new File(contextXml).exists() == false) {
        ctx.directError("contextXml", "Please select an existant GWikiContext.xml file");
      }
    } else {
      if (StringUtils.equals(storageType, FILESYSTEM) == false
          && StringUtils.equals(storageType, FILESYSTEM) == false) {
        ctx.directError("storageType", "Please select valid Storagetype");
        return;
      }
      if (StringUtils.equals(storageType, FILESYSTEM) == true) {
        if (StringUtils.isBlank(storageFilePath) == true) {
          ctx.directError("storageFilePath", "Please select directory where to store wiki files");
          return;
        }
        File dirFile = new File(storageFilePath);
        if (dirFile.exists() == true) {
          if (dirFile.isDirectory() == true) {
            return;
          } else {
            ctx.directError("storageFilePath", "Please select a directory where to store wiki files");
          }
        } else {
          boolean suc = dirFile.mkdirs();
          if (suc == false) {
            ctx.directError("storageFilePath", "Cannot create storageFilePath");
          }
        }
      } else {
        jdbcConfigModel.validate(ctx);
      }

    }
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

  public String getUseContextXml()
  {
    return useContextXml;
  }

  public void setUseContextXml(String useContextXml)
  {
    this.useContextXml = useContextXml;
  }

  public String getContextXml()
  {
    return contextXml;
  }

  public void setContextXml(String contextXml)
  {
    this.contextXml = contextXml;
  }

}
