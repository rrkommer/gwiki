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

package de.micromata.genome.gwiki.tools.confluence.imp;

import java.util.List;

import org.dom4j.CharacterData;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;

/**
 * Internal entity element for confluence import.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class Entity
{
  private String id;

  protected Element element;

  public Entity(Element el)
  {
    this.element = el;
    parseId();
    parse();
  }

  public void parseId()
  {
    id = selectElement(element, "id").getText();
  }

  public abstract void parse();

  public String toString()
  {
    return "Entity|id:" + id;
  }

  @SuppressWarnings("unchecked")
  public List<Element> selectElements(Element el, String path)
  {
    return el.selectNodes(path);
  }

  @SuppressWarnings("unchecked")
  public List<Node> selectNodes(Element el, String path)
  {
    return el.selectNodes(path);
  }

  public List<Element> selectElements(String path)
  {
    return selectElements(element, path);
  }

  public List<Node> selectNodes(String path)
  {
    return selectNodes(element, path);
  }

  public Element selectElement(Element el, String path)
  {
    List<Element> els = selectElements(el, path);
    if (els.size() == 0) {
      return null;
    }
    return els.get(0);
  }

  public Element selectElement(String path)
  {
    return selectElement(element, path);
  }

  public Element selectProperty(String propName)
  {
    return selectElement(element, "property[@name=\"" + propName + "\"]");
  }

  public String selectText(String path)
  {
    return selectText(element, path);
  }

  public String selectText(Element el, String path)
  {
    if (el == null)
      return null;
    Object obj = el.selectObject(path);

    if (obj == null) {
      return null;
    }
    if ((obj instanceof CharacterData) == true) {
      return ((CharacterData) obj).getText();
    }
    if ((obj instanceof List< ? >) == true) {
      StringBuilder sb = new StringBuilder();
      List< ? > l = (List< ? >) obj;
      for (Object le : l) {
        if (le instanceof CharacterData) {
          sb.append(((CharacterData) le).getText());
        }
      }
      return sb.toString();
    }
    if ((obj instanceof Text) == false) {
      return null;
    }
    Text text = (Text) obj;
    if (text == null)
      return null;
    return text.getText();
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }
}
