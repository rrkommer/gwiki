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

package com.googlecode.sardine;

import java.util.Date;
import java.util.Map;

import com.googlecode.sardine.util.SardineUtil;

/**
 * Describes a resource on a remote server. This could be a directory or an actual file.
 *
 * @author jonstevens
 */
public class DavResource
{

  /**
   * The base url.
   */
  private String baseUrl;

  /**
   * The name.
   */
  private String name;

  /**
   * The creation.
   */
  private Date creation;

  /**
   * The modified.
   */
  private Date modified;

  /**
   * The content type.
   */
  private String contentType;

  /**
   * The content length.
   */
  private Long contentLength;

  /**
   * The current directory.
   */
  private boolean currentDirectory;

  /**
   * The custom props.
   */
  private Map<String, String> customProps;

  /**
   * The url.
   */
  private String url;

  /**
   * The name decoded.
   */
  private String nameDecoded;

  /**
   * Represents a webdav response block.
   *
   * @param baseUrl the base url
   * @param name the name of the resource, with all /'s removed
   * @param creation the creation
   * @param modified the modified
   * @param contentType the content type
   * @param contentLength the content length
   * @param currentDirectory the current directory
   * @param customProps the custom props
   */
  public DavResource(String baseUrl, String name, Date creation, Date modified, String contentType, Long contentLength,
      boolean currentDirectory, Map<String, String> customProps)
  {
    this.baseUrl = baseUrl;
    this.name = name;
    this.creation = creation;
    this.modified = modified;
    this.contentType = contentType;
    this.contentLength = contentLength;
    this.currentDirectory = currentDirectory;
    this.customProps = customProps;
  }

  /** */
  public String getBaseUrl()
  {
    return this.baseUrl;
  }

  /**
   * A URLEncoded version of the name as returned by the server.
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * A URLDecoded version of the name.
   */
  public String getNameDecoded()
  {
    if (this.nameDecoded == null) {
      this.nameDecoded = SardineUtil.decode(this.name);
    }
    return this.nameDecoded;
  }

  /** */
  public Date getCreation()
  {
    return this.creation;
  }

  /** */
  public Date getModified()
  {
    return this.modified;
  }

  /** */
  public String getContentType()
  {
    return this.contentType;
  }

  /** */
  public Long getContentLength()
  {
    return this.contentLength;
  }

  /**
   * Absolute url to the resource.
   */
  public String getAbsoluteUrl()
  {
    if (this.url == null) {
      String result = null;
      if (this.baseUrl.endsWith("/")) {
        result = this.baseUrl + this.name;
      } else {
        result = this.baseUrl + "/" + this.name;
      }

      if (this.contentType != null && this.isDirectory() && this.name != null && this.name.length() > 0) {
        result = result + "/";
      }

      this.url = result;
    }
    return this.url;
  }

  /**
   * Does this resource have a contentType of httpd/unix-directory?
   */
  public boolean isDirectory()
  {
    return (this.contentType != null && this.contentType.equals("httpd/unix-directory"));
  }

  /**
   * Is this the current directory for the path we requested? ie: if we requested: http://foo.com/bar/dir/, is this the
   * DavResource for that directory?
   */
  public boolean isCurrentDirectory()
  {
    return this.currentDirectory;
  }

  /** */
  public Map<String, String> getCustomProps()
  {
    return this.customProps;
  }

  /** */
  @Override
  public String toString()
  {
    return "DavResource [baseUrl=" + this.baseUrl + ", contentLength=" + this.contentLength + ", contentType="
        + this.contentType + ", creation=" + this.creation + ", modified=" + this.modified + ", name="
        + this.name + ", nameDecoded=" + this.getNameDecoded() + ", getAbsoluteUrl()="
        + this.getAbsoluteUrl() + ", isDirectory()=" + this.isDirectory() + "]";
  }
}
