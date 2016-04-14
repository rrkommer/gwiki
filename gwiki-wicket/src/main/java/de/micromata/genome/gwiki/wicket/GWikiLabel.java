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

package de.micromata.genome.gwiki.wicket;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import de.micromata.genome.gwiki.wicket.util.GWikiUtils;

/**
 * A {@link Label} representing either a complete GWiki page or selected parts or chungs of a GWikiPage, depending on the constructor used.<br/>
 * If no page is found a message will be returned, stating that no page was found. The i18n Key to that message is
 * <code>GWikiLabel.Page_does_not_exist</code> .
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiLabel extends Label
{

  private static final long serialVersionUID = -3802667568734694375L;

  /**
   * Creates a {@link Label} with a whole GWiki page as content. Will contain all html markup (html rood, head, body...) if GWiki has not
   * been altered.
   * 
   * @param id component id
   * @param gwikiPageId GWiki pageId to resolve
   */
  public GWikiLabel(String id, final String gwikiPageId)
  {
    this(id, gwikiPageId, null, null);
  }

  /**
   * Creates a {@link Label} with the given part of a GWiki page as content. Will only contain markup used by the part.
   * 
   * @param id component id
   * @param gwikiPageId GWiki pageId to resolve
   * @param wikiPart GWiki part to resolve, for example "MainPage" for the main content of a page
   */
  public GWikiLabel(String id, final String gwikiPageId, final String wikiPart)
  {
    this(id, gwikiPageId, wikiPart, null);
  }

  /**
   * Creates a {@link Label} with the given GWiki chunk as content. A chunk is a named part of a GWiki page. Works only if the GWiki page is
   * of type GWiki.
   * <p>
   * Example on how to create a named chunk on a GWiki page:<br/>
   * <code>
   * {chunk:name=NamedChunk}
   * Content of the chunk.
   * {chunk}
   * </code>
   * </p>
   * 
   * @param id component id
   * @param gwikiPageId GWiki pageId to resolve
   * @param wikiPart GWiki part to resolve, for example "MainPage" for the main content of a page
   * @param wikiChunk name of the chunk to resolve
   */
  public GWikiLabel(String id, final String gwikiPageId, final String wikiPart, final String wikiChunk)
  {
    super(id);

    String pageContent = GWikiUtils.findWikiPage(gwikiPageId, wikiPart, wikiChunk);
    if (pageContent == null) {
      pageContent = getLocalizer().getString("GWikiLabel.Page_does_not_exist", this);
    }

    IModel<String> model = new Model<String>(pageContent);

    setDefaultModel(model);

    setEscapeModelStrings(false);

    setEscapeModelStrings(false);
  }

}
