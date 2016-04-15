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

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.googlecode.sardine.util.SardineException;

/**
 * The main interface for Sardine operations.
 * 
 * @author jonstevens
 */
public interface Sardine
{
  /**
   * Gets a directory listing.
   */
  public List<DavResource> getResources(String url) throws SardineException;

  /**
   * Add or remove custom properties for a url.
   */
  public void setCustomProps(String url, Map<String, String> addProps, List<String> removeProps)
      throws SardineException;

  /**
   * Uses HttpGet to get an input stream for a url
   */
  public InputStream getInputStream(String url) throws SardineException;

  /**
   * Uses webdav put to send data to a server
   */
  public void put(String url, byte[] data) throws SardineException;

  /**
   * Uses webdav put to send data to a server
   */
  public void put(String url, InputStream dataStream) throws SardineException;

  /**
   * Uses webdav put to send data to a server with a specific content type header
   */
  public void put(String url, byte[] data, String contentType) throws SardineException;

  /**
   * Uses webdav put to send data to a server with a specific content type header
   */
  public void put(String url, InputStream dataStream, String contentType) throws SardineException;

  /**
   * Delete a resource at the specified url
   */
  public void delete(String url) throws SardineException;

  /**
   * Uses webdav to create a directory at the specified url
   */
  public void createDirectory(String url) throws SardineException;

  /**
   * Move a url to from source to destination. Assumes overwrite.
   */
  public void move(String sourceUrl, String destinationUrl) throws SardineException;

  /**
   * Copy a url from source to destination. Assumes overwrite.
   */
  public void copy(String sourceUrl, String destinationUrl) throws SardineException;

  /**
   * Performs a HEAD request to see if a resource exists or not. Anything outside of the 200-299 response code range
   * returns false.
   */
  public boolean exists(String url) throws SardineException;

  /**
   * Enables HTTP GZIP compression. If enabled, requests originating from Sardine will include "gzip" as an
   * "Accept-Encoding" header.
   * <p>
   * If the server also supports gzip compression, it should serve the contents in compressed gzip format and include
   * "gzip" as the Content-Encoding. If the content encoding is present, Sardine will automatically decompress the files
   * upon reception.
   * </p>
   */
  public void enableCompression();

  /**
   * Disables support for HTTP compression.
   * 
   * @see Sardine#enableCompression()
   */
  public void disableCompression();

  /**
   * Checks whether support for compression is enabled or not.
   * 
   * @return <tt>true</tt> if compression is enabled, <tt>false</tt> otherwise.
   */
  public boolean isCompressionEnabled();
}
