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
package de.micromata.genome.gdbfs;

import java.util.List;

import junit.framework.TestCase;
import de.micromata.genome.util.matcher.EveryMatcher;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SubFileSystemTest extends TestCase
{
  public void testListAndRead()
  {
    FileSystem rootfs = new StdFileSystem("./src");
    SubFileSystem nr = new SubFileSystem(rootfs, "test/resources");

    List<FsObject> files = nr.listFiles("", new EveryMatcher<String>(), 'F', true);

    for (FsObject file : files) {
      String name = file.getName();
      assertFalse(name.startsWith("/test/resources"));
      assertTrue(file.exists());
      if (file.isDirectory() == false) {
        byte[] data = file.getFileSystem().readBinaryFile(file.getName());
        assertNotNull(data);
      }

    }
  }
}
