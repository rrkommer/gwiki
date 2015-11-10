//////////////////////////////////////////////////////////////////////////////
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

///////////////////////////////////////////////////////////////////////////
//
// Project Genome Core
//
// Author    jensi@micromata.de
// Created   27.02.2009
// Copyright Micromata 27.02.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.matcher.name;

import java.util.ArrayList;
import java.util.Arrays;

import de.micromata.genome.util.matcher.MatcherBase;
import de.micromata.genome.util.types.Name;

/**
 * matches if at least one given name has equal name() value.
 * 
 * @author jens@micromata.de
 */
public class AnyNameMatcher<T extends Name> extends MatcherBase<T>
{
  private static final long serialVersionUID = 5697802074293705812L;

  private Iterable< ? extends Name> validNames;

  public AnyNameMatcher(Name... validNames)
  {
    this(Arrays.asList(validNames));
  }

  public AnyNameMatcher(Iterable< ? extends Name> validNames)
  {
    if (validNames == null) {
      this.validNames = new ArrayList<Name>(0);
    }
    this.validNames = validNames;
  }

  public boolean match(T name)
  {
    for (Name n : validNames) {
      if (n.name().equals(name.name()) == true) {
        return true;
      }
    }
    return false;
  }

  public static <T extends Name> AnyNameMatcher<T> getInstance(T... names)
  {
    return new AnyNameMatcher<T>(names);
  }

}
