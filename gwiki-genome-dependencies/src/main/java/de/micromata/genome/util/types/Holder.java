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
// Created   09.02.2007
// Copyright Micromata 09.02.2007
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.types;

/**
 * Indirection to hold value. May be used where local variable has to be final.
 * 
 * @author roger@micromata.de
 * 
 */
public class Holder<T>
{

  private T holded;

  public Holder()
  {
  }

  public Holder(T t)
  {
    holded = t;
  }

  public T get()
  {
    return holded;
  }

  public void set(T t)
  {
    holded = t;
  }

  public T getHolded()
  {
    return holded;
  }

  public void setHolded(T t)
  {
    holded = t;
  }
}
