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

import static de.micromata.genome.util.xml.xmlbuilder.Xml.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import de.micromata.genome.util.xml.xmlbuilder.XmlElement;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

public class Mp3Stats
{

  public static void genStatistics(Mp3Db db, XmlElement node)
  {
    DecimalFormat df = new DecimalFormat("#,###");
    node.add(Html.p(code("Komponisten: " + df.format(db.composers.table.size()))));
    List<Media> ml = db.getMedia();
    int cdc = 0;
    for (Media m : ml) {
      String mc = m.get(Media.MEDIACOUNT);
      if (StringUtils.isBlank(mc) == true) {
        ++cdc;
        continue;
      }
      try {
        cdc += NumberUtils.createInteger(mc);
      } catch (NumberFormatException ex) {
        ++cdc;
      }
    }
    int tcest = 36000;
    node.add(Html.p(code("Medien: " + df.format(ml.size()) + ", single: " + df.format(cdc))));
    node.add(Html.p(code("Titel: " + df.format(db.title.table.size()))));
    node.add(Html.p(code("<i>Only Parts with trackinfo</i>:")));
    double factor = (double) tcest / (double) db.tracks.table.size();

    node.add(Html.p(code("Tracks: "
        + df.format(db.tracks.table.size()))));
    long timesec = 0;
    long sizebytes = 0;
    for (String[] td : db.tracks.table) {
      Track track = new Track(db, td);
      timesec += track.getTime();
      sizebytes += track.getSize();
    }
    long esttimesec = (long) (factor * timesec);
    SimpleDateFormat sd = new SimpleDateFormat("MM-dd HH:mm:ss");
    String dur = sd.format(new Date(timesec * 1000));
    String esdur = sd.format(new Date(esttimesec * 1000));
    String size = df.format(sizebytes);

    node.add(Html.p(code("Total time: "
        + dur
        + ", "
        + df.format((int) (timesec / 3600))
        + " hours")));
    node.add(Html.p(code("Total size: " + size + " (Parts)")));
    
    node.add(Html.p(code("Orchester: " + df.format(db.orchesters.table.size()))));
    node.add(Html.p(code("Intepreten: " + df.format(db.interprets.table.size()))));
  }
}
