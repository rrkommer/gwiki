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

package de.micromata.genome.util.types;

import java.util.AbstractMap;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;

/**
 * Map implemented on base of a ArrayList.
 * 
 * Insert order is stable. Only use this for a few elements, because key access is always full seek in list.
 * 
 * @author roger
 * 
 * @param <K>
 * @param <V>
 */
public class ArrayMap<K, V> extends AbstractMap<K, V>
{
  private ArraySet<Entry<K, V>> entries = new ArraySet<Entry<K, V>>();

  public boolean containsKey(Object key)
  {
    for (Entry<K, V> me : entries) {
      if (ObjectUtils.equals(key, me.getKey())) {
        return true;
      }
    }
    return false;
  }

  public boolean containsValue(Object value)
  {
    for (Entry<K, V> me : entries) {
      if (ObjectUtils.equals(value, me.getValue())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet()
  {
    return entries;
  }

  @Override
  public V put(K key, V value)
  {
    for (Entry<K, V> me : entries) {
      if (ObjectUtils.equals(me.getKey(), key) == true) {
        V rv = me.getValue();
        me.setValue(value);
        return rv;
      }
    }
    entries.add(new Pair<K, V>(key, value));
    return null;
  }

  public ArraySet<Entry<K, V>> getEntries()
  {
    return entries;
  }

  public void setEntries(ArraySet<Entry<K, V>> entries)
  {
    this.entries = entries;
  }

}
