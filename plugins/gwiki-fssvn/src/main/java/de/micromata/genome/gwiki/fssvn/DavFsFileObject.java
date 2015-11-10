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

import java.util.Map;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FsFileObject;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class DavFsFileObject extends FsFileObject implements FsAttributedObject
{

  private static final long serialVersionUID = 1227088678455212127L;

  private Map<String, String> attributeMap;

  public DavFsFileObject(FileSystem fileSystem, String name, String mimeType, long lastModified, Map<String, String> attributeMap)
  {
    super(fileSystem, name, mimeType, lastModified);
    this.attributeMap = attributeMap;
  }

  public DavFsFileObject(DavFsFileObject other)
  {
    super(other);
    this.attributeMap = other.attributeMap;
  }

  public Object clone()
  {
    return new DavFsFileObject(this);
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
