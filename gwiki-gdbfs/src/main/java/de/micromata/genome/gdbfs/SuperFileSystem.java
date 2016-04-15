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

package de.micromata.genome.gdbfs;

/**
 * A Super file system is a mount point with mount prefix.
 * 
 * Virtual FileSystem /a/b with mount point /a will be delageted to /b of parent file system.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SuperFileSystem extends MountPointFileSystem
{

  /**
   * Instantiates a new super file system.
   */
  public SuperFileSystem()
  {
  }

  /**
   * Instantiates a new super file system.
   *
   * @param parentFileSystem the parent file system
   * @param path the path
   */
  public SuperFileSystem(FileSystem parentFileSystem, String path)
  {
    super(parentFileSystem, path);
    // optimize if parent is MountPointFileSystem.
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.MointPointFileSystem#getFqName(java.lang.String)
   */
  @Override
  protected String getFqName(String name)
  {
    if (name.startsWith(path) == false) {
      throw new FsException("name is not part of this SuperFileSystem. name=" + name + "; path=" + path);
    }
    return name.substring(path.length());
  }

}
