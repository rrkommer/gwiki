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

package de.micromata.genome.gwiki.page.gspt;

import java.util.Iterator;
import java.util.Map;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class TagReplacer extends ReplacerBase
{
  protected Class< ? > tagClass;

  protected String tagName;

  private final boolean evaluateELViaGroovy;

  private final boolean convertAttributeNames;

  protected boolean isEvaluateELViaGroovy()
  {
    return evaluateELViaGroovy;
  }

  public TagReplacer(String tagName, Class< ? > tagClass, final boolean evaluateELViaGroovy)
  {
    this(tagName, tagClass, evaluateELViaGroovy, true);
  }

  public TagReplacer(String tagName, Class< ? > tagClass, final boolean evaluateELViaGroovy, final boolean convertAttributeNames)
  {
    this.tagClass = tagClass;
    this.tagName = tagName;
    this.evaluateELViaGroovy = evaluateELViaGroovy;
    this.convertAttributeNames = convertAttributeNames;
  }

  public String getEnd()
  {
    return ">";
  }

  public String getStart()
  {
    return "<" + tagName;
  }

  private static String transFormToProp(String s)
  {
    int idx = s.indexOf('-');
    if (idx == -1)
      return s;
    s = s.substring(0, idx) + s.substring(idx + 1, idx + 2).toUpperCase() + s.substring(idx + 2);
    return transFormToProp(s);
  }

  public abstract String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed);

  public String attributesToGroovyMap(Map<String, String> attr)
  {
    if (attr.isEmpty() == true)
      return "new java.util.HashMap()";
    StringBuilder sb = new StringBuilder();
    sb.append("[ ");
    boolean isFirst = true;
    for (Map.Entry<String, String> e : attr.entrySet()) {
      if (isFirst == false) {
        sb.append(", ");
      }
      if (convertAttributeNames == true) {
        sb.append(transFormToProp(e.getKey()));
      } else {
        sb.append(e.getKey());
      }
      sb.append(": ");
      if (evaluateELViaGroovy == true) {
        String trimmedValue = e.getValue().trim();
        if ((trimmedValue.startsWith("${") == true) && (trimmedValue.endsWith("}") == true)) {
          sb.append(trimmedValue.substring(2, trimmedValue.length() - 1));
        } else {
          sb.append("\"\"\"").append(escapeQuote(trimmedValue, '\"')).append("\"\"\"");
        }
      } else {
        sb.append("'''").append(escapeQuote(e.getValue(), '\'')).append("'''");
      }
      isFirst = false;
    }
    sb.append("]");
    return sb.toString();
  }

  public String attributesToGroovyArray(Map<String, String> attr)
  {
    if (attr.isEmpty() == true)
      return "[]";
    StringBuilder sb = new StringBuilder();
    sb.append("[ ");
    Iterator<Map.Entry<String, String>> iter = attr.entrySet().iterator();
    appendArrayEntry(sb, iter.next());
    while (iter.hasNext() == true) {
      sb.append(",");
      appendArrayEntry(sb, iter.next());
    }
    sb.append("]");
    return sb.toString();
  }

  protected void appendArrayEntry(StringBuilder sb, Map.Entry<String, String> e)
  {

    if (convertAttributeNames == true) {
      sb.append("\"" + transFormToProp(e.getKey()) + "\"");
    } else {
      sb.append("\"" + e.getKey() + "\"");
    }

    sb.append(", ");
    String trimmedValue = e.getValue().trim();

    if (trimmedValue.startsWith("<%=") && trimmedValue.endsWith("%>") == true) {
      sb.append(trimmedValue.substring(3, trimmedValue.length() - 2));
      return;
    }
    if (evaluateELViaGroovy == false) {
      sb.append("'''").append(escapeQuote(e.getValue(), '\'')).append("'''");
      return;
    }

    if ((trimmedValue.startsWith("${") == true) && (trimmedValue.endsWith("}") == true)) {
      sb.append(trimmedValue.substring(2, trimmedValue.length() - 1));
      return;
    }

    sb.append("\"\"\"").append(escapeQuote(trimmedValue, '\"')).append("\"\"\"");
  }

  protected String escapeQuote(String text, char quoteChar)
  {
    return ExtendedTemplate.escapeQuote(text, quoteChar);
  }
}
