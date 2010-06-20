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

package de.micromata.genome.gwiki.plugin.confluenceimporter_1_0.confluence;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

/**
 * Holds a confluence body element.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class BodyContent extends Entity
{
  private List<String> bodies;

  public BodyContent(Element el)
  {
    super(el);
  }

  @Override
  public void parse()
  {
    bodies = new ArrayList<String>();
    List<Element> nodes = selectElements("property[@name = \"body\"]");
    for (Element n : nodes) {
      // String s = n.toString();
      bodies.add(selectText(n, "child::text()"));
      // bodies.add(selectText(n, ))
    }
    // ent.setBody(selectText(tel, "property[@name = \"body\"]/child::text()"));

  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("BodyContent|")//
        .append("id:").append(getId())//
        .append("|body:").append(bodies)//
    ;
    return sb.toString();
  }

  public List<String> getBodies()
  {
    return bodies;
  }

  public void setBodies(List<String> bodies)
  {
    this.bodies = bodies;
  }

}
