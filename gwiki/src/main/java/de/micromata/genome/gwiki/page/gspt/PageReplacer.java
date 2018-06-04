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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.util.types.Converter;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class PageReplacer extends RegExpReplacer
{
  public PageReplacer()
  {
    super("(.*?)(<%?@\\s*page\\s+)(.*)", "(.*?)([@%]>)(.*)");
  }

  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String contentType = attr.get("contentType");
    String imports = attr.get("import");
    StringBuilder sb = new StringBuilder();
    // TODO test
    if (StringUtils.isNotBlank(contentType) == true && StringUtils.isBlank(imports) == true) {
      sb.append("<% pageContext.getResponse().setContentType(\"" + contentType + "\"); %>");
      // uebernommen
    }

    if (StringUtils.isNotBlank(imports) == true) {
      List<String> imps = Converter.parseStringTokens(imports, ", ", false);
      sb.append("<%# ");
      for (String im : imps) {
        sb.append("  import ").append(im).append(";\n");
      }
      sb.append("%>");
    }
    String isELIgnored = attr.get("isELIgnored");
    if (StringUtils.isNotBlank(isELIgnored) == true) {
      sb.append("<% pageContext.setEvaluateTagAttributes(").append(isELIgnored).append(" == false); %>");
    }
    String useParentTagContext = attr.get("useParentTagContext");
    if (StringUtils.isNotBlank(useParentTagContext) == true) {
      sb.append("<% pageContext.setUseParentTagContext(").append(useParentTagContext).append("); %>");
    }
    return sb.toString();
  }
}
