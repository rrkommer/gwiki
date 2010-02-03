/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   27.10.2009
// Copyright Micromata 27.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.actionbean;

import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import de.micromata.genome.gwiki.page.GWikiContext;

public class CommonMutipartRequestHandler
{
  @SuppressWarnings({ "deprecation", "unchecked"})
  public static void handleMultiPartRequest(GWikiContext ctx)
  {
    
    if (ServletFileUpload.isMultipartContent(ctx.getRequest()) == false) {
      return;
    }
    try {
      ServletFileUpload sfc = new ServletFileUpload(new DiskFileItemFactory());
      List<FileItem> files = (List<FileItem>) sfc.parseRequest(ctx.getRequest());
      CommonMultipartRequest req = new CommonMultipartRequest(ctx.getRequest());
      for (FileItem fi : files) {
        if (fi.isFormField() == true) {
          req.addFormField(fi);
        } else {
          req.addFileItem(fi);
        }
      }
      ctx.setRequest(req);
    } catch (Exception ex) {
      ctx.addSimpleValidationError("Failed to upload: " + ex.getMessage());
    }
  }
}
