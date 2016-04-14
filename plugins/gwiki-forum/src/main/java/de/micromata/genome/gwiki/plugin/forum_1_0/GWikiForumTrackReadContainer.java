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

package de.micromata.genome.gwiki.plugin.forum_1_0;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.util.types.Converter;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiForumTrackReadContainer implements Serializable
{

  private static final long serialVersionUID = -5614973381281396963L;

  private long lastRead;

  private Set<String> readedPages = new HashSet<String>();

  public GWikiForumTrackReadContainer()
  {

  }

  public GWikiForumTrackReadContainer(String data)
  {
    if (StringUtils.isNotEmpty(data) == true) {
      int idx = data.indexOf(';');
      if (idx != -1) {
        String ts = data.substring(0, idx);
        data = data.substring(idx + 1);
        try {
          lastRead = GWikiProps.parseTimeStamp(ts).getTime();
        } catch (Exception ex) {
          ; // nothing
          lastRead = new Date().getTime();
        }
        List<String> pl = Converter.parseStringTokens(data, ",", false);
        readedPages.addAll(pl);
      }
    }
  }

  public boolean markRead(GWikiElementInfo ei)
  {
    if (ei == null || ei.getModifiedAt() == null) {
      return false;
    }
    if (lastRead > ei.getModifiedAt().getTime()) {
      return false;
    }
    if (readedPages.contains(ei.getId()) == true) {
      return false;
    }
    readedPages.add(ei.getId());
    return true;
  }

  public String getLastReadS()
  {
    return GWikiProps.formatTimeStamp(new Date(lastRead));
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(getLastReadS());
    sb.append(";");
    for (String s : readedPages) {
      sb.append(s);
      sb.append(",");
    }
    return sb.toString();
  }

  public long getLastRead()
  {
    return lastRead;
  }

  public void setLastRead(long lastRead)
  {
    this.lastRead = lastRead;
  }

  public Set<String> getReadedPages()
  {
    return readedPages;
  }

  public void setReadedPages(Set<String> readedPages)
  {
    this.readedPages = readedPages;
  }
}
