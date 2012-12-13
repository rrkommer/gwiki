////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////


// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

////////////////////////////////////////////////////////////////////////////


// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

////////////////////////////////////////////////////////////////////////////


package de.micromata.genome.gdbfs;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.db.DbDialectEnum;
import de.micromata.genome.gdbfs.db.DbFileSystemImpl;
import de.micromata.genome.gdbfs.db.DbTarget;

public class FileSystemTestSuite extends TestSuite
{
  List<Method> methods = new ArrayList<Method>();

  private static boolean isTestMethod(Method m)
  {
    String name = m.getName();
    Class< ? >[] parameters = m.getParameterTypes();
    Class< ? > returnType = m.getReturnType();
    return parameters.length == 0 && name.startsWith("test") && returnType.equals(Void.TYPE);
  }

  public static Test suite()
  {

    String rootDir = "./tmp/unittests/gdfstests";
    FileSystemTestSuite suite = new FileSystemTestSuite();
    createSuite(suite, "filesystem", new StdFileSystem(rootDir));
    createSuite(suite, "ramfilesystem", new RamFileSystem("unittest"));
    Properties settings = new Properties();
    try {
      InputStream is = FileSystemTestSuite.class.getClassLoader().getResourceAsStream(
          FileSystemTestSuite.class.getName().replace('.', '/').replace(FileSystemTestSuite.class.getSimpleName(), "gdbstest.properties"));
      settings.load(is);
    } catch (Exception ex) {
      throw new RuntimeException("Cannot load gdbstest.properties: " + ex.getMessage(), ex);
    }
    for (DbDialectEnum dbd : DbDialectEnum.values()) {
      String connUrl = settings.getProperty("gdbfs." + dbd.name() + ".connecturl");
      if (StringUtils.isEmpty(connUrl) == true) {
        continue;
      }
      String dialect = settings.getProperty("gdbfs." + dbd.name() + ".dialect");
      if (StringUtils.isEmpty(dialect) == true) {
        continue;
      }
      BasicDataSource ds = new BasicDataSource();
      ds.setUrl(connUrl);
      ds.setDriverClassName(settings.getProperty("gdbfs." + dbd.name() + ".driverClassName"));
      String user = settings.getProperty("gdbfs." + dbd.name() + ".username");
      if (StringUtils.isNotBlank(user) == true) {
        ds.setUsername(user);
      }
      String password = settings.getProperty("gdbfs." + dbd.name() + ".password");
      if (StringUtils.isNotBlank(password) == true) {
        ds.setPassword(password);
      }
      DbFileSystemImpl ofs = new DbFileSystemImpl();

      ofs.setDbTarget(new DbTarget(DbDialectEnum.valueOf(dialect), ds));
      ofs.setFileSystemName("GDBFS_UNITTEST");
      try {
        ofs.exists("");
      } catch (Exception ex) {
        System.out.println("Database " + dialect + " not available");
        continue;
      }
      createSuite(suite, dialect, ofs);
    }

    return suite;

  }

  public static void createSuite(FileSystemTestSuite suite, String name, FileSystem fsys)
  {

    for (Method m : FileSystemCase.class.getDeclaredMethods()) {
      if (isTestMethod(m) == false) {
        continue;
      }
      FileSystemCase test = new FileSystemCase(name, fsys){};
      test.setName(m.getName());
      suite.addTest(test);
    }
  }

}
