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
package de.micromata.genome.gwiki.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class InternalizedHashMap<K, V> extends HashMap<K, V>
{

  private static final long serialVersionUID = 7005897132373604174L;

  private Internalizator<K> keyInternatizator;

  private Internalizator<V> valueInternatizator;

  public InternalizedHashMap()
  {

  }

  public InternalizedHashMap(Internalizator<K> keyInternatizator, Internalizator<V> valueInternatizator)
  {
    this.keyInternatizator = keyInternatizator;
    this.valueInternatizator = valueInternatizator;
  }

  public InternalizedHashMap(Internalizator<K> keyInternatizator, Internalizator<V> valueInternatizator, int initialCapacity,
      float loadFactor)
  {
    super(initialCapacity, loadFactor);
    this.keyInternatizator = keyInternatizator;
    this.valueInternatizator = valueInternatizator;
  }

  public InternalizedHashMap(Internalizator<K> keyInternatizator, Internalizator<V> valueInternatizator, int initialCapacity)
  {
    super(initialCapacity);
    this.keyInternatizator = keyInternatizator;
    this.valueInternatizator = valueInternatizator;
  }

  public InternalizedHashMap(Internalizator<K> keyInternatizator, Internalizator<V> valueInternatizator, Map< ? extends K, ? extends V> m)
  {
    super(m);
    this.keyInternatizator = keyInternatizator;
    this.valueInternatizator = valueInternatizator;
  }

  @Override
  public V put(K key, V value)
  {
    key = keyInternatizator.internalize(key);
    value = valueInternatizator.internalize(value);
    return super.put(key, value);
  }

  public Internalizator<K> getKeyInternatizator()
  {
    return keyInternatizator;
  }

  public void setKeyInternatizator(Internalizator<K> keyInternatizator)
  {
    this.keyInternatizator = keyInternatizator;
  }

  public Internalizator<V> getValueInternatizator()
  {
    return valueInternatizator;
  }

  public void setValueInternatizator(Internalizator<V> valueInternatizator)
  {
    this.valueInternatizator = valueInternatizator;
  }

}
