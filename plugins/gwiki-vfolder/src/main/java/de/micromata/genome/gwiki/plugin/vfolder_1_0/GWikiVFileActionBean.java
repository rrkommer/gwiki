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
package de.micromata.genome.gwiki.plugin.vfolder_1_0;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FsFileObject;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.spi.storage.GWikiFileStorage;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiVFileActionBean extends GWikiVDirOrFileActionBeanBase
{
  private String rawText;

  public void init()
  {
    super.init();
  }

  public String readFileContent(GWikiContext wikiContext, String indexFile)
  {
    GWikiFileStorage gstore = (GWikiFileStorage) wikiContext.getWikiWeb().getStorage();
    FsObject obj = gstore.getStorage().getFileObject(indexFile);
    if (obj == null)
      return null;
    if ((obj instanceof FsFileObject) == false)
      return null;
    FsFileObject file = (FsFileObject) obj;
    String content = file.readString();
    return content;
  }

  public Object onInit()
  {
    if (StringUtils.equals(wikiContext.getRequestParameter("dl"), "true") == true) {
      return onDownload();
    }
    init();

    String indexFile = pageId + "TextExtract.txt";
    rawText = readFileContent(wikiContext, indexFile);
    return null;
  }

  public Object onDownload()
  {
    init();
    GWikiElement vfe = wikiContext.getWikiWeb().getElement(
        wikiContext.getCurrentElement().getElementInfo().getProps().getStringValue(GWikiVFolderUtils.FVOLDER));
    try {
      GWikiVFolderUtils.writeContent(vfe, pageId, wikiContext.getResponse());
    } catch (IOException ex) {
      GWikiLog.note("Error writing attachment: " + pageId);
    }
    return noForward();
  }

  public String getRawText()
  {
    return rawText;
  }

  public void setRawText(String rawText)
  {
    this.rawText = rawText;
  }
}
