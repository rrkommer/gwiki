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

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.MatcherBase;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class SaxElementMatchers
{
  public static Matcher<SaxElement> nameMatcher(String name)
  {
    return new MatcherBase<SaxElement>()
    {

      @Override
      public boolean match(SaxElement object)
      {
        return object.getElementName().equals(name);
      }

    };
  }

  public static Matcher<SaxElement> attribute(String name, Matcher<String> attr)
  {
    return new MatcherBase<SaxElement>()
    {

      @Override
      public boolean match(SaxElement object)
      {
        String attrv = object.getAttributes().getValue(name);
        if (attrv == null) {
          return false;
        }
        return attr.match(attrv);
      }

    };
  }

  public static Matcher<SaxElement> withBody(boolean withBody)
  {
    return new MatcherBase<SaxElement>()
    {

      @Override
      public boolean match(SaxElement object)
      {
        return withBody == object.hasBody;
      }
    };
  }
}
