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
 * Confluence Page.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class Page extends ConfluenceElement
{
  private String parent;

  private List<String> ancestors;

  private String title;

  private List<String> contentRefs;

  public Page(Element el)
  {
    super(el);
  }

  @Override
  public void parse()
  {
    ancestors = new ArrayList<String>();
    contentRefs = new ArrayList<String>();
    String parent = selectText("property[@name=\"parent\"]/id/child::text()");
    setParent(parent);
    for (Element el : selectElements("collection[@name=\"ancestors\"]/element")) {
      getAncestors().add(selectText(el, "id/child::text()"));
    }
    String title = selectText("property[@name=\"title\"]/child::text()");
    setTitle(title);
    for (Element el : selectElements("collection[@name=\"bodyContents\"]")) {
      contentRefs.add(selectText(el, "element/id/child::text()"));
    }
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("Page|")//
        .append("id:").append(getId())//
        .append("|title:").append(title)//
        .append("|ancestors:").append(ancestors)//
        .append("|content:").append(contentRefs)//
    ;
    return sb.toString();
  }

  public String getParent()
  {
    return parent;
  }

  public void setParent(String parent)
  {
    this.parent = parent;
  }

  public List<String> getAncestors()
  {
    return ancestors;
  }

  public void setAncestors(List<String> ancestors)
  {
    this.ancestors = ancestors;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public List<String> getContentRefs()
  {
    return contentRefs;
  }

  public void setContentRefs(List<String> contentRefs)
  {
    this.contentRefs = contentRefs;
  }

}
