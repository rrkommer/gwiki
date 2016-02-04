////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gdbfs.jpa.JpaFileSystemImpl;
import de.micromata.genome.util.runtime.LocalSettingsEnv;
import de.micromata.genome.util.runtime.Log4JInitializer;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class FileSystemTestSuite extends TestSuite
{
  List<Method> methods = new ArrayList<Method>();

  private static boolean isTestMethod(Method m)
  {
    String name = m.getName();
    Class<?>[] parameters = m.getParameterTypes();
    Class<?> returnType = m.getReturnType();
    return parameters.length == 0 && name.startsWith("test") && returnType.equals(Void.TYPE);
  }

  public static Test suite()
  {
    LocalSettingsEnv lse = LocalSettingsEnv.get();
    Log4JInitializer.initializeLog4J();
    String rootDir = "target/tmp/unittests/gdfstests";
    FileSystemTestSuite suite = new FileSystemTestSuite();
    createSuite(suite, "filesystem", new StdFileSystem(rootDir));
    createSuite(suite, "ramfilesystem", new RamFileSystem("unittest"));
    createSuite(suite, "jpafilesystem", new JpaFileSystemImpl("unittest"));

    return suite;

  }

  public static void createSuite(FileSystemTestSuite suite, String name, FileSystem fsys)
  {

    for (Method m : FileSystemCase.class.getDeclaredMethods()) {
      if (isTestMethod(m) == false) {
        continue;
      }
      FileSystemCase test = new FileSystemCase(name, fsys)
      {
        @Override
        public void run(TestResult result)
        {
          try {
            super.run(result);
          } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw ex;
          }
        }

      };
      test.setName(m.getName());
      suite.addTest(test);
    }
  }

}
