/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   27.11.2009
// Copyright Micromata 27.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.controls;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FileSystemUtils;
import de.micromata.genome.gdbfs.FsDirectoryObject;
import de.micromata.genome.gdbfs.RamFileSystem;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.spi.storage.GWikiFileStorage;
import de.micromata.genome.gwiki.tools.confluence.imp.ConfluenceImporter;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * A mounted web is for temporary storage of imported pages.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiMountedWeb implements Serializable
{
  // public static String SESSION_KEY = "de.micromata.genome.gwiki.controls.GWikiMountedWeb";

  private static final long serialVersionUID = 7293729219165715440L;

  private String tmpDirName;

  // private FileItem dataFile;

  // private FileSystem fileSystem;

  // private GWikiStorage wikiStorage;

  public GWikiMountedWeb()
  {

  }

  public GWikiMountedWeb(String tmpDirName)
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

  protected void mountZipFileSystem(InputStream is, GWikiContext wikiContext, boolean confluenceArchive, String pathPrefix)
      throws IOException
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
    if (confluenceArchive == true) {
      FileSystem inFs = new RamFileSystem("confimp");
      FileSystemUtils.copyFromZip(is, inFs.getFileObject(""));
      ConfluenceImporter importer = new ConfluenceImporter(inFs);
      importer.parseDom();
      String targetDir = tmpDirName + "/";
      if (StringUtils.isNotEmpty(pathPrefix) == true) {
        targetDir = targetDir + pathPrefix + "/";
      }
      importer.doImport(wikiContext, targetDir, pathPrefix);
    } else {
      FileSystemUtils.copyFromZip(is, target);
    }
  }

  public void initialize(GWikiContext wikiContext, FileItem dataFile, boolean confluenceArchive, String pathPrefix)
  {
    if (dataFile == null) {
      wikiContext.addSimpleValidationError("Kein Zip vorhanden");
      return;
    }
    try {

      mountZipFileSystem(dataFile.getInputStream(), wikiContext, confluenceArchive, pathPrefix);
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
