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

package de.micromata.genome.gwiki.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;

/**
 * TODO gwiki move this to another class.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class WebUtils
{
  private static LookupTranslator BASIC_XML_ESCAPE = new LookupTranslator(EntityArrays.BASIC_ESCAPE);

  public static String encodeUrlParam(String value)
  {
    if (StringUtils.isEmpty(value) == true) {
      return "";
    }

    try {
      return URLEncoder.encode(value, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static String escapeHtml(String value)
  {
    //    return BASIC_XML_ESCAPE.translate(value);
    return StringEscapeUtils.escapeHtml3(value);
  }

  public static String escapeXml(String value)
  {
    return BASIC_XML_ESCAPE.translate(value);
    //    return StringEscapeUtils.escapeXml11(value);
  }

  public static String escapeJavaScript(String value)
  {
    return StringEscapeUtils.escapeEcmaScript(value);
  }
}
