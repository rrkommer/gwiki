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
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import de.micromata.genome.gwiki.model.GWikiAbstractElement;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiAttachment;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * GWikiElement for an attachment.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFileAttachment extends GWikiAbstractElement implements GWikiAttachment, GWikiPropKeys
{

  private static final long serialVersionUID = 592490717243853925L;

  private GWikiBinaryAttachmentArtefakt data = new GWikiBinaryAttachmentArtefakt();

  public GWikiFileAttachment(GWikiElementInfo elementInfo)
  {
    super(elementInfo);
  }

  public GWikiFileAttachment(GWikiFileAttachment other)
  {
    super(other.getElementInfo());
  }

  public String getContentType()
  {
    return getElementInfo().getProps().getStringValue(CONTENTYPE);
  }

  public int getSize()
  {
    return data.getStorageData() == null ? 0 : data.getStorageData().length;
  }

  public String getType()
  {
    return "attachment";
  }

  public GWikiArtefakt< ? > getMainPart()
  {
    return data;
  }

  public void serve(GWikiContext ctx)
  {
    ctx.getResponse().setContentType(getContentType());
    ctx.getResponse().setContentLength((int) getSize());
    try {
      byte[] bd = data.getStorageData();
      if (bd != null) {
        OutputStream os = ctx.getResponse().getOutputStream();
        IOUtils.copy(new ByteArrayInputStream(bd), os);
      }
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  public GWikiBinaryAttachmentArtefakt getData()
  {
    return data;
  }

  public void collectParts(Map<String, GWikiArtefakt< ? >> map)
  {
    map.put("", data);
    super.collectParts(map);

  }
}
