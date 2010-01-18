/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   13.06.2009
// Copyright Micromata 13.06.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.xml.xmlbuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * XML Shortcuts.
 * 
 * Status noch alpha und nicht komplett.
 * 
 * @todo <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
 * @todo CDATA
 * 
 * @author roger@micromata.de
 * 
 */
public class Xml
{
  public static XmlDocument xml(XmlNode... elements)
  {
    return new XmlDocument(elements);
  }

  public static XmlDocument xml(String[][] attrs, XmlNode... elements)
  {
    return new XmlDocument(attrs, elements);
  }

  public static XmlNode[] nodes(XmlNode... childs)
  {
    return childs;
  }

  public static XmlNode comment(String comment)
  {
    return new XmlComment(comment);
  }

  public static XmlNode code(String code)
  {
    return new XmlCode(code);
  }

  /**
   * just a () wrapper.
   * 
   * @param attrs
   * @return
   */
  public static String[][] attrs(String[]... attrs)
  {
    return attrs;
  }

  public static String[][] attrs(String... attrs)
  {
    if (attrs.length == 0)
      return XmlElement.EMPTY_STRINGARRAYARRAY;
    String[][] ret = new String[attrs.length / 2][2];
    for (int i = 0; i < attrs.length;) {
      ret[i / 2][0] = attrs[i++];
      ret[i / 2][1] = attrs[i++];
    }
    return ret;
  }

  public static XmlElement element(String elemName, String[][] attrs)
  {
    return new XmlElement(elemName, attrs);
  }

  public static XmlElement element(String elemName, XmlNode... childs)
  {
    return new XmlElement(elemName, childs);
  }

  public static XmlElement element(String elemName, String... attrs)
  {
    return new XmlElement(elemName, attrs);
  }

  public static XmlElement element(String elemName, String[][] attrs, XmlNode... childs)
  {
    return new XmlElement(elemName, attrs, childs);
  }

  public static XmlText text(String text)
  {

    if (text == null)
      text = "";
    return new XmlText(text);
  }

  public static <T> List<T> asList(T... values)
  {
    ArrayList<T> ret = new ArrayList<T>(values.length);
    for (T v : values) {
      ret.add(v);
    }
    return ret;
  }

  public static String[][] listAsAttrs(List<String> l)
  {
    return attrs(l.toArray(new String[] {}));
  }

  public static <T> List<T> add(List<T> list, T... ts)
  {
    for (T t : ts) {
      list.add(t);
    }
    return list;
  }
}
