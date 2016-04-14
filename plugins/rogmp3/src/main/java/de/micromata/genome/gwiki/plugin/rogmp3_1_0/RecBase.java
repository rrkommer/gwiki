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

package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public class RecBase
{
  protected Mp3Db db;

  protected String[] rec;

  protected File mp3path;

  public String toString()
  {
    return getClass().getSimpleName() + ": " + rec;
  }

  /**
   * @param db
   * @param rec
   */
  public RecBase(Mp3Db db, String[] rec)
  {
    this.db = db;
    this.rec = rec;
  }

  public static String normalize(String n)
  {
    n = StringUtils.replace(n, " ", "_");
    return n;
  }

  public static <T extends RecBase> void sortByIdx(List<T> list, final int idx, boolean desc)
  {
    if (desc == true) {
      Collections.sort(list, new Comparator<T>() {

        @Override
        public int compare(T o1, T o2)
        {
          return o2.get(idx).compareTo(o1.get(idx));
        }
      });
    } else {
      Collections.sort(list, new Comparator<T>() {

        @Override
        public int compare(T o1, T o2)
        {
          return o1.get(idx).compareTo(o2.get(idx));
        }
      });
    }
  }

  public static String esc(String data)
  {
    if (data == null) {
      return "";
    }
    return StringEscapeUtils.escapeHtml(data);
  }

  public static boolean isRecFilled(String[] rec, int idx)
  {
    if (rec.length <= idx) {
      return false;
    }
    return StringUtils.isNotBlank(rec[idx]);
  }

  public String get(int idx)
  {
    if (rec.length > idx) {
      return rec[idx];
    }
    return "";
  }

  protected String renderDetailRec(String label, int idx)
  {
    String s = get(idx);
    if (StringUtils.isNotEmpty(s) == true) {
      return label + esc(s) + "<br/>";
    }
    return "";
  }

  public Mp3Db getDb()
  {
    return db;
  }

  public void setDb(Mp3Db db)
  {
    this.db = db;
  }

  public String[] getRec()
  {
    return rec;
  }

  public void setRec(String[] rec)
  {
    this.rec = rec;
  }

}
