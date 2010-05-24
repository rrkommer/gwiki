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
package de.micromata.genome.gwiki.controls;

import org.apache.commons.lang.StringUtils;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiBlogEntryAction extends GWikiBlogBaseActionBean
{
  public boolean init()
  {
    blogPageId = wikiContext.getCurrentElement().getElementInfo().getParentId();
    if (StringUtils.isNotEmpty(blogPageId) == true) {
      blogPage = wikiContext.getWikiWeb().findElementInfo(blogPageId);
    }
    return super.init();
  }

  @Override
  public Object onInit()
  {
    if (init() == false) {
      return null;
    }
    return null;
  }
}
