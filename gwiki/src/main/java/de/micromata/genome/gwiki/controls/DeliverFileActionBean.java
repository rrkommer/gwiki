/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   25.10.2009
// Copyright Micromata 25.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.controls;

import java.io.IOException;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiBinaryArtefakt;
import de.micromata.genome.gwiki.model.GWikiTextArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * 
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@Deprecated
public class DeliverFileActionBean extends ActionBeanBase
{

  @Override
  public Object onInit()
  {
    GWikiArtefakt< ? > fa = getWikiContext().getParts().get("File");
    if (fa == null) {
      throw new RuntimeException("No Part named File found");
    }
    if (fa instanceof GWikiBinaryArtefakt< ? >) {
      deliver((GWikiBinaryArtefakt< ? >) fa);
    } else if (fa instanceof GWikiTextArtefakt< ? >) {
      throw new RuntimeException("Deliver text artefakt currently not supported");
    } else {
      throw new RuntimeException("Unknown artefakt type: " + fa.getClass().getName());
    }
    return super.onInit();
  }

  protected void deliver(GWikiBinaryArtefakt<?> bin)
  {
    byte[] data = bin.getStorageData();
    if (data == null) {
      return;
    }
    // TODO mime type
    // TODO file name
    getWikiContext().getResponse().setContentLength(data.length);
    try {
      getWikiContext().getResponse().getOutputStream().write(data);
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }

  }
}
