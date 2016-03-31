////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

  @Override
  public String getContentType()
  {
    return getElementInfo().getProps().getStringValue(CONTENTYPE);
  }

  @Override
  public int getSize()
  {
    return data.getStorageData() == null ? 0 : data.getStorageData().length;
  }

  @Override
  public String getType()
  {
    return "attachment";
  }

  @Override
  public GWikiArtefakt<?> getMainPart()
  {
    return data;
  }

  @Override
  public void serve(GWikiContext ctx)
  {
    String contt = ctx.getWikiWeb().getDaoContext().getMimeTypeProvider().getMimeType(ctx, this.getElementInfo());
    if (contt != null) {
      ctx.getResponse().setContentType(contt);
    }
    ctx.getResponse().setContentLength(getSize());
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

  @Override
  public void collectParts(Map<String, GWikiArtefakt<?>> map)
  {
    map.put("", data);
    super.collectParts(map);

  }
}
