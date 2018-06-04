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

package de.micromata.genome.gwiki.web.dav;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Request.Method;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.umgmt.GWikiUserAuthorization;
import de.micromata.genome.gwiki.web.GWikiServlet;

/**
 * WebDav service implementation.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GFsResource implements PropFindableResource
{
  protected FsDavResourceFactory resourceFactory;

  private FsObject fsObject;

  public GFsResource(FsDavResourceFactory resourceFactory, FsObject fsObject)
  {
    this.fsObject = fsObject;
    this.resourceFactory = resourceFactory;
  }

  public FileSystem getFileSystem()
  {
    return fsObject.getFileSystem();
  }

  public Date getCreateDate()
  {
    return fsObject.getCreatedAt();
  }

  protected GWikiAuthorization getAuthorization(GWikiContext ctx)
  {
    if (ctx == null || ctx.getWikiWeb() == null || ctx.getWikiWeb().getAuthorization() == null) {
      if (GWikiServlet.INSTANCE == null || GWikiServlet.INSTANCE.getDAOContext() == null) {
        return null;
      }
      return GWikiServlet.INSTANCE.getDAOContext().getAuthorization();
    }
    return ctx.getWikiWeb().getAuthorization();
  }

  public Object authenticate(String user, String password)
  {
    if (StringUtils.equals(user, resourceFactory.getInternalUserName()) == true) {
      String encpass = GWikiUserAuthorization.encrypt(password);
      if (StringUtils.equals(encpass, resourceFactory.getInternalPass()) == true) {
        return true;
      }
      return false;
    }
    GWikiContext ctx = GWikiContext.getCurrent();
    GWikiAuthorization auth = getAuthorization(ctx);
    if (auth == null) {
      return null;
    }
    if (auth.login(ctx, user, password) == false) {
      return null;
    }
    return user;
  }

  public boolean authorise(Request request, Method method, Auth auth)
  {

    if (auth == null || StringUtils.isBlank(auth.getUser()) == true) {
      return false;
    }
    if (StringUtils.equals(auth.getUser(), resourceFactory.getInternalUserName()) == true) {
      return true;
    }
    GWikiContext ctx = GWikiContext.getCurrent();
    GWikiAuthorization gauth = getAuthorization(ctx);
    if (gauth == null) {
      return false;
    }
    if (gauth.isAllowTo(ctx, GWikiAuthorizationRights.GWIKI_FSWEBDAV.name()) == false)
      return false;
    return true;
  }

  public String checkRedirect(Request request)
  {
    return null;
  }

  public Date getModifiedDate()
  {
    return fsObject.getModifiedAt();
  }

  public String getName()
  {
    String n = fsObject.getNamePart();
    return n;
  }

  public String getRealm()
  {
    return GWikiAuthorizationRights.GWIKI_FSWEBDAV.name();
  }

  public String getUniqueId()
  {
    return null;
  }

}
