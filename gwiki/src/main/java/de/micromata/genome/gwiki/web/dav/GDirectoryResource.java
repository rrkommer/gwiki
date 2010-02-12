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

package de.micromata.genome.gwiki.web.dav;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.CopyableResource;
import com.bradmcevoy.http.DeletableResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.MakeCollectionableResource;
import com.bradmcevoy.http.MoveableResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.PutableResource;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.XmlWriter;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gdbfs.FsDirectoryObject;
import de.micromata.genome.gdbfs.FsObject;

/**
 * WebDav service implementation.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GDirectoryResource extends GFsResource implements MakeCollectionableResource, PutableResource, CopyableResource,
    DeletableResource, MoveableResource, PropFindableResource, GetableResource
{
  private FsDirectoryObject dirObject;

  public GDirectoryResource(FsDavResourceFactory resourceFactory, FsDirectoryObject dirObject)
  {
    super(resourceFactory, dirObject);
    this.dirObject = dirObject;
  }

  public void delete()
  {
    dirObject.getFileSystem().delete(dirObject.getName());
  }

  public CollectionResource createCollection(String newName) throws NotAuthorizedException, ConflictException
  {
    FsDirectoryObject newDir = dirObject.mkdir(newName);
    if (newDir == null) {
      throw new ConflictException(this);
    }
    return new GDirectoryResource(resourceFactory, newDir);
  }

  public void copyTo(CollectionResource toCollection, String name)
  {
    // TODO Auto-generated method stub

  }

  public void moveTo(CollectionResource rDest, String name) throws ConflictException
  {
    // TODO Auto-generated method stub

  }

  public Long getContentLength()
  {
    return null;
  }

  public String getContentType(String accepts)
  {
    return "text/html";
  }

  public Long getMaxAgeSeconds(Auth auth)
  {
    return null;
  }

  public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException,
      NotAuthorizedException
  {

    StringBuilder sb = new StringBuilder();
    XmlWriter w = new XmlWriter(out);
    sb.append("<html>\n<body>\n<h1>").append(StringEscapeUtils.escapeHtml(this.getName())).append("</h1>\n");
    sb.append("<table border='1'>\n");
    for (FsObject r : dirObject.getChilds(null)) {
      sb.append("<tr><td>") //
          .append("<a href='").append(resourceFactory.getReqPrefix()).append(r.getName()).append("'>") //
          .append(StringEscapeUtils.escapeHtml(r.getNamePart())).append("</a></td>") //
          .append("<td>").append(StringEscapeUtils.escapeHtml(ObjectUtils.toString(r.getModifiedAt()))).append("</td>") //
          .append("</tr>\n");
      ;
    }
    sb.append("</table>\n</body>\n</html>");
    out.write(sb.toString().getBytes()); // TODO gwiki encoding.

  }

  public Resource createNew(String newName, InputStream inputStream, Long length, String contentType) throws IOException
  {
    String localName = FileNameUtils.join(dirObject.getName(), newName);
    // if (getFileSystem().exists(localName) == true) {
    // throw new IOException("File already exists: " + newName);
    // }
    getFileSystem().writeFile(localName, inputStream, true);
    FsObject fsnew = getFileSystem().getFileObject(localName);
    Resource res = resourceFactory.convertRes(resourceFactory, fsnew);
    return res;
  }

  public Resource child(String childName)
  {
    return resourceFactory.convertRes(resourceFactory, dirObject.getChild(childName));
  }

  public List< ? extends Resource> getChildren()
  {
    List<FsObject> fsl = dirObject.getChilds(null);
    List<Resource> res = new ArrayList<Resource>();
    for (FsObject fs : fsl) {
      res.add(resourceFactory.convertRes(resourceFactory, fs));
    }
    return res;
  }
}
