////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
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

package de.micromata.genome.gwiki.model;

/**
 * Standard property keys used in *Settings.properties.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiPropKeys
{
  /**
   * Pseudo properties.
   */
  public static final String PAGEID = "PAGEID";

  public static final String TYPE = "TYPE";

  /**
   * size of attachments
   */
  public static final String SIZE = "SIZE";

  public static final String CREATEDAT = "CREATEDAT";

  public static final String CREATEDBY = "CREATEDBY";

  public static final String MODIFIEDAT = "MODIFIEDAT";

  public static final String MODIFIEDBY = "MODIFIEDBY";

  public static final String PARENTPAGE = "PARENTPAGE";

  public static final String TITLE = "TITLE";

  /**
   * Standard-Ordnung bei der Darstellung der Unterseiten.
   */
  public static final String ORDER = "ORDER";

  /**
   * List of child elements for ordering.
   */
  public static final String CHILDORDER = "CHILDORDER";

  public static final String AUTH_VIEW = "AUTH_VIEW";

  public static final String AUTH_EDIT = "AUTH_EDIT";

  public static final String AUTH_CREATE = "AUTH_CREATE";

  public static final String CONTENTYPE = "CONTENTYPE";

  public static final String KEYWORDS = "KEYWORDS";

  public static final String WIKITEMPLATE = "WIKITEMPLATE";

  public static final String WIKIMETATEMPLATE = "WIKIMETATEMPLATE";

  public static final String WIKICONTROLERCLASS = "WIKICONTROLERCLASS";

  public static final String WIKIPAGEID = "WIKIPAGEID";

  public static final String WIKIPAGE = "WIKIPAGE";

  public static final String NOINDEX = "NOINDEX";

  /**
   * To group sites into space.
   */
  public static final String WIKISPACE = "WIKISPACE";

  /**
   * Element kann nicht direkt angezeigt werden.
   */
  public static final String NOVIEW = "NOVIEW";

  public static final String NOTOC = "NOTOC";

  public static final String MACRO_WITH_BODY = "MACRO_WITH_BODY";

  public static final String MACRO_EVAL_BODY = "MACRO_EVAL_BODY";

  public static final String MACRO_WITH_PREVIEW = "MACRO_WITH_PREVIEW";

  public static final String MACRO_RENDERMODES = "MACRO_RENDERMODES";

  /**
   * List of pageIds with the I18N-Modudes used by the page.
   */
  public static final String I18NMODULES = "I18NMODULES";

  /**
   * Language of the page.
   */
  public static final String LANG = "LANG";

  public static final String HELP_PAGE = "HELP_PAGE";

  /**
   * This element is part of another element.
   */
  public static final String PARTOF = "PARTOF";

  /**
   * Skin of the page.
   */
  public static final String SKIN = "SKIN";

  public static final String ICON_MEDIUM = "ICON_MEDIUM";

}
