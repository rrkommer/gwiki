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

import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * Action to save user preference.
 * 
 * This Action is only used by ajax calls.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiUserPrefAsyncActionBean extends ActionBeanBase
{
  /**
   * key of the user pereference.
   */
  private String key;

  /**
   * Value to save.
   */
  private String value;

  /**
   * Should the value persists
   */
  private boolean persist = false;

  public Object onInit()
  {
    return noForward();
  }

  public Object onSave()
  {
    // TODO check security.
    wikiContext.getWikiWeb().getAuthorization().setUserProp(wikiContext, key, value, persist);
    return noForward();
  }

  public String getKey()
  {
    return key;
  }

  public void setKey(String key)
  {
    this.key = key;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public boolean isPersist()
  {
    return persist;
  }

  public void setPersist(boolean persist)
  {
    this.persist = persist;
  }
}
