package de.micromata.genome.gwiki.spi.storage;

import org.springframework.beans.factory.FactoryBean;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.StdFileSystem;
import de.micromata.genome.gdbfs.jpa.JpaFileSystemImpl;
import de.micromata.genome.gwiki.utils.StringUtils;
import de.micromata.genome.util.runtime.LocalSettings;

/**
 * Creates a file system from the gwiki.localsettings.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class LsFileSystemFactoryBean implements FactoryBean<FileSystem>
{
  public static final String LOCAL_FILE_SYSTEM = "Filesystem";
  public static final String JPA_FILE_SYSTEM = "Database";

  @Override
  public FileSystem getObject() throws Exception
  {
    LocalSettings ls = LocalSettings.get();
    String fsysType = ls.get("gwiki.storage.type", LOCAL_FILE_SYSTEM);
    if (StringUtils.equals(fsysType, LOCAL_FILE_SYSTEM) == true) {
      String path = ls.get("gwiki.storage.filesystem.path");
      StdFileSystem ret = new StdFileSystem(path);
      return ret;
    } else if (StringUtils.equals(fsysType, JPA_FILE_SYSTEM) == true) {
      JpaFileSystemImpl ret = new JpaFileSystemImpl();
      return ret;
    } else {
      throw new RuntimeException("Unkown gwiki.filesystem.type: " + fsysType + "; required '" + LOCAL_FILE_SYSTEM
          + "' or '" + JPA_FILE_SYSTEM + "'");
    }
  }

  @Override
  public Class<?> getObjectType()
  {
    return FileSystem.class;
  }

  @Override
  public boolean isSingleton()
  {
    return true;
  }

}
