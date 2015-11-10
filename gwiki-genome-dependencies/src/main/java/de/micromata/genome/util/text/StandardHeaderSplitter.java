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

/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    lado@micromata.de
// Created   Feb 14, 2008
// Copyright Micromata Feb 14, 2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.text;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.types.Pair;

public class StandardHeaderSplitter
{

  public static Pair<String, Map<String, String>> split(String text) throws IOException
  {
    LineNumberReader lnr = new LineNumberReader(new StringReader(text));
    Map<String, String> headers = new HashMap<String, String>();
    String line = null;
    boolean leedingNewlines = true;
    while ((line = lnr.readLine()) != null) {
      if (StringUtils.isBlank(line)) {
        if (leedingNewlines == true) {// es kann sein, dass am Anfang die Newlines sind(wegen Code, etc)
          continue;
        } else {
          break;
        }
      }
      String key = StringUtils.substringBefore(line, ":");
      String value = StringUtils.substringAfter(line, ":");
      headers.put(StringUtils.trim(key), StringUtils.trim(value));
      leedingNewlines = false;
    }
    if (headers.size() == 0) {
      return new Pair<String, Map<String, String>>(text, headers);
    }
    return new Pair<String, Map<String, String>>(slurp(lnr), headers);
  }

  public static String slurp(Reader in) throws IOException
  {
    StringBuilder sb = new StringBuilder();
    char[] b = new char[4096];
    for (int n; (n = in.read(b)) != -1;) {
      sb.append(new String(b, 0, n));
    }
    return sb.toString();
  }
}
