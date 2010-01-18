package de.micromata.genome.util.xml.xmlbuilder;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang.Validate;

/**
 * Ein XML-Element (tag) 
 * 
 * @author roger@micromata.de
 * 
 */
public class XmlElement extends XmlWithChilds
{
  public static final String[] EMPTY_STRINGARRAY = new String[0];

  public static final String[][] EMPTY_STRINGARRAYARRAY = new String[0][0];

  /**
   * element name
   */
  private String name;

  /**
   * attributes. Must be even.
   */
  private String[] attributes;

  public XmlElement()
  {

  }

  /**
   * Simple Element
   * 
   * @param tagName
   */
  public XmlElement(String tagName)
  {
    this.name = tagName;
  }

  /**
   * Element with attributes
   * 
   * @param name
   * @param attrs each element contains 2 elements for name and value
   */
  public XmlElement(String name, String[]... attrs)
  {
    this(name, join(attrs));
  }

  /**
   * Element with attributes and childs.
   * 
   * @param name
   * @param attrs
   * @param childs
   */
  public XmlElement(String name, String[][] attrs, XmlNode... childs)
  {
    this(name, join(attrs));
    nest(childs);
  }

  public XmlElement(String name, XmlNode... childs)
  {
    this(name, EMPTY_STRINGARRAYARRAY, childs);
  }

  /**
   * 
   * @param name
   * @param attrs must be even.
   */
  public XmlElement(String name, String... attrs)
  {
    Validate.notNull(name);
    Validate.notNull(attrs);
    Validate.isTrue(attrs.length % 2 == 0);
    this.name = name;
    this.attributes = attrs;
  }

  public static String[] join(String[]... attrs)
  {
    if (attrs.length == 0)
      return EMPTY_STRINGARRAY;
    if (attrs.length == 1)
      return attrs[0];

    String[] res = new String[attrs.length * 2];
    int idx = 0;
    for (String[] a : attrs) {
      res[idx++] = a[0];
      res[idx++] = a[1];
    }
    return res;
  }

  public XmlElement add(XmlNode node)
  {
    if (childs == null) {
      childs = new ArrayList<XmlNode>();
    }
    childs.add(node);
    return this;
  }

  public XmlElement nest(XmlNode... childs)
  {
    if (childs.length == 0)
      return this;

    if (this.childs == null) {
      this.childs = new ArrayList<XmlNode>();
    }
    for (XmlNode c : childs)
      this.childs.add(c);
    return this;
  }

  public static void renderAttributes(String[] attributes, XmlRenderer sb) throws IOException
  {
    if (attributes != null && attributes.length > 0) {
      for (int i = 0; i < attributes.length; ++i) {
        String n = attributes[i++];
        String v = attributes[i];
        sb.code(" ").code(n).code("=\"").text(v).code("\"");
      }
    }
  }
  /**
   * Methode zum generieren der XML-Ausgabe.
   * 
   * @param sb
   * @param ident
   */
  public void toXml(XmlRenderer sb) throws IOException
  {
    if (name == null) {
      toStringChilds(sb);
      return;
    }
    sb.elementBeginOpen().code(name);
    renderAttributes(attributes, sb);

    if (childs == null || childs.size() == 0) {
      sb.elementBeginEndClosed();
      return;
    }
    sb.elementBeginEndOpen();
    toStringChilds(sb);
    // if (sb.charAt(sb.length() - 1) == '>') {
    // sb.append("\n").append(ident);
    // }
    sb.elementEndOpen().code(name).elementEndClose();
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String[] getAttributes()
  {
    return attributes;
  }

  public void setAttributes(String[] attributes)
  {
    this.attributes = attributes;
  }

}
