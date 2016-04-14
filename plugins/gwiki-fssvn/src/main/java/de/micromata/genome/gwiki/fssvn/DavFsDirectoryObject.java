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

package de.micromata.genome.gwiki.fssvn;

import java.util.Map;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FsDirectoryObject;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class DavFsDirectoryObject extends FsDirectoryObject implements FsAttributedObject
{
  private static final long serialVersionUID = 2199417680473737380L;

  private Map<String, String> attributeMap;

  public DavFsDirectoryObject(FileSystem fileSystem, String name, long lastModified, Map<String, String> attributeMap)
  {
    super(fileSystem, name, lastModified);
    this.attributeMap = attributeMap;
  }

  public DavFsDirectoryObject(DavFsDirectoryObject other)
  {
    super(other);
    this.attributeMap = other.attributeMap;
  }

  public Object clone()
  {
    return new DavFsDirectoryObject(this);
  }

  public String getAttribute(String key)
  {
    return attributeMap.get(key);
  }

  public Map<String, String> getAttributMap()
  {
    return attributeMap;
  }

  public void setAttributMap(Map<String, String> attributeMap)
  {
    this.attributeMap = attributeMap;
  }

}
