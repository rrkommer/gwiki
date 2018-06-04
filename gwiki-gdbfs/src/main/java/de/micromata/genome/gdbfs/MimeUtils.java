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

/*
 Created on 09.01.2008
 */
package de.micromata.genome.gdbfs;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Hold mime mapping.
 * 
 * @author roger@micromata.de
 * 
 */
public class MimeUtils
{

  /**
   * The Constant mimeTypes.
   */
  private static final Map<String, String> mimeTypes = new HashMap<String, String>();

  static {
    mimeTypes.put("gsp", "text/plain");
    mimeTypes.put("gspt", "text/plain");
    mimeTypes.put("groovy", "text/plain");
    mimeTypes.put("css", "text/css");
    mimeTypes.put("htm", "text/html");
    mimeTypes.put("html", "text/html");
    mimeTypes.put("js", "application/javascript");
    mimeTypes.put("png", "image/png");
    mimeTypes.put("jpeg", "image/jpeg");
    mimeTypes.put("jpg", "image/jpeg");
    mimeTypes.put("gif", "image/gif");
    mimeTypes.put("pdf", "application/pdf");
    mimeTypes.put("txt", "text/plain");
    mimeTypes.put("xml", "text/xml");
    mimeTypes.put("class", "application/java-vm");
    mimeTypes.put("java", "text/x-java-source");
    mimeTypes.put("jar", "application/java-archive");
    mimeTypes.put("xls", "application/vnd.ms-excel");
    mimeTypes.put("swf", "application/x-shockwave-flash");
    mimeTypes.put("ico", "image/x-icon");
    mimeTypes.put("zip", "application/zip");
    mimeTypes.put("tgz", "application/x-gtar");
    mimeTypes.put("doc", "application/msword");
    mimeTypes.put("xls", "application/vnd.ms-excel");
    mimeTypes.put("ppt", "application/vnd.ms-powerpoint");
    mimeTypes.put("pps", "application/vnd.ms-powerpoint");
  }

  /**
   * Gets the mime type from file.
   *
   * @param fname the fname
   * @return the mime type from file
   */
  public static String getMimeTypeFromFile(String fname)
  {
    if (fname == null) {
      return null;
    }
    return mimeTypes.get(StringUtils.substringAfterLast(fname, "."));
  }
}
