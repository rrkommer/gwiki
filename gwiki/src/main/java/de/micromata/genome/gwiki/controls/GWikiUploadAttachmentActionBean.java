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

package de.micromata.genome.gwiki.controls;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FileSystemUtils;
import de.micromata.genome.gdbfs.FsDirectoryObject;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiBinaryAttachmentArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * ActionBean for uploading attachments.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiUploadAttachmentActionBean extends ActionBeanBase
{

  private String userName;

  private String passWord;

  private String pageId;

  private String parentPageId;

  private String fileName;

  private String encData;

  private String token;

  private boolean storeTmpFile;

  public Object onInit()
  {
    return noForward();
  }

  public Object onLogin()
  {
    boolean loggedIn = wikiContext.getWikiWeb().getAuthorization().login(wikiContext, userName, passWord);
    Map<String, String> resp = new HashMap<String, String>();
    resp.put("rc", loggedIn ? "0" : "1");
    sendResponse(resp);
    return noForward();
  }

  public Object onIsLoggedIn()
  {
    boolean notLoggedIn = wikiContext.getWikiWeb().getAuthorization().needAuthorization(wikiContext);
    Map<String, String> resp = new HashMap<String, String>();
    resp.put("rc", notLoggedIn ? "1" : "0");
    sendResponse(resp);
    return noForward();
  }

  protected String encodeAsUrl(Map<String, String> map)
  {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> me : map.entrySet()) {
      if (sb.length() > 0) {
        sb.append("&");
      }
      try {
        sb.append(URLEncoder.encode(me.getKey(), "UTF-8"));
        sb.append("=");
        sb.append(URLEncoder.encode(me.getValue() != null ? me.getValue() : "", "UTF-8"));
      } catch (UnsupportedEncodingException ex) {
        throw new RuntimeException(ex);
      }
    }
    return sb.toString();
  }

  protected Map<String, String> toMap(String... keyValues)
  {
    Map<String, String> ret = new HashMap<String, String>();
    for (int i = 0; i < keyValues.length; ++i) {
      if (i + 1 >= keyValues.length) {
        return ret;
      }
      ret.put(keyValues[i], keyValues[i + 1]);
      ++i;
    }
    return ret;
  }

  protected void sendResponse(Map<String, String> resp)
  {
    String sr = encodeAsUrl(resp);
    try {
      wikiContext.getResponseOutputStream().write(sr.getBytes("UTF-8"));
      wikiContext.getResponseOutputStream().flush();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  protected void sendResponse(int rc, String message)
  {
    sendResponse(toMap("rc", Integer.toString(rc), "rm", message));
  }

  public Object onUploadImage()
  {
    try {
      if (wikiContext.getWikiWeb().getAuthorization().needAuthorization(wikiContext) == true) {
        if (StringUtils.isBlank(userName) == true || StringUtils.isBlank(passWord)) {
          sendResponse(2, "need login");
          return noForward();
        }
        boolean loggedIn = wikiContext.getWikiWeb().getAuthorization().login(wikiContext, userName, passWord);
        if (loggedIn == false) {
          sendResponse(1, "invalid user or password");
          return noForward();
        }
      }
      try {
        if (wikiContext.getWikiWeb().getAuthorization().initThread(wikiContext) == false) {
          sendResponse(2, "need login");
          return noForward();
        }
        if (StringUtils.isEmpty(pageId) == true) {
          pageId = fileName;
        }
        if (StringUtils.isEmpty(pageId) == true) {
          sendResponse(3, "need file name");
          return noForward();
        }
        if (StringUtils.isEmpty(encData) == true) {
          sendResponse(4, "data is empty");
          return noForward();
        }

        byte[] data = Base64.decodeBase64(encData.getBytes());
        if (StringUtils.isNotEmpty(parentPageId) == true) {
          String pp = GWikiContext.getParentDirPathFromPageId(parentPageId);
          pageId = pp + pageId;
        }
        if (storeTmpFile == true) {
          FileSystem fs = wikiContext.getWikiWeb().getStorage().getFileSystem();
          FsDirectoryObject tmpDir = fs.createTempDir("appletupload", 1000 * 60 * 30);
          String nf = FileSystemUtils.mergeDirNames(tmpDir.getName(), pageId);
          String pdirs = FileNameUtils.getParentDir(nf);
          fs.mkdirs(pdirs);
          fs.writeBinaryFile(nf, data, true);
          sendResponse(toMap("rc", "0", "tmpFileName", nf));
        } else {
          if (wikiContext.getWikiWeb().findElementInfo(pageId) != null) {
            sendResponse(5, "File already exists");
            return noForward();
          }
          String metaTemplateId = "admin/templates/FileWikiPageMetaTemplate";
          GWikiElement el = GWikiWebUtils.createNewElement(wikiContext, pageId, metaTemplateId, fileName);
          el.getElementInfo().getProps().setStringValue(GWikiPropKeys.PARENTPAGE, parentPageId);
          GWikiArtefakt< ? > art = el.getMainPart();
          GWikiBinaryAttachmentArtefakt att = (GWikiBinaryAttachmentArtefakt) art;
          att.setStorageData(data);
          if (data != null) {
            el.getElementInfo().getProps().setIntValue(GWikiPropKeys.SIZE, data.length);
          }
          wikiContext.getWikiWeb().saveElement(wikiContext, el, false);
          sendResponse(toMap("rc", "0", "tmpFileName", el.getElementInfo().getId()));

        }
      } finally {
        wikiContext.getWikiWeb().getAuthorization().clearThread(wikiContext);
      }
    } catch (Exception ex) {
      GWikiLog.warn("Failure to upload attachment: " + ex.getMessage(), ex);
      sendResponse(10, "General Gwiki error: " + ex.getMessage());
    }
    return noForward();
  }

  public String getParentPageId()
  {
    return parentPageId;
  }

  public void setParentPageId(String parentPageId)
  {
    this.parentPageId = parentPageId;
  }

  public String getFileName()
  {
    return fileName;
  }

  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }

  public String getEncData()
  {
    return encData;
  }

  public void setEncData(String encData)
  {
    this.encData = encData;
  }

  public String getToken()
  {
    return token;
  }

  public void setToken(String token)
  {
    this.token = token;
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public String getPassWord()
  {
    return passWord;
  }

  public void setPassWord(String passWord)
  {
    this.passWord = passWord;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public boolean isStoreTmpFile()
  {
    return storeTmpFile;
  }

  public void setStoreTmpFile(boolean storeTmpFile)
  {
    this.storeTmpFile = storeTmpFile;
  }

}
