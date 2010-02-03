import de.micromata.genome.gwiki.page.impl.actionbean.*;
import de.micromata.genome.gwiki.model.*;
import de.micromata.genome.gwiki.auth.*;

class WikiControlActionBean extends ActionBeanBase
{
  public String pageId;
  
  private String authRightsRule;

  private GWikiSimpleUser singleUser = new GWikiSimpleUser();
  
  private void initialize()
  {
    singleUser = new GWikiSimpleUser(GWikiSimpleUserAuthorization.getSingleUser(wikiContext));
    authRightsRule = singleUser.getRightsMatcherRule();
  }

  public Object onInit()
  {
    initialize();
    return null;
  }
  public Object onReloadWiki()
  {  
     wikiContext.getWikiWeb().reloadWeb();
     return null;
  }
  public Object onReloadPage()
  {  
     if (pageId == null)    {
      wikiContext.addSimpleValidationError("keine PageID gesetzt");
      return null;
     }
    wikiContext.getWikiWeb().reloadPage(pageId);
     return null;
  }
  public Object onClearPageCache()
  {  
   
    wikiContext.getWikiWeb().getDaoContext().getPageCache().clearCachedPages();
     return null;
  }
  public Object onRebuildIndex()
  {
  	wikiContext.wikiWeb.rebuildIndex();
  	return null;
  }
  public Object onReIndexPage()
  {
     if (pageId == null)
     {
      wikiContext.addSimpleValidationError("keine PageID gesetzt");
      return null;
     }
     wikiContext.wikiWeb.rebuildIndex(pageId, wikiContext);
     return null;
  }
  public Object onSetRights()
  {
	  GWikiSimpleUserAuthorization auth = new GWikiSimpleUserAuthorization();

    try {
      singleUser.setRightsMatcherRule(authRightsRule);
    } catch (Exception ex) {
      wikiContext.addSimpleValidationError("Fehler in der RightRule: " + ex.getMessage());
      return null;
    }
    auth.setSingleUser(wikiContext, singleUser);
    wikiContext.getWikiWeb().setAuthorization(auth);
    return null;
  }

  public String getAuthRightsRule()
  {
    return authRightsRule;
  }

  public void setAuthRightsRule(String authRightsRule)
  {
    this.authRightsRule = authRightsRule;
  }

  public GWikiSimpleUser getSingleUser()
  {
    return singleUser;
  }

  public void setSingleUser(GWikiSimpleUser user)
  {
    this.singleUser = user;
  }

  public String getPageId() { return pageId; }
  public void setPageId(String pid) { pageId = pid; }
}
