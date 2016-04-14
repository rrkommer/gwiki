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

package de.micromata.genome.gwiki.jetty;

import java.util.function.Supplier;

import de.micromata.genome.util.types.Pair;

public enum StandaloneDatabases
{
  LOCAL_DERBY(1, "Embedded Derby", "org.apache.derby.jdbc.EmbeddedDriver", "", () -> {
    String path = CmdLineInput.getInput("Path to store database", "gwiki_derby");
    String url = "jdbc:derby:" + path;
    return Pair.make(url + ";create=true", url);
  }),

  ORACLE(2, "Oracle",
      "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@localhost:1521:gwiki"),

  POSTGRES(3, "Postgres",
      "org.postgresql.Driver", "jdbc:postgresql://localhost:5432/gwiki")

  ;

  public static StandaloneDatabases getDatabaseByInput(String input)
  {
    for (StandaloneDatabases db : values()) {
      if (Integer.toString(db.getNum()).equals(input) == true) {
        return db;
      }
    }
    return null;
  }

  private int num;
  private String description;
  private String driver;
  private String sampleUrl;
  private Supplier<Pair<String, String>> cmdlineUrlSuplider;

  private StandaloneDatabases(int num, String description, String driver, String sampleUrl)
  {
    this(num, description, driver, sampleUrl, null);
  }

  private StandaloneDatabases(int num, String description, String driver, String sampleUrl,
      Supplier<Pair<String, String>> cmdlineUrlSuplider)
  {
    this.num = num;
    this.description = description;
    this.driver = driver;
    this.sampleUrl = sampleUrl;
    this.cmdlineUrlSuplider = cmdlineUrlSuplider;
  }

  public int getNum()
  {
    return num;
  }

  public String getDescription()
  {
    return description;
  }

  public String getDriver()
  {
    return driver;
  }

  public String getSampleUrl()
  {
    return sampleUrl;
  }

  public Supplier<Pair<String, String>> getCmdlineUrlSuplider()
  {
    return cmdlineUrlSuplider;
  }

}
