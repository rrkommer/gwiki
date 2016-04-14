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

package de.micromata.genome.gwiki.page.gspt;

import java.util.Map;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class BodyTagEndReplacer extends TagReplacer
{

  public BodyTagEndReplacer(String tagName, Class< ? > tagClass, boolean evaluateELViaGroovy)
  {
    super(tagName, tagClass, evaluateELViaGroovy);
  }

  @Override
  public String getEnd()
  {
    return ">";
  }

  @Override
  public String getStart()
  {
    return "</" + tagName;
  }

  @Override
  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String tagSupport = TagSupport.class.getName();
    StringBuilder sb = new StringBuilder();
    sb.append("<% /* seof ").append(tagName).append(" */\n").append("     if (").append(tagSupport).append(
        ".continueAfterBody(pageContext) == false)\n").append("        break;\n").append("    }\n").append("    ").append(tagSupport)
        .append(".afterBody(this, pageContext);\n").append("  }\n")
        // .append(" out = pageContext.getInternalGroovyOut();\n")
        .append("  if (").append(tagSupport).append(".endTag(this, pageContext) == false)\n").append("    return;\n").append("/* eeof ")
        .append(tagName).append("*/ %>") //
    ;
    return sb.toString();
  }
}
