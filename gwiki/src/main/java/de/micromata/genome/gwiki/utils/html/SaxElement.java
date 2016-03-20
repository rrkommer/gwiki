package de.micromata.genome.gwiki.utils.html;

import org.apache.xerces.xni.XMLAttributes;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class SaxElement
{
  String elementName;

  XMLAttributes attributes;

  boolean hasBody;

  public SaxElement(String elementName, XMLAttributes attributes, boolean hasBody)
  {
    this.elementName = elementName.toLowerCase();
    this.attributes = attributes;
    this.hasBody = hasBody;
  }

  public String getElementName()
  {
    return elementName;
  }

  public XMLAttributes getAttributes()
  {
    return attributes;
  }

  public boolean isHasBody()
  {
    return hasBody;
  }

}
