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

package de.micromata.genome.gwiki.page.impl;

import java.io.IOException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

/**
 * Editor Artefact for attachments.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiAttachmentEditorArtefakt extends GWikiEditorArtefaktBase<byte[]> implements GWikiEditorArtefakt<byte[]>
{

  private static final long serialVersionUID = 6020855175438784647L;

  private GWikiBinaryAttachmentArtefakt attachment;

  private FileItem dataFile;

  private String appletTmpFileName;

  public GWikiAttachmentEditorArtefakt(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partName,
      GWikiBinaryAttachmentArtefakt attachment)
  {
    super(elementToEdit, editBean, partName);
    this.attachment = attachment;
  }

  public void onSave(GWikiContext ctx)
  {
    if (StringUtils.isEmpty(appletTmpFileName) == true) {
      appletTmpFileName = ctx.getRequestParameter(partName + ".appletTmpFileName");
    }
    String name = partName + ".attachment";
    dataFile = ctx.getFileItem(name);
    if (StringUtils.isEmpty(appletTmpFileName) == true) {
      if (dataFile == null || StringUtils.isEmpty(dataFile.getName()) == true) {
        if (editBean.isNewPage() == true) {
          ctx.addSimpleValidationError(ctx.getTranslated("gwiki.edit.EditPage.attach.message.nofile"));
          return;
        }
      } else {
        String fname = dataFile.getName();
        if (editBean != null && StringUtils.isBlank(editBean.getTitle()) == true) {
          editBean.setTitle(fname);
        }
        storeUpload(ctx);
      }
    } else {
      storeUpload(ctx);
    }

  }

  private void storeUpload(GWikiContext ctx)
  {
    if (StringUtils.isEmpty(appletTmpFileName) == true) {
      if (dataFile == null) {
        return;
      }
      if (dataFile == null || dataFile.getSize() != -1) {
        // dif = dataFile.getInputStream();
        // if (dif == null && attachment.getStorageData() == null) {
        // ctx.addSimpleValidationError("no data to upload/empty file");
        // }
        // return;
      }
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      byte[] data;
      try {
        IOUtils.copy(dataFile.getInputStream(), bout);
        data = bout.toByteArray();
        attachment.setStorageData(data);
      } catch (IOException ex) {
        ctx.addValidationError("gwiki.edit.EditPage.attach.message.uploadfailed", ex.getMessage());
      }
    } else {
      FileSystem fs = ctx.getWikiWeb().getStorage().getFileSystem();
      byte[] data = fs.readBinaryFile(appletTmpFileName);
      if (data == null || data.length == 0) {
        if (attachment.getStorageData() == null) {
          ctx.addValidationError("gwiki.edit.EditPage.attach.message.nouploaddata");
        }
        return;
      }
      attachment.setStorageData(data);
    }
    if (attachment.getStorageData() != null) {
      elementToEdit.getElementInfo().getProps().setIntValue(GWikiPropKeys.SIZE, attachment.getStorageData().length);
    }
  }

  private String translate(GWikiContext ctx, String key)
  {
    return ctx.getWikiWeb().getI18nProvider().translate(ctx, key);
  }

  public boolean renderWithParts(GWikiContext ctx)
  {
    String value = "";
    boolean attmentExists = false;
    storeUpload(ctx);
    if (attachment.getStorageData() != null) {
      attmentExists = true;
    }
    // funktioniert leider nicht
    if (dataFile != null && dataFile.getSize() != -1) {
      value = dataFile.getName();
    }
    String parentPageId = editBean.getParentPageId();
    StringBuilder html = new StringBuilder();
    html.append("<input id=\"appletTmpFileName\" type=\"hidden\" name=\"" + partName + ".appletTmpFileName\">");
    html.append("<script type=\"text/javascript\">") //
        .append("function swithFileUploadToStd() {\n") //
        .append("jQuery(\"#gwikiattappfrm\").hide();") //
        .append("jQuery(\"#gwikiattappexists\").hide();") //
        .append("jQuery(\"#gwikiattstd\").show();") //
        .append("}\n") //
        .append("function swithFileUploadToStd() {\n") //
        .append("jQuery(\"#gwikiattappfrm\").hide();") //
        .append("jQuery(\"#gwikiattappexists\").hide();") //
        .append("jQuery(\"#gwikiattstd\").show();") //
        .append("}\n") //
        .append("function switchFileExists() {\n") //
        .append("jQuery(\"#gwikiattappfrm\").hide();") //
        .append("jQuery(\"#gwikiattstd\").hide();") //
        .append("jQuery(\"#gwikiattappexists\").show();") //
        .append("}\n") //

        .append("function switchFileUploadToApplet(){\n") //
        .append("jQuery(\"#gwikiattstd\").hide(); \n")//
        .append("jQuery(\"#gwikiattappexists\").hide();\n")//
        .append("jQuery(\"#gwikiattappfrm\").show();\n")//
        .append(
            "jQuery(\"#gwikiattpapplet\").load(\""
                + ctx.localUrl("/edit/UploadAppletWindow")
                + "?storeTmpFile=true"
                + "&parentPageId="
                + parentPageId
                + " #uploadappletbody\")\n") //
        .append("}\n") //
        .append("function refreshFromApplet(fileName, tmpFileName) {\n")//
        .append("alert('")
        .append(ctx.getTranslated("gwiki.edit.EditPage.attach.message.uploaded"))
        .append("');\n")//
        .append("jQuery('#appletTmpFileName').val(tmpFileName);") //
        .append("jQuery('#editPageTitle').val(fileName);") //
        // .append("jQuery('gwikiattfilsize').html('
        .append("switchFileExists();") //
        .append("}\n")//
        .append("</script>") //
    ;
    html.append("<div id=\"gwikiattappexists\" >\n")//
        .append("<span id=\"gwikiattfilsize\">");
    if (attmentExists == true) {
      html.append(attachment.getStorageData().length).append(" bytes") //
      ;
    }
    html.append("</span><br/>") //
    ;

    html.append(
        Html.a(Xml.attrs("href", "javascript:swithFileUploadToStd()")).nest(
            Xml.text(translate(ctx, "Ugwiki.edit.EditPage.attach.message.nouploaddata"))))//
        .append("<br/>")//
        .append(Html.a(Xml.attrs("href", "javascript:switchFileUploadToApplet()"))//
            .nest(Xml.text(translate(ctx, "gwiki.edit.EditPage.attach.label.uploadviaapplet")))) //
        .append("</div>") //
    ;
    html.append("<div id=\"gwikiattappfrm\" >\n")//
        .append(
            Html.a(Xml.attrs("href", "javascript:swithFileUploadToStd()")).nest(
                Xml.text(translate(ctx, "gwiki.edit.EditPage.attach.label.switchtostandard")))) //
        .append("<div id=\"gwikiattpapplet\">&nbsp;</div>") //
        .append("</div>\n") //
        .append("<div id=\"gwikiattstd\">\n") //
        .append(Html.input(Xml.attrs("type", "file", "value", value, "name", partName + ".attachment"))) //
        .append("<br/>\n") //
        .append(
            Html.a(Xml.attrs("href", "javascript:switchFileUploadToApplet()")).nest(
                Xml.text(translate(ctx, "gwiki.edit.EditPage.attach.label.switchtostandard")))) //
    ;
    if (attmentExists == true) {
      html.append("<script type=\"text/javascript\">switchFileExists();</script>\n");
    } else {
      html.append("<script type=\"text/javascript\">swithFileUploadToStd();</script>\n");
    }
    String pageId = editBean.getPageId();

    // String url = ctx.localUrl("/edit/UploadAppletWindow?");
    StringBuilder sb = new StringBuilder();
    if (StringUtils.isNotEmpty(pageId) == true) {
      if (sb.length() > 0) {
        sb.append("&");
      }
      sb.append("pageId=").append(pageId);
    }
    if (StringUtils.isNotEmpty(parentPageId) == true) {
      if (sb.length() > 0) {
        sb.append("&");
      }
      sb.append("parentPageId=").append(parentPageId);
    }

    // html.append("<br/>" + Html.a(Xml.attrs("target", "upap", "href", url + sb.toString())).nest(Xml.text("Upload applet")));

    html.append("</div>\n");

    ctx.append(html);

    return true;
  }

  @Override
  public String getTabTitle()
  {
    String title = super.getTabTitle();
    if (title.length() > 1) {
      return title;
    }
    return "Attachment";
  }

  public FileItem getDataFile()
  {
    return dataFile;
  }

  public void setDataFile(FileItem dataFile)
  {
    this.dataFile = dataFile;
  }

  public String getAppletTmpFileName()
  {
    return appletTmpFileName;
  }

  public void setAppletTmpFileName(String appletTmpFileName)
  {
    this.appletTmpFileName = appletTmpFileName;
  }
}
