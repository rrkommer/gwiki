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

package de.micromata.genome.gwiki.page.impl.actionbean;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;

import de.micromata.genome.gdbfs.FileSystem;

/**
 * Adapter to a file item.
 * 
 * @author roger
 * 
 */
public class GWikiFileSystemFileItemFactory implements FileItemFactory
{
  /**
   * Underlying file system
   */
  private FileSystem fileSystem;

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.fileupload.FileItemFactory#createItem(java.lang.String, java.lang.String, boolean, java.lang.String)
   */
  @Override
  public FileItem createItem(String fieldName, String contentType, boolean isFormField, String fileName)
  {

    return new FileSystemFileItem(fileSystem, fieldName, contentType, isFormField, fileName);
  }

  public FileSystem getFileSystem()
  {
    return fileSystem;
  }

  public void setFileSystem(FileSystem fileSystem)
  {
    this.fileSystem = fileSystem;
  }

}
