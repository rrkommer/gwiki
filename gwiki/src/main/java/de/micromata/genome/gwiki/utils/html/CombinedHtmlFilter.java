/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   03.12.2009
// Copyright Micromata 03.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.utils.html;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.filters.DefaultFilter;

/**
 * Html filter to combine two other filter.
 * 
 * @author roger
 * 
 */
public class CombinedHtmlFilter extends DefaultFilter
{
  protected DefaultFilter first;

  protected DefaultFilter second;

  public CombinedHtmlFilter()
  {

  }

  public CombinedHtmlFilter(DefaultFilter first, DefaultFilter second)
  {
    super();
    this.first = first;
    this.second = second;
  }

  public void characters(XMLString text, Augmentations augs) throws XNIException
  {
    first.characters(text, augs);
    second.characters(text, augs);
  }

  public void comment(XMLString text, Augmentations augs) throws XNIException
  {
    first.comment(text, augs);
    second.comment(text, augs);
  }

  public void doctypeDecl(String root, String publicId, String systemId, Augmentations augs) throws XNIException
  {
    first.doctypeDecl(root, publicId, systemId, augs);
    second.doctypeDecl(root, publicId, systemId, augs);
  }

  public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException
  {
    first.emptyElement(element, attributes, augs);
    second.emptyElement(element, attributes, augs);
  }

  public void endCDATA(Augmentations augs) throws XNIException
  {
    first.endCDATA(augs);
    second.endCDATA(augs);
  }

  public void endDocument(Augmentations augs) throws XNIException
  {
    first.endDocument(augs);
    second.endDocument(augs);
  }

  public void endElement(QName element, Augmentations augs) throws XNIException
  {
    first.endElement(element, augs);
    second.endElement(element, augs);
  }

  public void endGeneralEntity(String name, Augmentations augs) throws XNIException
  {
    first.endGeneralEntity(name, augs);
    second.endGeneralEntity(name, augs);
  }

  public void endPrefixMapping(String prefix, Augmentations augs) throws XNIException
  {
    first.endPrefixMapping(prefix, augs);
    second.endPrefixMapping(prefix, augs);
  }

  public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException
  {
    first.ignorableWhitespace(text, augs);
    second.ignorableWhitespace(text, augs);
  }

  public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException
  {
    first.processingInstruction(target, data, augs);
    second.processingInstruction(target, data, augs);
  }

  public void startCDATA(Augmentations augs) throws XNIException
  {
    first.startCDATA(augs);
    second.startCDATA(augs);
  }

  public void startDocument(XMLLocator locator, String encoding, Augmentations augs) throws XNIException
  {
    first.startDocument(locator, encoding, augs);
    second.startDocument(locator, encoding, augs);
  }

  public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs) throws XNIException
  {
    first.startDocument(locator, encoding, nscontext, augs);
    second.startDocument(locator, encoding, nscontext, augs);
  }

  public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException
  {
    first.startElement(element, attributes, augs);
    second.startElement(element, attributes, augs);
  }

  public void startGeneralEntity(String name, XMLResourceIdentifier id, String encoding, Augmentations augs) throws XNIException
  {
    first.startGeneralEntity(name, id, encoding, augs);
    second.startGeneralEntity(name, id, encoding, augs);
  }

  public void startPrefixMapping(String prefix, String uri, Augmentations augs) throws XNIException
  {
    first.startPrefixMapping(prefix, uri, augs);
    second.startPrefixMapping(prefix, uri, augs);
  }

  public void textDecl(String version, String encoding, Augmentations augs) throws XNIException
  {
    first.textDecl(version, encoding, augs);
    second.textDecl(version, encoding, augs);
  }

  public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException
  {
    first.xmlDecl(version, encoding, standalone, augs);
    second.xmlDecl(version, encoding, standalone, augs);
  }
}
