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

import java.util.List;

/**
 * Represents a directory in the FileSystem.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FsDirectoryObject extends FsObject
{

  private static final long serialVersionUID = 7503728953607974196L;

  public FsDirectoryObject(FileSystem fileSystem, String name, long lastModified)
  {
    super(fileSystem, name, FileSystem.TYPE_DIR, null, lastModified);
  }

  public FsDirectoryObject(FsDirectoryObject other)
  {
    super(other);
  }

  @Override
  public Object clone()
  {
    return new FsDirectoryObject(this);
  }

  public FsDirectoryObject mkdir(String dir)
  {
    String locName = FileNameUtils.join(getName(), dir);
    if (fileSystem.existsForWrite(locName) == true) {
      FsObject fso = fileSystem.getFileObject(locName);
      if (fso instanceof FsDirectoryObject) {
        return (FsDirectoryObject) fso;
      }
      return null;
    }
    if (fileSystem.mkdir(locName) == false)
      return null;
    return (FsDirectoryObject) fileSystem.getFileObject(locName);
  }

  public FsObject getChild(String childName)
  {
    return fileSystem.getFileObject(FileNameUtils.join(getName(), childName));
  }

  public List<FsObject> getChilds(Character type)
  {
    return fileSystem.listFiles(getName(), null, type, false);
  }

}
