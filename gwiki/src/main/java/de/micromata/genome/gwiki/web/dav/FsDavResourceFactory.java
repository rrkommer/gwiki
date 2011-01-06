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

package de.micromata.genome.gwiki.web.dav;

import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FsDirectoryObject;
import de.micromata.genome.gdbfs.FsFileObject;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.spi.storage.GWikiFileStorage;
import de.micromata.genome.gwiki.web.GWikiServlet;

/**
 * WebDav service implementation.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FsDavResourceFactory implements ResourceFactory
{
  private FileSystem storage;

  private String reqPrefix;

  private boolean wordHtmlEdit = true;

  private String internalUserName;

  private String internalPass;

  public FsDavResourceFactory(FileSystem storage, String reqPrefix)
  {
    this.storage = storage;
    this.reqPrefix = reqPrefix;
  }

  public FileSystem getStorage()
  {
    if (storage != null) {
      return storage;
    }

    GWikiStorage wkStorage = GWikiServlet.INSTANCE.getWikiWeb().getStorage();
    if (wkStorage instanceof GWikiFileStorage) {
      storage = ((GWikiFileStorage) wkStorage).getStorage();
    } else {
      throw new RuntimeException("GWiki not found or has no compatible storage");
    }
    return storage;
  }

  private String getInternalName(String path)
  {
    if (path.startsWith(reqPrefix) == true) {
      path = path.substring(reqPrefix.length());
    }
    if (path.endsWith("/") == true) {
      path = path.substring(0, path.length() - 1);
    }
    return path;
  }

  public Resource convertRes(FsDavResourceFactory resourceFactory, FsObject obj)
  {
    if (obj == null)
      return null;
    if (obj instanceof FsFileObject) {
      return new GFileResource(resourceFactory, (FsFileObject) obj);
    } else if (obj instanceof FsDirectoryObject) {
      return new GDirectoryResource(resourceFactory, (FsDirectoryObject) obj);
    }
    return null;
  }

  public Resource getResource(String host, String path)
  {
    String name = getInternalName(path);
    if (name.length() == 0) {
      name = "/";
    }
    FsObject obj = storage.getFileObject(name);
    if (obj == null && name.equals("/") == true) {
      storage.mkdir("");
      obj = storage.getFileObject(name);
    }

    return convertRes(this, obj);
  }

  public String getSupportedLevels()
  {
    return "1,2";
  }

  public String getReqPrefix()
  {
    return reqPrefix;
  }

  public void setReqPrefix(String reqPrefix)
  {
    this.reqPrefix = reqPrefix;
  }

  public void setStorage(FileSystem storage)
  {
    this.storage = storage;
  }

  public boolean isWordHtmlEdit()
  {
    return wordHtmlEdit;
  }

  public void setWordHtmlEdit(boolean wordHtmlEdit)
  {
    this.wordHtmlEdit = wordHtmlEdit;
  }

  public String getInternalUserName()
  {
    return internalUserName;
  }

  public void setInternalUserName(String internalUserName)
  {
    this.internalUserName = internalUserName;
  }

  public String getInternalPass()
  {
    return internalPass;
  }

  public void setInternalPass(String internalPass)
  {
    this.internalPass = internalPass;
  }

}
