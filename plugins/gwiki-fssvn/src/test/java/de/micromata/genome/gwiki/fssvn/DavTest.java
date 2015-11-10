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
package de.micromata.genome.gwiki.fssvn;

import java.util.List;

import org.junit.Test;

import de.micromata.genome.gdbfs.FsObject;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class DavTest
{
  public static final String url = "https://svn.micromata.de/svn/testsvn/trunk";

  public static final String user = "";

  public static final String pass = "";

  public static final DavFileSystem createFs()
  {
    return new DavFileSystem(url, user, pass);
  }

  @Test
  public void dummy()
  {

  }

  /**
   * Deactivated
   */
  // @Test
  public void testList()
  {
    DavFileSystem df = createFs();
    System.out.println(" fs modtime: " + df.getModificationCounter());

    // String iname = "de/micromata/genome/gwiki/plugin/GWikiPlugin.java";
    // System.out.println(" Content: of " + iname + "\n" + df.readTextFile(iname));

    List<FsObject> ret = df.listFilesByPattern("", "*.java", 'F', true);
    for (FsObject f : ret) {
      System.out.println(f.getName());
      System.out.println(" mod: "
          + f.getModifiedAtString()
          + " by "
          + f.getModifiedBy()
          + "; created: "
          + f.getCreatedAtString()
          + " by "
          + f.getCreatedBy());
      System.out.println(" " + f.getAttributes());

    }
  }
}
