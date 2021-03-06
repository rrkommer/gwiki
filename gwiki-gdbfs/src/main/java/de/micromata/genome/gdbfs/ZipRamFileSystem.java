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

import java.io.InputStream;

/**
 * Zip Ram File system.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class ZipRamFileSystem extends RamFileSystem
{

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 2733712903364025301L;

  /**
   * Instantiates a new zip ram file system.
   */
  public ZipRamFileSystem()
  {
    super();
  }

  /**
   * Instantiates a new zip ram file system.
   *
   * @param fsName the fs name
   */
  public ZipRamFileSystem(String fsName)
  {
    super(fsName);
  }

  /**
   * Instantiates a new zip ram file system.
   *
   * @param fsName the fs name
   * @param is the is
   */
  public ZipRamFileSystem(String fsName, InputStream is)
  {
    super(fsName);
    load(is);
  }

  /**
   * Load.
   *
   * @param is the is
   */
  public void load(InputStream is)
  {
    FileSystemUtils.copyFromZip(is, this.getFileObject(""));
  }
}
