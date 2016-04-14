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

import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;

public class CommonMutipartRequestHandler
{
  boolean hasAnyFileItems(CommonMultipartRequest req)
  {
    return false;
  }

  @SuppressWarnings({ "unchecked"})
  public static void handleMultiPartRequest(GWikiContext ctx)
  {

    if (ServletFileUpload.isMultipartContent(ctx.getRequest()) == false) {
      return;
    }

    try {
      ServletFileUpload sfc = new ServletFileUpload(ctx.getWikiWeb().getDaoContext().getFileItemFactory());
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
      GWikiLog.warn("Failed to upload file: " + ex.getMessage(), ex);
      ctx.addSimpleValidationError("Failed to upload: " + ex.getMessage());
    }
  }
}
