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

import org.junit.Ignore;
import org.junit.Test;

public class GenITunesTest
{
  @Ignore
  @Test
  public void testGen()
  {
    String dbPath = "D:\\ourMP3gwiki\\acccexp";
    String mproot = "D:\\ourMP3\\mp3root\\classic";
    String outF = "D:\\ourMP3gwiki\\rogMp3.xml";
    Mp3Db db = Mp3Db.get(dbPath, mproot);

    GenItunesFile git = new GenItunesFile(db, outF);
    git.renderItunes();
    System.out.println("MP3 size: " + git.getSumMp3Size() / (1024 * 1024) + " MB");
  }
}
