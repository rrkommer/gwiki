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

/////////////////////////////////////////////////////////////////////////////
//
// Project Genome Core
//
// Author    jensi@micromata.de
// Created   04.08.2009
// Copyright Micromata 04.08.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.types;

import java.util.Comparator;

public class NameComparator implements Comparator<Name>
{
  private static NameComparator instance = new NameComparator();

  public static NameComparator getInstance()
  {
    return instance;
  }

  public int compare(Name o1, Name o2)
  {
    return o1.name().compareTo(o2.name());
  }

}
