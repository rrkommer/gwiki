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
