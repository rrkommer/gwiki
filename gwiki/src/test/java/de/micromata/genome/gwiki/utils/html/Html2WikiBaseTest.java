////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010 Micromata GmbH
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
package de.micromata.genome.gwiki.utils.html;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class Html2WikiBaseTest extends TestCase
{
  protected String transform(String html)
  {
    return Html2WikiFilter.html2Wiki(html);
  }

  protected String transform(String html, String... htmle)
  {
    Set<String> s = new HashSet<String>();
    for (String h : htmle) {
      s.add(h);
    }
    return Html2WikiFilter.html2Wiki(html, s);
  }
}
