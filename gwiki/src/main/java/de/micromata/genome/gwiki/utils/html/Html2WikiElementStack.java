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

package de.micromata.genome.gwiki.utils.html;

import org.apache.commons.collections15.ArrayStack;

/**
 * Internal class for the Html2WikiFilter.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class Html2WikiElementStack
{
  private ArrayStack<Html2WikiElement> stack = new ArrayStack<Html2WikiElement>();

  public void push(Html2WikiElement el)
  {
    stack.push(el);
  }

  public Html2WikiElement pop()
  {
    return stack.pop();
  }

  public Html2WikiElement peek(int offset)
  {
    return stack.peek(offset);
  }
}
