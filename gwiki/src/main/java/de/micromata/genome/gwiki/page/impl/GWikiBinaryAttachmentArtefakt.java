/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   22.10.2009
// Copyright Micromata 22.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import java.io.ByteArrayInputStream;
import java.util.List;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiBinaryArtefaktBase;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.attachments.TextExtractorUtils;
import de.micromata.genome.gwiki.page.search.GWikiIndexedArtefakt;
import de.micromata.genome.gwiki.utils.AppendableI;

/**
 * Containing a binary attachment.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiBinaryAttachmentArtefakt extends GWikiBinaryArtefaktBase<byte[]> implements GWikiEditableArtefakt, GWikiIndexedArtefakt
{

  private static final long serialVersionUID = 2413110416852468772L;

  public GWikiBinaryAttachmentArtefakt()
  {
  }

  public void getPreview(GWikiContext ctx, AppendableI sb)
  {
    if (ctx.getWikiElement() == null) {
      return;
    }
    String pid = ctx.getWikiElement().getElementInfo().getId();
    if (pid.contains(".") == false) {
      return;
    }
    if (getStorageData() == null) {
      return;
    }
    try {
      String t = TextExtractorUtils.getTextExtract(pid, new ByteArrayInputStream(getStorageData()));
      sb.append(t);
    } catch (Exception ex) {
      GWikiLog.note("Failure extracting text: " + ex.getMessage(), ex);
    }
  }

  public GWikiEditorArtefakt< ? > getEditor(GWikiElement elementToEdit, GWikiEditPageActionBean bean, String partKey)
  {
    return new GWikiAttachmentEditorArtefakt(elementToEdit, bean, partKey, this);
  }

  public String buildFileName(String pageId, String partName)
  {
    return partName + pageId;
  }

  public void collectArtefakts(List<GWikiArtefakt< ? >> al)
  {
    al.add(this);

  }

  public byte[] getCompiledObject()
  {
    return getStorageData();
  }

  public String getFileSuffix()
  {
    return "";
  }

  public void setCompiledObject(byte[] compiledObject)
  {
    // nix
  }

}
