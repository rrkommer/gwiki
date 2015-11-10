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

package de.micromata.genome.gwiki.page.search.expr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiSchedulerJobBase;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.IndexStoragePersistHandler;
import de.micromata.genome.gwiki.spi.storage.GWikiFileStorage;
import de.micromata.genome.util.runtime.CallableX;

public class SearchExpressionIndexerCallback extends GWikiSchedulerJobBase
{

  private static final long serialVersionUID = 261736968307852231L;

  public void call()
  {
    try {
      GWikiLog.note("Start build full text index");
      String pageId = args.get("pageId");
      boolean full = false;
      if (StringUtils.equals(args.get("full"), "true") == true) {
        full = true;
      }
      if (StringUtils.isNotEmpty(pageId) == true) {
        List<GWikiElementInfo> eil = new ArrayList<GWikiElementInfo>();
        GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(pageId);
        if (ei != null) {
          eil.add(ei);
        }
        rebuildIndex(wikiContext, eil, full);
      } else {
        rebuildIndex(wikiContext, wikiContext.getWikiWeb().getElementInfos(), full);
      }
      GWikiLog.note("Finished build full text index");
    } catch (Exception ex) {
      GWikiLog.warn("Job failed: " + SearchExpressionIndexerCallback.class.getName() + "; " + ex.getMessage(), ex);

    }
  }

  protected void rebuildIndex(final GWikiContext wikiContext, GWikiElementInfo ei)
  {
    IndexStoragePersistHandler pe = new IndexStoragePersistHandler();
    GWikiFileStorage storage = (GWikiFileStorage) wikiContext.getWikiWeb().getStorage();
    // TODO gwiki read lock
    GWikiElement el = storage.loadElementImpl(ei);
    Map<String, GWikiArtefakt< ? >> parts = storage.getParts(el);
    Map<String, GWikiArtefakt< ? >> cp = new HashMap<String, GWikiArtefakt< ? >>();
    Map<String, GWikiArtefakt< ? >> np = new HashMap<String, GWikiArtefakt< ? >>();
    cp.putAll(parts);
    pe.onPersist(wikiContext, storage, el, parts);
    for (Map.Entry<String, GWikiArtefakt< ? >> me : parts.entrySet()) {
      if (cp.containsKey(me.getKey()) == false) {
        np.put(me.getKey(), me.getValue());
      }
    }
    storage.storeImplNoTrans(el, np);
  }

  public void rebuildIndex(final GWikiContext wikiContext, final Iterable<GWikiElementInfo> eis, final boolean completeIndex)
  {

    wikiContext.getWikiWeb().getAuthorization().runAsSu(wikiContext, new CallableX<Void, RuntimeException>() {

      public Void call() throws RuntimeException
      {
        wikiContext.getWikiWeb().getStorage().rebuildIndex(wikiContext, eis, completeIndex);
        return null;
      }
    });

  }
}