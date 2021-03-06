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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * RAM file system initially loaded from a zip from standard file system.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FileZipRamFileSystem extends ZipRamFileSystem
{

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = -7317267244076883488L;

  /**
   * Instantiates a new file zip ram file system.
   *
   * @param zipFile the zip file
   * @param fsName the fs name
   */
  public FileZipRamFileSystem(String zipFile, String fsName)
  {
    this(zipFile, fsName, false);
  }

  /**
   * Instantiates a new file zip ram file system.
   *
   * @param zipFile the zip file
   * @param fsName the fs name
   * @param readOnly the read only
   */
  public FileZipRamFileSystem(String zipFile, String fsName, boolean readOnly)
  {
    super(fsName);
    try {
      InputStream is = new FileInputStream(zipFile);
      load(is);
      setReadOnly(readOnly);
    } catch (IOException ex) {
      throw new FsException("Cannot open ZipFile: " + zipFile + "; " + ex.getMessage(), ex);
    }
  }
}
