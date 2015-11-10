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
// Author    roger@micromata.de
// Created   11.07.2006
// Copyright Micromata 11.07.2006
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.types;

/**
 * @see Pair
 * @author roger@micromata.de
 * 
 */
public class StringObjectPair extends Pair<String, Object>
{
  private static final long serialVersionUID = 8490052926203941134L;

  public StringObjectPair()
  {
    super();
  }

  public StringObjectPair(String key, Object value)
  {
    super(key, value);
  }
}
