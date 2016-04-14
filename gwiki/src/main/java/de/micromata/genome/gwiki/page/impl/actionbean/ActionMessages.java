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

package de.micromata.genome.gwiki.page.impl.actionbean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActionMessages extends HashMap<String, List<ActionMessage>>
{

  private static final long serialVersionUID = -5958445295370932524L;

  public void put(String field, ActionMessage msg)
  {
    List<ActionMessage> am = get(field);
    if (am == null) {
      am = new ArrayList<ActionMessage>();
      super.put(field, am);
    }
    am.add(msg);
  }
}
