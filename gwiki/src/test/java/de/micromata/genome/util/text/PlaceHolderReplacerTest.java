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

package de.micromata.genome.util.text;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class PlaceHolderReplacerTest extends TestCase
{
  public void testIt()
  {
    Map<String, String> mailContext = new HashMap<String, String>();
    mailContext.put("USER", "A");
    mailContext.put("PUBURL", "http://localhost:8080/");
    mailContext.put("NEWPASS", "X");
    String pattern = "The password for user ${USER} on\n${PUBURL}\nhas changed to: ${NEWPASS};";
    String message = PlaceHolderReplacer.resolveReplaceDollarVars(pattern, mailContext);
    System.out.println(message);

  }
}
