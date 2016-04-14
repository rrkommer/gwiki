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

import java.util.List;

public class Interpret extends RecBase
{
  public static final int PK = 0;

  public static final int NAME = 1;

  public static final int DETAIL_NAME = 0;

  public static final int INSTRUMENT = 2;

  public static final int DETAIL_INSTRUMENT = 1;

  public static final int DETAIL_TITEL_FK = 2;

  public static final int ROLLE = 3;

  public Interpret(Mp3Db db, String[] rec)
  {
    super(db, rec);
  }

  public List<Title> getTitles()
  {
    return db.getTitelsFromInterpret(this);
  }

  public String getPk()
  {
    return get(PK);
  }

  public String getName()
  {
    return get(NAME);
  }

  public String getInstrument()
  {
    return get(INSTRUMENT);
  }

}
