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
package de.micromata.genome.gwiki.plugin.rssfeed_1_0;

import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;
import de.micromata.genome.util.xml.xmlbuilder.XmlNode;

/**
 * @author Ingo Joseph
 *
 */
public class Atom 
{
  //Atom
  public static XmlElement feed(String xmlns)
  {
    return Xml.element("feed", Xml.attrs("xmlns", xmlns));
  }
  
  public static XmlElement name(XmlNode... childs)
  {
    XmlElement ret = Xml.element("name", Xml.attrs());
    ret.nest(childs);
    return ret;
  }
  
  public static XmlElement subtitle(XmlNode... childs)
  {
    XmlElement ret = Xml.element("subtitle", Xml.attrs());
    ret.nest(childs);
    return ret;
  }

  public static XmlElement updated(XmlNode... childs)
  {
    XmlElement ret = Xml.element("updated", Xml.attrs());
    ret.nest(childs);
    return ret;
  }
  
  public static XmlElement entry(XmlNode... childs)
  {
    XmlElement ret = Xml.element("entry", Xml.attrs());
    ret.nest(childs);
    return ret;
  }
  
  public static XmlElement link(String href)
  {
    return Xml.element("link", Xml.attrs("href", href));
  }
  
  public static XmlElement summary(String type)
  {
    return Xml.element("summary", Xml.attrs("type", type));
  }
  
  public static XmlElement content(String type)
  {
    return Xml.element("content", Xml.attrs("type", type));
  }
  
  public static XmlElement link(String[][] attributes)
  {
    return new XmlElement("link", attributes);
  }
}