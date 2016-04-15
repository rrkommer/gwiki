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

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * URLStreamHandler for FileSystem.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FsObjectURLStreamHandler extends URLStreamHandler
{

  /**
   * The file.
   */
  private FsObject file;

  /**
   * Instantiates a new fs object url stream handler.
   *
   * @param file the file
   */
  public FsObjectURLStreamHandler(FsObject file)
  {
    this.file = file;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.net.URLStreamHandler#openConnection(java.net.URL)
   */
  @Override
  protected URLConnection openConnection(URL u) throws IOException
  {
    return new FsObjectURLConnection(u, file);
  }

  public FsObject getFile()
  {
    return file;
  }

  public void setFile(FsObject file)
  {
    this.file = file;
  }

}
