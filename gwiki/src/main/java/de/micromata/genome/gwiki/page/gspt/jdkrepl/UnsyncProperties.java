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

package de.micromata.genome.gwiki.page.gspt.jdkrepl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Not synchronized version of Properties
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class UnsyncProperties extends HashMap<String, Object>
{
  /**
   * 
   */
  private static final long serialVersionUID = 5660994201955845607L;

  Map< ? extends String, ? extends Object> defaults;

  public UnsyncProperties()
  {
    super();
  }

  public UnsyncProperties(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
  }

  public UnsyncProperties(int initialCapacity)
  {
    super(initialCapacity);
  }

  public UnsyncProperties(Map< ? extends String, ? extends Object> m)
  {
    super(m);
  }

  @SuppressWarnings("unchecked")
  public UnsyncProperties(Properties props)
  {
    this((Map< ? extends String, ? extends Object>) (Map< ? , ? >) props);
  }

  public String getProperty(String key)
  {
    Object oval = super.get(key);
    String sval = (oval instanceof String) ? (String) oval : null;
    return sval;
  }

  public String getProperty(String key, String defaultValue)
  {
    String val = getProperty(key);
    return (val == null) ? defaultValue : val;
  }

  public Object setProperty(String key, String value)
  {
    return put(key, value);
  }

  public Map< ? extends String, ? extends Object> getDefaults()
  {
    return defaults;
  }

  public void setDefaults(Map< ? extends String, ? extends Object> defaults)
  {
    this.defaults = defaults;
  }
}
