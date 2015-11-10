import de.micromata.genome.gwiki.page.impl.actionbean.*;
import de.micromata.genome.gwiki.model.*;
import de.micromata.genome.gwiki.auth.*;
import javax.servlet.http.Cookie;

class WikiControlActionBean extends ActionBeanBase
{
  public String pageId;
  
  private String authRightsRule;

  //private GWikiSimpleUser singleUser = new GWikiSimpleUser();
  
  private String cacheSizes;
  
  private void initialize()
  {
    //singleUser = new GWikiSimpleUser(GWikiSimpleUserAuthorization.getSingleUser(wikiContext));
    //authRightsRule = singleUser.getRightsMatcherRule();
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
      wikiContext.addSimpleValidationError("no PageID set");
      return null;
     }
    wikiContext.getWikiWeb().reloadPage(pageId);
     return null;
  }
  public Object onClearPageCache()
  {  
   
    wikiContext.getWikiWeb().getPageCache().clearCachedPages();
     return null;
  }
  public Object onRebuildIndex()
  {
    wikiContext.wikiWeb.rebuildIndex();
    return null;
  }
  public Object onRebuildIndexFull()
  {
    wikiContext.wikiWeb.rebuildIndex(true);
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
  /*
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
  }*/
  public Object onClearCookies()
  {
     Cookie[] cookies = wikiContext.getRequest().getCookies();
     int cookieLenght = cookies.length;
      
      for (Cookie cookie in cookies) {
      if (cookie.getName().equals("JSESSIONID") == true) {
        continue;
      }
        wikiContext.addSimpleValidationError("Clear cookie: " + cookie.getName() + "=" + cookie.getValue() + ";" + cookie.getPath());
        cookie.setMaxAge(0);
        cookie.setPath("/");
        //cookie.setDomain(request.getHeader("host"));
        wikiContext.getResponse().addCookie(cookie);
       } 
  }  
  public Object onCalcCacheSizes()
  {
    StringBuilder sb =new StringBuilder();
    sb.append("pageInfoCache: ")
       .append(wikiContext.getWikiWeb().getPageCache().getPageCacheInfo())
    .append("; wikiWeb: " + Integer.toString(de.micromata.genome.util.bean.PrivateBeanUtils.getBeanSize(wikiContext.getWikiWeb())))
    ;
    cacheSizes = sb.toString();
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
  /*
  public GWikiSimpleUser getSingleUser()
  {
    return singleUser;
  }

  public void setSingleUser(GWikiSimpleUser user)
  {
    this.singleUser = user;
  }
*/
  public String getPageId() { return pageId; }
  public void setPageId(String pid) { pageId = pid; }
  public String getRefPageId() { return pageId; }
  public void setRefPageId(String pid) { pageId = pid; }
  public String getCacheSizes() { return cacheSizes; }
  public void setCacheSizes(String c) { cacheSizes = c; }
}
