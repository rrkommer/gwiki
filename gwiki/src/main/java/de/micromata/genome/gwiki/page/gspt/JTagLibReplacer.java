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

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;

import de.micromata.genome.gwiki.page.gspt.taglibs.TagLibraryInfoImpl;


/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class JTagLibReplacer extends ReplacerBase
{

  protected GsptPreprocessor processor;

  public JTagLibReplacer(GsptPreprocessor processor)
  {
    this.processor = processor;
  }

  public String getEnd()
  {
    return "%>";
  }

  public String getStart()
  {
    return "<%@ taglib";
  }

  protected TagLibraryInfo createTagLibraryInfo(PageContext pageContext, String prefix, String uri)
  {
    return new TagLibraryInfoImpl(pageContext, prefix, uri);
  }

  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String uri = attr.get("uri");
    String prefix = attr.get("prefix");
    PageContext pageContext = processor.getPageContext();
    TagLibraryInfo tli = createTagLibraryInfo(pageContext, prefix, uri);
    for (TagInfo ti : tli.getTags()) {
      if (ti.getTagClassName() == null) {
        continue;
      }
      processor.addTagLib(prefix, ti);
    }
    processor.addPrefixCheck(prefix);
    return "";
  }
}
