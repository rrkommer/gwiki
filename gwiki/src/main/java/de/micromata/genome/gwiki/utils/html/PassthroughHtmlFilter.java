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

package de.micromata.genome.gwiki.utils.html;

import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.filters.Writer;

/**
 * Filter just pass throgh. Can be used as base implementation.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class PassthroughHtmlFilter extends Writer
{

  public PassthroughHtmlFilter()
  {
    super();
  }

  public PassthroughHtmlFilter(OutputStream outputStream, String encoding) throws UnsupportedEncodingException
  {
    super(outputStream, encoding);
  }

  public PassthroughHtmlFilter(java.io.Writer writer, String encoding)
  {
    super(writer, encoding);
  }

  public void doFilter(String html)
  {
    XMLParserConfiguration parser = new HTMLConfiguration();
    parser.setProperty("http://cyberneko.org/html/properties/filters", new XMLDocumentFilter[] { this});
    XMLInputSource source = new XMLInputSource(null, null, null, new StringReader(html), "UTF-8");
    try {
      parser.parse(source);
      return;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /** XML declaration. */
  public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException
  {
    super.xmlDecl(version, encoding, standalone, augs);
  } // xmlDecl(String,String,String,Augmentations)

  /** Doctype declaration. */
  public void doctypeDecl(String root, String publicId, String systemId, Augmentations augs) throws XNIException
  {
    // <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    fPrinter.print("<!DOCTYPE " + root + " PUBLIC \"" + publicId + "\" \"" + systemId + "\">");
  } // doctypeDecl(String,String,String,Augmentations)

}
