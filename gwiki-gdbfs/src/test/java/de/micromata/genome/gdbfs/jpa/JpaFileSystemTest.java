package de.micromata.genome.gdbfs.jpa;

import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;

import de.micromata.genome.util.runtime.LocalSettingsEnv;
import de.micromata.genome.util.runtime.Log4JInitializer;
import junit.framework.TestCase;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class JpaFileSystemTest extends TestCase
{
  byte[] fillData(int size)
  {
    byte[] ret = new byte[size];
    for (int i = 0; i < size; ++i) {
      ret[i] = (byte) i;
    }
    return ret;
  }

  @Test
  public void testFirst()
  {
    try {
      LocalSettingsEnv lse = LocalSettingsEnv.get();
      Log4JInitializer.initializeLog4J();
      Map<String, BasicDataSource> dse = lse.getDataSources();

      GwikiEmgrFactory emfac = GwikiEmgrFactory.get();
      Long pk = emfac.tx().go((emgr) -> {
        JpaFilesystemDO nd = new JpaFilesystemDO();
        nd.setFsName("testFirst");
        nd.setName("file");
        nd.setFileType('F');
        nd.setData(fillData(1024 * 1024 * 6));
        emgr.insertDetached(nd);
        return nd.getPk();
      });
      JpaFilesystemDO readed = emfac.notx().go((emgr) -> emgr.selectByPkDetached(JpaFilesystemDO.class, pk));
      byte[] data = readed.getData();

    } catch (RuntimeException ex) {
      ex.printStackTrace();
      throw ex;
    }
  }
}
