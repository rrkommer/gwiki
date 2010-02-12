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

/**
 * Appendable, which does not throw IOExceptions.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class AbstractAppendable implements AppendableI
{

  public AppendableI append(CharSequence csq, int start, int end)
  {
    return append(csq.subSequence(start, end));
  }

  public AppendableI append(char c)
  {
    return append(Character.toString(c));
  }

  public AppendableI append(Object value)
  {
    return append(String.valueOf(value));
  }

  public AppendableI append(Object... values)
  {
    for (Object val : values) {
      append(val);
    }
    return this;
  }

  public AppendableI append(CharSequence s)
  {
    if (s == null) {
      s = "null";
    }
    if (s instanceof String)
      return this.append((String) s);
    if (s instanceof StringBuffer)
      return this.append((StringBuffer) s);
    return this.append(s.toString());
  }
}
