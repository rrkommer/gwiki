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

package de.micromata.genome.gwiki.page.gspt;

import java.util.Map;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class BodyTagReplacer extends TagReplacer
{
  public BodyTagReplacer(String tagName, Class< ? > tagClass, boolean evaluateELViaGroovy)
  {
    super(tagName, tagClass, evaluateELViaGroovy);
  }

  @Override
  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String a = attributesToGroovyArray(attr);
    StringBuilder sb = new StringBuilder();
    String tagSupport = TagSupport.class.getName();
    if (isClosed == true) {
      sb.append("<% if (").append(tagSupport).append(".closedBodyTag(this, new ").append(tagClass.getName()).append("(), ").append(a)
          .append(", pageContext) == false) return;\n %>");
    } else {
      sb.append("<%  if (").append(tagSupport).append(".initTag(this, new ").append(tagClass.getName()).append("(), ").append(a).append(
          ", pageContext)) {\n");
      // sb.append(" out = pageContext.getInternalGroovyOut();\n");
      sb.append("      while(true) {\n");
      sb.append("%>");
    }
    // if (isClosed == true)
    // sb.append(new BodyTagEndReplacer(tagName, tagClass, isEvaluateELViaGroovy()).replace(attr, true));
    return sb.toString();
  }
}
