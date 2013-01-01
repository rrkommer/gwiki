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

////////////////////////////////////////////////////////////////////////////

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

////////////////////////////////////////////////////////////////////////////

package de.micromata.genome.gwiki.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.commons.collections15.iterators.IteratorEnumeration;

/**
 * Simulation of a HttpSession.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@SuppressWarnings("deprecation")
public class StandaloneHttpSession implements HttpSession
{
  private Map<String, Object> sessionAttributes = new HashMap<String, Object>();

  private long created = System.currentTimeMillis();

  private ServletContext servletContext;

  public StandaloneHttpSession()
  {

  }

  public StandaloneHttpSession(ServletContext servletContext)
  {
    this.servletContext = servletContext;
  }

  public Object getAttribute(String key)
  {
    return sessionAttributes.get(key);
  }

  @SuppressWarnings({ "unchecked", "rawtypes"})
  public Enumeration getAttributeNames()
  {
    return new IteratorEnumeration(sessionAttributes.keySet().iterator());
  }

  public long getCreationTime()
  {
    return created;
  }

  public String getId()
  {
    return "SimSession";
  }

  public long getLastAccessedTime()
  {
    return created;
  }

  public int getMaxInactiveInterval()
  {
    return 100000;
  }

  public ServletContext getServletContext()
  {
    return servletContext;
  }

  public HttpSessionContext getSessionContext()
  {
    return null;
  }

  public Object getValue(String key)
  {
    return getAttribute(key);
  }

  public String[] getValueNames()
  {

    return null;
  }

  public void invalidate()
  {
    sessionAttributes.clear();
  }

  public boolean isNew()
  {
    return false;
  }

  public void putValue(String arg0, Object arg1)
  {
    setAttribute(arg0, arg1);

  }

  public void removeAttribute(String arg0)
  {
    sessionAttributes.remove(arg0);

  }

  public void removeValue(String arg0)
  {
    removeAttribute(arg0);
  }

  public void setAttribute(String key, Object value)
  {
    sessionAttributes.put(key, value);

  }

  public void setMaxInactiveInterval(int arg0)
  {

  }

}
