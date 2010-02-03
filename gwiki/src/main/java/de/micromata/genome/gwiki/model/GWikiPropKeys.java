/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   20.10.2009
// Copyright Micromata 20.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

public interface GWikiPropKeys
{
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

  public static final String MACRO_WITH_BODY = "MACRO_WITH_BODY";

  public static final String MACRO_EVAL_BODY = "MACRO_EVAL_BODY";

  public static final String MACRO_WITH_PREVIEW = "MACRO_WITH_PREVIEW";

  public static final String MACRO_RENDER_MODES = "MACRO_RENDER_MODES";

  /**
   * List of pageIds with the I18N-Modudes used by the page.
   */
  public static final String I18NMODULES = "I18NMODULES";
}
