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
package de.micromata.genome.gwiki.plugin.confluenceimporter_1_0;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.fileupload.FileItem;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FileSystemUtils;
import de.micromata.genome.gdbfs.FsDirectoryObject;
import de.micromata.genome.gdbfs.RamFileSystem;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.plugin.confluenceimporter_1_0.confluence.ConfluenceImporter;
import de.micromata.genome.gwiki.spi.storage.GWikiFileStorage;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiMountedConfluenceWeb
{

  private String tmpDirName;

  // private FileItem dataFile;

  // private FileSystem fileSystem;

  // private GWikiStorage wikiStorage;

  public GWikiMountedConfluenceWeb()
  {

  }

  public GWikiMountedConfluenceWeb(String tmpDirName)
  {
    this.tmpDirName = tmpDirName;
  }

  protected FileSystem getFileSystem(GWikiContext wikiContext)
  {
    GWikiStorage stor = wikiContext.getWikiWeb().getStorage();
    if ((stor instanceof GWikiFileStorage) == false) {
      wikiContext.addSimpleValidationError("GWikiStorage doesn't support FileSystems");
      return null;
    }
    FileSystem fs = ((GWikiFileStorage) stor).getStorage();
    return fs;
  }

  protected void mountZipFileSystem(InputStream is, GWikiContext wikiContext, String pathPrefix) throws IOException
  {
    FileSystem fs = getFileSystem(wikiContext);
    if (fs == null) {
      return;
    }
    FsDirectoryObject target = fs.createTempDir("gwkimw", TimeInMillis.DAY);
    if (target == null) {
      wikiContext.addSimpleValidationError("Unable to create temp directory");
      return;
    }
    tmpDirName = target.getName();

    FileSystem inFs = new RamFileSystem("confimp");
    FileSystemUtils.copyFromZip(is, inFs.getFileObject(""));
    ConfluenceImporter importer = new ConfluenceImporter(inFs);
    importer.parseDom();
    String targetDir = tmpDirName + "/";
    // if (StringUtils.isNotEmpty(pathPrefix) == true) {
    // targetDir = FileNameUtils.join(targetDir, pathPrefix) + "/";
    // }
    importer.doImport(wikiContext, targetDir, pathPrefix);

  }

  public void initialize(GWikiContext wikiContext, FileItem dataFile, String pathPrefix)
  {
    if (dataFile == null) {
      wikiContext.addSimpleValidationError("Kein Zip vorhanden");
      return;
    }
    try {
      mountZipFileSystem(dataFile.getInputStream(), wikiContext, pathPrefix);
    } catch (Exception ex) {
      wikiContext.addSimpleValidationError("Failure while mounting Zip File System: " + ex.getMessage());
      return;
    }
  }

  public void deleteTmpDir(GWikiContext wikiContext, String tmpDir)
  {
    FileSystem fs = getFileSystem(wikiContext);
    if (fs == null) {
      return;
    }
    fs.deleteRecursive(tmpDir);
  }

  public List<GWikiElementInfo> getElemenInfos(GWikiContext wikiContext)
  {
    return wikiContext.getWikiWeb().getStorage().loadPageInfos(tmpDirName);
  }

  public String getTmpDirName()
  {
    return tmpDirName;
  }

  public void setTmpDirName(String tmpDirName)
  {
    this.tmpDirName = tmpDirName;
  }
}
