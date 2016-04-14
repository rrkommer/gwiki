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

package de.micromata.genome.gwiki.plugin.rssfeed_1_0;

import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;
import de.micromata.genome.util.xml.xmlbuilder.XmlNode;

/**
 * @author Ingo Joseph
 * 
 */
public class RSS
{
  public static XmlElement rss(String version)
  {
    return Xml.element("rss", Xml.attrs("version", version));
  }

  public static XmlElement channel(XmlNode... childs)
  {
    XmlElement ret = Xml.element("channel", Xml.attrs());
    ret.nest(childs);
    return ret;
  }

  public static XmlElement title(XmlNode... childs)
  {
    XmlElement ret = Xml.element("title", Xml.attrs());
    ret.nest(childs);
    return ret;
  }

  public static XmlElement link(XmlNode... childs)
  {
    XmlElement ret = Xml.element("link", Xml.attrs());
    ret.nest(childs);
    return ret;
  }

  public static XmlElement description(XmlNode... childs)
  {
    XmlElement ret = Xml.element("description", Xml.attrs());
    ret.nest(childs);
    return ret;
  }

  public static XmlElement copyright(XmlNode... childs)
  {
    XmlElement ret = Xml.element("copyright", Xml.attrs());
    ret.nest(childs);
    return ret;
  }

  public static XmlElement language(XmlNode... childs)
  {
    XmlElement ret = Xml.element("language", Xml.attrs());
    ret.nest(childs);
    return ret;
  }

  public static XmlElement pubData(XmlNode... childs)
  {
    XmlElement ret = Xml.element("pubData", Xml.attrs());
    ret.nest(childs);
    return ret;
  }

  public static XmlElement item(XmlNode... childs)
  {
    XmlElement ret = Xml.element("item", Xml.attrs());
    ret.nest(childs);
    return ret;
  }
  
  public static XmlElement author(XmlNode... childs)
  {
    XmlElement ret = Xml.element("author", Xml.attrs());
    ret.nest(childs);
    return ret;
  }
  
  public static XmlElement xmlHeader() {
    return null;
  }
}