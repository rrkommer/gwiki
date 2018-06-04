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

import org.apache.commons.lang3.StringUtils;

/**
 * Representing a File (not a directory) in the file system.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FsFileObject extends FsObject
{

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = -252297990366094145L;

  /**
   * Instantiates a new fs file object.
   *
   * @param fileSystem the file system
   * @param name the name
   * @param mimeType the mime type
   * @param lastModified the last modified
   */
  public FsFileObject(FileSystem fileSystem, String name, String mimeType, long lastModified)
  {
    super(fileSystem, name, FileSystem.TYPE_FILE, mimeType, lastModified);
  }

  /**
   * Instantiates a new fs file object.
   *
   * @param other the other
   */
  public FsFileObject(FsFileObject other)
  {
    super(other);
  }

  @Override
  public Object clone()
  {
    return new FsFileObject(this);
  }

  public boolean isTextFile()
  {
    String mime = mimeType;
    if (StringUtils.isBlank(mime) == true) {
      mime = MimeUtils.getMimeTypeFromFile(name);
    }
    if (mime == null) {
      if (name.endsWith(".gwiki") == true || name.endsWith(".properties") == true) {
        mime = "text/text";
      } else {
        return false;
      }
    }
    if (mime.startsWith("text/") == true) {
      return true;
    }
    return false;
  }

  /**
   * Read data.
   *
   * @return the byte[]
   */
  public byte[] readData()
  {
    return fileSystem.readBinaryFile(name);
  }

  /**
   * Read string.
   *
   * @return the string
   */
  public String readString()
  {
    return fileSystem.readTextFile(name);
  }

  /**
   * Write new file.
   *
   * @param data the data
   */
  // TODO exception
  public void writeNewFile(byte[] data)
  {
    fileSystem.writeBinaryFile(name, data, false);
  }

  /**
   * Write file.
   *
   * @param data the data
   */
  public void writeFile(byte[] data)
  {
    fileSystem.writeBinaryFile(name, data, true);
  }

  /**
   * Write new file.
   *
   * @param data the data
   */
  public void writeNewFile(String data)
  {
    fileSystem.writeTextFile(name, data, false);
  }

  /**
   * Write file.
   *
   * @param data the data
   */
  public void writeFile(String data)
  {
    fileSystem.writeTextFile(name, data, true);
  }

  /**
   * Delete.
   */
  public void delete()
  {
    fileSystem.delete(name);
  }
}
