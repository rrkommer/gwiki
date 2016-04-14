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

package de.micromata.genome.gwiki.plugin.mp3extractor_1_0;

import java.io.InputStream;

import org.apache.commons.lang.StringUtils;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.Mp3File;

import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.attachments.TextExtractor;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.LogExceptionAttribute;

/**
 * Simple mp3 extractor
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class Mp3TextExtractor implements TextExtractor
{

  private void appendIfExists(StringBuilder sb, String label, String data)
  {
    if (StringUtils.isBlank(data) == true) {
      return;
    }
    sb.append(label).append(": ").append(data).append("\n");
  }

  @Override
  public String extractText(String fileName, InputStream data)
  {
    StringBuilder sb = new StringBuilder();
    try {
      Mp3File mp3file = new Mp3File(fileName);
      if (mp3file.hasId3v1Tag() == true) {
        ID3v1 tag = mp3file.getId3v1Tag();
        appendIfExists(sb, "Artist", tag.getArtist());
        appendIfExists(sb, "Album", tag.getAlbum());
        appendIfExists(sb, "Title", tag.getTitle());
        appendIfExists(sb, "Track", tag.getTrack());
        appendIfExists(sb, "Comment", tag.getComment());
      }
    } catch (Exception e) {
      GLog.note(GWikiLogCategory.Wiki, "Cannot not extract mp3 content: " + e.getMessage(),
          new LogExceptionAttribute(e));
    }
    return sb.toString();
  }

}
