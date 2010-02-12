////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
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

package de.micromata.genome.gdbfs.db;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FsFileObject;


public class FsDbFileObject extends FsFileObject
{

  private static final long serialVersionUID = 3444365568528456157L;

  private Long pk;

  private String dataEnc;

  private String dataCol0;

  public FsDbFileObject(FileSystem fileSystem, String name, String mimeType, long lastModified)
  {
    super(fileSystem, name, mimeType, lastModified);
  }

  public FsDbFileObject(FileSystem fileSystem, Long pk, String name, String mimeType, long lastModified, String dataEnc, String dataCol0)
  {
    super(fileSystem, name, mimeType, lastModified);
    this.pk = pk;
    this.dataEnc = dataEnc;
    this.dataCol0 = dataCol0;
  }

  public boolean isLongValue()
  {
    return dataEnc != null && dataEnc.length() > 1 && dataEnc.charAt(1) == FileSystem.DATAENCODING_LONGVALUE;
  }

  public String getDataEnc()
  {
    return dataEnc;
  }

  public void setDataEnc(String dataEnc)
  {
    this.dataEnc = dataEnc;
  }

  public String getDataCol0()
  {
    return dataCol0;
  }

  public void setDataCol0(String dataCol0)
  {
    this.dataCol0 = dataCol0;
  }

  public Long getPk()
  {
    return pk;
  }

  public void setPk(Long pk)
  {
    this.pk = pk;
  }

}
