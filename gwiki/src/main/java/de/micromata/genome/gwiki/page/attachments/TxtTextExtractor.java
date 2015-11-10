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

package de.micromata.genome.gwiki.page.attachments;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * Extracts text from a text file.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class TxtTextExtractor implements TextExtractor
{

  public String extractText(String fileName, InputStream data)
  {
    try {
      StringWriter sout = new StringWriter();
      IOUtils.copy(data, sout, "ISO-8859-1");
      return sout.toString();
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

}
