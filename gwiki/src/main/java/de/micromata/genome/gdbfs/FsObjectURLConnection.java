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
package de.micromata.genome.gdbfs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * URLConnection for FileSystem.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FsObjectURLConnection extends URLConnection
{

  private FsObject file;

  /**
   * @param url
   */
  public FsObjectURLConnection(URL url)
  {
    super(url);
    throw new RuntimeException("Constructor FsObjectURLConnection(URL url) not supported");
  }

  public FsObjectURLConnection(URL url, FsObject file)
  {
    super(url);
    this.file = file;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.net.URLConnection#connect()
   */
  @Override
  public void connect() throws IOException
  {

  }

  /**
   * Needed for compiling jsp
   */
  @Override
  public long getLastModified()
  {
    return file.getLastModified();
  }

  @Override
  public InputStream getInputStream() throws IOException
  {
    byte[] data = file.getFileSystem().readBinaryFile(file.getName());
    return new ByteArrayInputStream(data);
  }
}
