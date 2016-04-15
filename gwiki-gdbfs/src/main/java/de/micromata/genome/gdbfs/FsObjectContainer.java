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

import java.io.Serializable;

/**
 * Container which holds a file and its data.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FsObjectContainer implements Serializable
{

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = -6952916904083188777L;

  /**
   * The file.
   */
  private FsObject file;

  /**
   * The byte data.
   */
  private byte[] byteData;

  /**
   * Instantiates a new fs object container.
   */
  public FsObjectContainer()
  {

  }

  /**
   * Instantiates a new fs object container.
   *
   * @param file the file
   */
  public FsObjectContainer(FsObject file)
  {
    this.file = file;
  }

  public FsObject getFile()
  {
    return file;
  }

  public void setFile(FsObject file)
  {
    this.file = file;
  }

  public byte[] getByteData()
  {
    return byteData;
  }

  public void setByteData(byte[] byteData)
  {
    this.byteData = byteData;
  }

}
