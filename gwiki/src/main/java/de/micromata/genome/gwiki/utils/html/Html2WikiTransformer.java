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

import java.util.List;

import org.apache.xerces.xni.XMLAttributes;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;

/**
 * Responseble for transforming a chunk of html.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface Html2WikiTransformer
{
  /**
   * Test if tag is matching
   * 
   * @param tagName name of element
   * @param attributes
   * @param withBody if true, html element has a body
   * @return
   */
  public boolean match(String tagName, XMLAttributes attributes, boolean withBody);

  /**
   * Parses a html begin to a macrofragment
   * 
   * @param tagName raw tagname
   * @param attributes
   * @param withBody if true, html element has a body
   * @return
   */
  public GWikiMacroFragment handleMacroTransformer(String tagName, XMLAttributes attributes, boolean withBody);

  /**
   * Will be called at the end of html element tag.
   * 
   * @param tagname raw html tag
   * @param lpfm the macro fragment
   * @param children child fragments
   * @param body body as text.
   */
  public void handleMacroEnd(String tagname, GWikiMacroFragment lpfm, List<GWikiFragment> children, String body);
}
