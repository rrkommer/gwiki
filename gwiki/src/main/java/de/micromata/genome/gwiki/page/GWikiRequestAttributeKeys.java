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

package de.micromata.genome.gwiki.page;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiRequestAttributeKeys
{
  public static final String welcomePageId = "welcomePageId";

  /**
   * if Boolean.TRUE no children of current page will be shown in navigation.
   */
  public static final String GWIKI_DISABLE_CHILD_NAV = "GWIKI_DISABLE_CHILD_NAV";
}
