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
package de.micromata.genome.util.xml.xmlbuilder.html;

import junit.framework.TestCase;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class HtmlInputTest extends TestCase
{
  public void testInput()
  {
    XmlElement xe = Html.input();
    String s = xe.toString();
    System.out.println(s);

    xe = Html.input("type", "text");
    xe.nest(Xml.text("asdfasdf"));
    s = xe.toString();
    System.out.println(s);
  }
}
