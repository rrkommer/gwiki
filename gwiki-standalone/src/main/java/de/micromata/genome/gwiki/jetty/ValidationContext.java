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

package de.micromata.genome.gwiki.jetty;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class ValidationContext
{
  private List<ValidationMessage> messages = new ArrayList<>();

  public boolean error(String message)
  {
    messages.add(new ValidationMessage(Level.ERROR, "", message));
    return false;
  }

  public boolean error(String property, String message)
  {
    messages.add(new ValidationMessage(Level.ERROR, property, message));
    return false;
  }

  public boolean info(String property, String message)
  {
    messages.add(new ValidationMessage(Level.INFO, property, message));
    return true;
  }

  public boolean info(String message)
  {
    messages.add(new ValidationMessage(Level.INFO, "", message));
    return true;
  }

  public List<ValidationMessage> getMessages()
  {
    return messages;
  }

  public boolean hasMessages()
  {
    return messages.isEmpty() == false;
  }

  public boolean hasErrors()
  {
    return messages.stream().anyMatch(vm -> vm.getLevel().toInt() >= Level.ERROR_INT);
  }

}
