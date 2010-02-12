/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   24.10.2009
// Copyright Micromata 24.10.2009
//
/////////////////////////////////////////////////////////////////////////////
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
 * @author roger
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
