/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   08.11.2009
// Copyright Micromata 08.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.comparators.ReverseComparator;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.model.matcher.GWikiPageIdMatcher;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiDefaultFileNames;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.utils.ThrowableUtils;
import de.micromata.genome.util.matcher.string.SimpleWildcardMatcherFactory;

/**
 * ActionBean dealing with comment actions.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPageCommentMacroActionBean extends ActionBeanBase implements GWikiPropKeys
{
  /**
   * Comments for page
   */
  private String pageId;

  /**
   * Comments currently edited by user.
   */
  private String commentText;

  private List<GWikiElementInfo> commentElements = new ArrayList<GWikiElementInfo>();

  protected void collectComments()
  {
    String pageName = GWikiContext.getNamePartFromPageId(pageId);

    String parentPath = GWikiContext.getParentDirPathFromPageId(pageId);
    String matchRule = parentPath + "comments/" + pageName + "/*";

    List<GWikiElementInfo> l = wikiContext.getElementFinder().getPageInfos(
        new GWikiPageIdMatcher(wikiContext, new SimpleWildcardMatcherFactory<String>().createMatcher(matchRule)));
    Collections.sort(l, new ReverseComparator<GWikiElementInfo>(new GWikiElementByPropComparator(GWikiPropKeys.CREATEDAT)));
    commentElements = l;
  }

  public Object onInit()
  {
    collectComments();
    return null;

  }

  protected GWikiMetaTemplate initMetaTemplate()
  {
    String metaTemplatePageId = GWikiDefaultFileNames.FRAGMENT_METATEMPLATE;
    return wikiContext.getWikiWeb().findMetaTemplate(metaTemplatePageId);
  }

  protected String getViewRightFromParent()
  {
    GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(pageId);
    if (ei == null)
      return null;
    return ei.getProps().getStringValue(AUTH_VIEW);
  }

  protected GWikiElement createNewElement()
  {
    GWikiProps props = new GWikiProps();
    GWikiMetaTemplate metaTemplate = initMetaTemplate();
    props.setStringValue(TYPE, metaTemplate.getElementType());
    props.setStringValue(WIKIMETATEMPLATE, GWikiDefaultFileNames.FRAGMENT_METATEMPLATE);
    props.setStringValue(TITLE, "Comment");
    props.setStringValue(CREATEDBY, wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext));
    props.setDateValue(CREATEDAT, new Date());
    props.setStringValue(MODIFIEDBY, wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext));
    props.setStringValue(AUTH_EDIT, GWikiAuthorizationRights.GWIKI_PRIVATE.name());
    props.setDateValue(MODIFIEDAT, new Date());
    String viewRight = getViewRightFromParent();
    if (viewRight != null) {
      props.setStringValue(AUTH_VIEW, viewRight);
    }

    props.setStringValue(AUTH_EDIT, GWikiAuthorizationRights.GWIKI_PRIVATE.name());
    GWikiElementInfo ei = new GWikiElementInfo(props, wikiContext.getWikiWeb()
        .findMetaTemplate(GWikiDefaultFileNames.FRAGMENT_METATEMPLATE));

    GWikiElement elementToEdit = getWikiContext().getWikiWeb().getStorage().createElement(ei);
    // elementToEdit.setMetaTemplate(metaTemplate);

    return elementToEdit;
  }

  public void renderCommentBody(GWikiElementInfo ei)
  {

    GWikiElement el = wikiContext.getWikiWeb().getElement(ei.getId());
    // Date modat = el.getElementInfo().getModifiedAt();
    // String modBy = el.getElementInfo().getModifiedBy();

    wikiContext.getWikiWeb().serveWiki(wikiContext, el);
  }

  public Object onSaveComment()
  {
    if (StringUtils.isEmpty(commentText) == true) {
      wikiContext.addSimpleValidationError("Keine Nachricht angegeben");
      return onInit();
    }
    if (StringUtils.isEmpty(pageId) == true) {
      wikiContext.addSimpleValidationError("pageId fehlt");
      return onInit();
    }
    String pageName = GWikiContext.getNamePartFromPageId(pageId);
    String parentPath = GWikiContext.getParentDirPathFromPageId(pageId);
    String newPageId = FileNameUtils.join(parentPath, "comments", pageName, GWikiProps.formatTimeStamp(new Date()));
    GWikiElement cel = createNewElement();
    cel.getElementInfo().setId(newPageId);
    Map<String, GWikiArtefakt< ? >> map = new HashMap<String, GWikiArtefakt< ? >>();
    cel.collectParts(map);
    String partName = "MainPage";
    GWikiWikiPageArtefakt wka = (GWikiWikiPageArtefakt) map.get(partName);
    wka.setStorageData(commentText);
    try {
      wka.compileFragements(wikiContext);
    } catch (Exception ex) {
      String st = ThrowableUtils.getExceptionStacktraceForHtml(ex);
      wikiContext.addSimpleValidationError("Kann Wiki Seite nicht kompilieren: " + ex.getMessage() + "\n" + st);
      return onInit();
    }
    getWikiContext().getWikiWeb().saveElement(wikiContext, cel, false);
    commentText = "";
    return onInit();
  }

  public String getCommentText()
  {
    return commentText;
  }

  public void setCommentText(String commentText)
  {
    this.commentText = commentText;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public List<GWikiElementInfo> getCommentElements()
  {
    return commentElements;
  }

  public void setCommentElements(List<GWikiElementInfo> commentElements)
  {
    this.commentElements = commentElements;
  }

}
