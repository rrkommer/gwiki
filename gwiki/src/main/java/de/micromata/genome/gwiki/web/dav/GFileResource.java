////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

package de.micromata.genome.gwiki.web.dav;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.bradmcevoy.common.ContentTypeUtils;
import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.CopyableResource;
import com.bradmcevoy.http.DeletableResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.MoveableResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;

import de.micromata.genome.gdbfs.FsFileObject;

/**
 * WebDav service implementation.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GFileResource extends GFsResource implements Resource, GetableResource, CopyableResource, DeletableResource, MoveableResource,
    PropFindableResource
{
  private FsFileObject fileObject;

  public GFileResource(FsDavResourceFactory resourceFactory, FsFileObject fileObject)
  {
    super(resourceFactory, fileObject);
    this.fileObject = fileObject;
  }

  public Long getContentLength()
  {
    return (long) fileObject.getLength();
  }

  public String getContentType(String accepts)
  {
    String mime = ContentTypeUtils.findContentTypes(fileObject.getName());
    return ContentTypeUtils.findAcceptableContentType(mime, accepts);
  }

  public Long getMaxAgeSeconds(Auth auth)
  {
    return null;
  }

  public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException,
      NotAuthorizedException
  {
    byte[] data = fileObject.readData();
    out.write(data);
  }

  public void copyTo(CollectionResource toCollection, String name)
  {
    // TODO Auto-generated method stub

  }

  public void moveTo(CollectionResource rDest, String name) throws ConflictException
  {
    // TODO Auto-generated method stub

  }

  public void delete()
  {
    fileObject.delete();

  }

  public FsFileObject getFileObject()
  {
    return fileObject;
  }

  public void setFileObject(FsFileObject fileObject)
  {
    this.fileObject = fileObject;
  }

}
