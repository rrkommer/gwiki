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

import java.util.Map;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SimpleSimpleTagReplacer extends TagReplacer
{

  public SimpleSimpleTagReplacer(String tagName, Class< ? > tagClass, boolean evaluateELViaGroovy)
  {
    super(tagName, tagClass, evaluateELViaGroovy, true);
  }

  @Override
  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String a = attributesToGroovyArray(attr);
    StringBuilder sb = new StringBuilder();
    sb.append("<%  ").append(TagSupport.class.getName()).append(".initSimpleSimpleTag(new ").append(tagClass.getName()).append("(), ")
        .append(a).append(", pageContext, { \n").append("%>\n");
    if (isClosed == true) {
      sb.append(new SimpleSimpleTagEndReplacer(tagName, tagClass, isEvaluateELViaGroovy()).replace(ctx, attr, true));
    }
    return sb.toString();
  }
}
