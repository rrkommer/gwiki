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

import java.io.File;

import junit.framework.TestCase;

public class StandardFileSystemTest extends TestCase
{
  public void testSecurityAccessWindowsC()
  {
    char csep = File.separatorChar;
    if (csep != '\\') {
      return;
    }
    String rootDir = "./tmp/unittests/gdfstests";
    FileSystem fs = new StdFileSystem(rootDir);
    try {
      fs.mkdir("c:/");
      fs.writeTextFile("c:/GwikiFs.txt", "Should be written", true);
      fail("FsInvalidNameException should be thrown");
    } catch (FsInvalidNameException ex) {
      System.out.println("Expected ex: " + ex.getMessage());
    }
  }

  public void testSecurityAccessParent()
  {
    String rootDir = "./tmp/unittests/gdfstests";
    FileSystem fs = new StdFileSystem(rootDir);
    try {
      fs.mkdirs("../thisDirShouldNotBeCreated");
      fail("FsInvalidNameException should be thrown");
    } catch (FsInvalidNameException ex) {
      System.out.println("Expected ex: " + ex.getMessage());
    }
  }
}
