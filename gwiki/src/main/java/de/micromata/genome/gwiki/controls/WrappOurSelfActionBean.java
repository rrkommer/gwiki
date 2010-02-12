/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   07.11.2009
// Copyright Micromata 07.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.controls;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FileSystemUtils;
import de.micromata.genome.gdbfs.StdFileSystem;
import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.spi.storage.GWikiFileStorage;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * Nur bei standalone moeglich. Verpackt sich selbst und laded sich als Datei herunter.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class WrappOurSelfActionBean extends ActionBeanBase
{
  public Object onInit()
  {
    return null;
  }

  protected boolean wrapp()
  {
    GWikiStorage ws = wikiContext.getWikiWeb().getStorage();
    if ((ws instanceof GWikiFileStorage) == false) {
      return false;
    }
    FileSystem fs = ((GWikiFileStorage) ws).getStorage();
    if ((fs instanceof StdFileSystem) == false) {
      return false;
    }
    String rootdir = fs.getFileSystemName();
    File f = new File(rootdir);
    System.out.println("RootDir is: " + f.getAbsolutePath());
    File nf = f.getAbsoluteFile().getParentFile();
    StdFileSystem wfs = new StdFileSystem(nf.getAbsolutePath());
    if (wfs.exists("gwikiweb") == false) {
      wikiContext.addSimpleValidationError("Cannot find gwikiweb in " + nf);
      return false;
    }
    if (wfs.exists("gwiki") == false) {
      wikiContext.addSimpleValidationError("Cannot find gwiki in " + nf);
      return false;
    }
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    Matcher<String> m = new BooleanListRulesFactory<String>().createMatcher("+*,-*/arch/*,-*.zip");
    FileSystemUtils.copyToZip(wfs.getFileObject(""), m, os);
    byte[] data = os.toByteArray();
    deliver(data, "gwikidestop.zip");
    return true;
  }

  protected boolean wrappWithBootstrap()
  {
    return false;
  }

  protected void deliver(byte[] data, String name)
  {

    HttpServletResponse resp = wikiContext.getResponse();
    resp.setContentType("application/binary");
    resp.setHeader("Content-Disposition", "atachment; filename=" + name);
    resp.setHeader("Pragma", "no-cache");

    resp.setContentLength(data.length);
    try {
      resp.getOutputStream().write(data);
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  public Object onDownload()
  {
    if (wrapp() == false) {
      wikiContext.addSimpleValidationError("Self Wrapping not supported");
      return null;
    }
    return noForward();
  }
}
