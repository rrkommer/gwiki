/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   27.10.2009
// Copyright Micromata 27.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import java.io.IOException;
import java.io.InputStream;

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
          ctx.addSimpleValidationError("Keine Datei angegeben");
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
        //dif = dataFile.getInputStream();
//        if (dif == null && attachment.getStorageData() == null) {
//          ctx.addSimpleValidationError("no data to upload/empty file");
//        }
//        return;
      }
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      byte[] data;
      try {
        IOUtils.copy(dataFile.getInputStream(), bout);
        data = bout.toByteArray();
        attachment.setStorageData(data);
      } catch (IOException ex) {
        ctx.addSimpleValidationError("Failed to upload: " + ex.getMessage());
      }
    } else {
      FileSystem fs = ctx.getWikiWeb().getStorage().getFileSystem();
      byte[] data = fs.readBinaryFile(appletTmpFileName);
      if (data == null || data.length == 0) {
        if (attachment.getStorageData() == null) {
          ctx.addSimpleValidationError("no data to upload/empty file");
        }
        return;
      }
      attachment.setStorageData(data);
    }
    if (attachment.getStorageData() != null) {
      elementToEdit.getElementInfo().getProps().setIntValue(GWikiPropKeys.SIZE, attachment.getStorageData().length);
    }
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
    html.append("<script>") //
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
        .append("alert('Datei hochgeladen');\n")//
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
    html.append(Html.a(Xml.attrs("href", "javascript:swithFileUploadToStd()")).nest(Xml.text("Upload new file via standard file upload")))//
        .append("<br/>")//
        .append(Html.a(Xml.attrs("href", "javascript:switchFileUploadToApplet()")).nest(Xml.text("Upload new File via applet upload"))) //
        .append("</div>") //
    ;
    html.append("<div id=\"gwikiattappfrm\" >\n")//
        .append(Html.a(Xml.attrs("href", "javascript:swithFileUploadToStd()")).nest(Xml.text("Switch to standard file upload"))) //
        .append("<div id=\"gwikiattpapplet\">&nbsp;</div>") //
        .append("</div>\n") //
        .append("<div id=\"gwikiattstd\">\n") //
        .append(Html.input(Xml.attrs("type", "file", "value", value, "name", partName + ".attachment"))) //
        .append("<br/>\n") //
        .append(Html.a(Xml.attrs("href", "javascript:switchFileUploadToApplet()")).nest(Xml.text("Switch to applet upload"))) //
    ;
    if (attmentExists == true) {
      html.append("<script>switchFileExists();</script>\n");
    } else {
      html.append("<script>swithFileUploadToStd();</script>\n");
    }
    String pageId = editBean.getPageId();

    String url = ctx.localUrl("/edit/UploadAppletWindow?");
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
