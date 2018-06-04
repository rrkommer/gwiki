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

package de.micromata.genome.gwiki.sampleplugin_1_0;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * Sample ActionBean.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SampleActionBean extends ActionBeanBase
{
  private String message;

  private String name;

  public Object onInit()
  {
    return null;
  }

  public Object onSayHello()
  {
    if (StringUtils.isBlank(name) == true) {
      wikiContext.addSimpleValidationError("name is empty");
      return null;
    }
    message = "Hello to " + name;
    return null;
  }

  public String getMessage()
  {
    return message;
  }

  public void setMessage(String message)
  {
    this.message = message;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }
}
