/*
 Created on 09.01.2008
 */
package de.micromata.genome.util.web;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author roger@micromata.de
 * 
 */
public class MimeUtils
{
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
  }

  public static String getMimeTypeFromFile(String fname)
  {
    if (fname == null)
      return null;
    return mimeTypes.get(StringUtils.substringAfterLast(fname, "."));
  }
}
