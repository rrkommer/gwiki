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
import java.util.List;

import junit.framework.TestCase;

public class ReadWriteCombinedFileSystemTest extends TestCase
{
  public FileSystem loadZipFs()
  {
    InputStream is = ReadWriteCombinedFileSystemTest.class.getResourceAsStream("/de/micromata/genome/gdbfs/ZipFs.zip");
    assertNotNull(is);
    RamFileSystem fs = new RamFileSystem("ramfs");
    FileSystemUtils.copyFromZip(is, fs.getFileObject(""));
    fs.setReadOnly(true);
    return fs;
  }

  public CombinedFileSystem getCombined()
  {
    String rootDir = "./tmp/unittests/gdfstests";
    FileSystem primary = new StdFileSystem(rootDir);
    primary.erase();
    return new ReadWriteCombinedFileSystem(primary, loadZipFs());
  }

  public void testZip()
  {
    FileSystem fs = loadZipFs();
    String dfn = "gdfstests/datafile/textfile.txt";
    assertTrue(fs.exists(dfn));
    String text = fs.readTextFile(dfn);
    assertEquals("BlaBla", text);
  }

  public void testRead()
  {
    CombinedFileSystem fs = getCombined();
    String dfn = "gdfstests/datafile/textfile.txt";
    assertTrue(fs.exists(dfn));
    assertFalse(fs.delete(dfn));
    fs.mkdirs("gdfstests/datafile");
    fs.writeTextFile(dfn, "Blub", true);
    String readed = fs.readTextFile(dfn);
    assertEquals("Blub", readed);
    fs.delete(dfn);
    assertTrue(fs.exists(dfn));
    assertEquals("BlaBla", fs.readTextFile(dfn));
  }

  public void testListFiles()
  {
    CombinedFileSystem fs = getCombined();
    fs.mkdirs("gdfstests/datafile");
    fs.writeTextFile("gdfstests/datafile/anotherfile.txt", "jo", false);
    List<FsObject> fsl = fs.listFilesByPattern("gdfstests/datafile", "*.txt", null, true);
    assertEquals(2, fsl.size());
  }
}
