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

package de.micromata.genome.util.xml.xmlbuilder.html;

import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;
import de.micromata.genome.util.xml.xmlbuilder.XmlNode;

/**
 * Plugin class for XmlBuilder with common html tags.
 * 
 * Noch weit davon entfernt komplett zu sein.
 * 
 * @author roger@micromata.de
 * 
 */
public class Html
{
  public static XmlNode nbsp()
  {
    return Xml.code("&nbsp;");
  }

  public static String[] href(String link)
  {
    return new String[] { "href", link};
  }

  public static String[] id(String id)
  {
    return new String[] { "id", id};
  }

  public static String[] hclass(String className)
  {
    return new String[] { "class", className};
  }

  public static XmlElement html(XmlNode... childs)
  {
    return new XmlElement("html", childs);
  }

  public static XmlElement body(XmlNode... childs)
  {
    return new XmlElement("body", childs);
  }

  public static XmlElement html()
  {
    return new XmlElement("html");
  }

  public static XmlElement a(String[]... attributes)
  {
    return new XmlElement("a", attributes);
  }

  public static XmlElement a(String[][] attributes, XmlNode... childs)
  {
    return new XmlElement("a", attributes, childs);
  }

  public static XmlElement p(XmlNode... childs)
  {
    return new XmlElement("p", new String[0][], childs);
  }

  public static XmlElement p(String[]... attributes)
  {
    return new XmlElement("p", attributes);
  }

  public static XmlElement p(String[][] attributes, XmlNode... childs)
  {
    return new XmlElement("p", attributes, childs);
  }

  public static XmlElement br()
  {
    return new XmlElement("br", new String[0][0]);
  }

  public static XmlElement i()
  {
    return new XmlElement("i", new String[0][0]);
  }

  public static XmlElement i(String[]... attributes)
  {
    return new XmlElement("i", attributes);
  }

  public static XmlElement i(XmlNode... childs)
  {
    return new XmlElement("i", childs);
  }

  public static XmlElement i(String[][] attributes, XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("i", attributes);
    ret.nest(childs);
    return ret;
  }

  public static XmlElement textarea(String[][] attributes, XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("textarea", attributes);
    ret.nest(childs);
    return ret;
  }

  public static XmlElement input(String... attributes)
  {
    final XmlElement ret = new XmlElement("input", attributes);
    return ret;
  }

  public static XmlElement input(String[][] attributes, XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("input", attributes);
    ret.nest(childs);
    return ret;
  }

  public static XmlElement table(String[][] attributes, XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("table", attributes);
    ret.nest(childs);
    return ret;
  }

  public static XmlElement table(XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("table");
    ret.nest(childs);
    return ret;
  }

  public static XmlElement tr(String... attributes)
  {
    final XmlElement ret = new XmlElement("tr", attributes);
    return ret;
  }

  public static XmlElement tr(String[][] attributes, XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("tr", attributes);
    ret.nest(childs);
    return ret;
  }

  public static XmlElement tr(XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("tr", Xml.attrs());
    ret.nest(childs);
    return ret;
  }

  public static XmlElement th(String[][] attributes, XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("th", attributes);
    ret.nest(childs);
    return ret;
  }

  public static XmlElement th(String... attributes)
  {
    final XmlElement ret = new XmlElement("th", attributes);
    return ret;
  }

  public static XmlElement th(XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("th", Xml.attrs());
    ret.nest(childs);
    return ret;
  }

  public static XmlElement td(String[][] attributes, XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("td", attributes);
    ret.nest(childs);
    return ret;
  }

  public static XmlElement td(String... attributes)
  {
    final XmlElement ret = new XmlElement("td", attributes);
    return ret;
  }

  public static XmlElement td(XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("td", Xml.attrs());
    ret.nest(childs);
    return ret;
  }

  public static XmlElement ul(String[][] attributes, XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("ul", attributes);
    ret.nest(childs);
    return ret;
  }

  public static XmlElement ul(String... attributes)
  {
    final XmlElement ret = new XmlElement("ul", attributes);
    return ret;
  }

  public static XmlElement ul(XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("ul", Xml.attrs());
    ret.nest(childs);
    return ret;
  }

  public static XmlElement li(String[][] attributes, XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("li", attributes);
    ret.nest(childs);
    return ret;
  }

  public static XmlElement li(String... attributes)
  {
    final XmlElement ret = new XmlElement("li", attributes);
    return ret;
  }

  public static XmlElement li(XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("li", Xml.attrs());
    ret.nest(childs);
    return ret;
  }

  public static XmlElement span(String[][] attributes, XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("span", attributes);
    ret.nest(childs);
    return ret;
  }

  public static XmlElement span(String... attributes)
  {
    final XmlElement ret = new XmlElement("span", attributes);
    return ret;
  }

  public static XmlElement span(XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("span", Xml.attrs());
    ret.nest(childs);
    return ret;
  }

  public static XmlElement div(String[][] attributes, XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("div", attributes);
    ret.nest(childs);
    return ret;
  }

  public static XmlElement div(String... attributes)
  {
    final XmlElement ret = new XmlElement("div", attributes);
    return ret;
  }

  public static XmlElement div(XmlNode... childs)
  {
    final XmlElement ret = new XmlElement("div", Xml.attrs());
    ret.nest(childs);
    return ret;
  }

  public static XmlElement img(String... attributes)
  {
    final XmlElement ret = new XmlElement("img", attributes);
    return ret;
  }
}
