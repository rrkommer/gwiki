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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class Composer extends RecBase
{
  public static final int PK = 0;

  public static final int NAME = 1;

  public static final int BORN = 2;

  public static final int DIED = 3;

  public static final int COUNTRY = 6;

  public static final int NAME_ONFS = 9;

  /**
   * @param db
   * @param rec
   */
  public Composer(Mp3Db db, String[] rec)
  {
    super(db, rec);
  }

  public String getPk()
  {
    return get(PK);
  }

  public String getName()
  {
    return get(NAME);
  }

  public String getListDescription()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(getName());
    String born = get(BORN);
    if (StringUtils.equals(born, "0") == false) {
      sb.append(" (").append(born).append("-").append(get(DIED)).append(", ").append(get(COUNTRY)).append(")");
    }
    return sb.toString();
  }

  public String getDetailDescription()
  {
    StringBuilder sb = new StringBuilder();
    String born = get(BORN);
    if (StringUtils.equals(born, "0") == false) {
      sb.append("Life: ").append(esc(born)).append("-").append(get(DIED)).append("<br/>");
    }
    String country = get(COUNTRY);
    if (StringUtils.isNotBlank(country) == true) {
      sb.append("Land: ").append(esc(country)).append("<br/>");
    }
    // sb.append(esc(getName()));
    // String born = get(BORN);
    // if (StringUtils.equals(born, "0") == false) {
    // sb.append(" (").append(esc(born)).append("-").append(get(DIED)).append(")");
    // }

    return sb.toString();
  }

  public int getBornYear()
  {
    if (NumberUtils.isNumber(get(BORN)) == true) {
      return Integer.parseInt(get(BORN));
    }
    return 0;
  }

  public int getDiedYear()
  {
    if (NumberUtils.isNumber(get(DIED)) == true) {
      return Integer.parseInt(get(DIED));
    }
    return 2999;
  }

  public File getMp3Path()
  {
    if (mp3path != null) {
      return mp3path;
    }
    mp3path = new File(db.getMp3root(), getNameOnFs());
    return mp3path;
  }

  public String getNameOnFs()
  {
    return get(NAME_ONFS);
  }
}
