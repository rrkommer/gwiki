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

package de.micromata.genome.util.matcher;

import groovy.lang.Binding;
import groovy.lang.Script;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * 
 * @author roger
 * 
 * @param <T>
 */
public class GroovyMatcher<T> extends MatcherBase<T>
{

  private static final long serialVersionUID = 6341664478451114710L;

  private String source;

  private Class< ? > scriptClass;

  public GroovyMatcher()
  {

  }

  public GroovyMatcher(String source, Class< ? > scriptClass)
  {
    this.source = source;
    this.scriptClass = scriptClass;
  }

  @SuppressWarnings({ "unchecked", "rawtypes"})
  public boolean match(T object)
  {

    Map<String, Object> context;
    if (object instanceof Map) {
      context = (Map) object;
    } else {
      context = new HashMap<String, Object>();
      context.put("arg", object);
    }
    Script script = InvokerHelper.createScript(scriptClass, new Binding(context));

    Object ret = script.run();
    return Boolean.TRUE.equals(ret);
  }

  public String getSource()
  {
    return source;
  }

  public void setSource(String source)
  {
    this.source = source;
  }

  public Class< ? > getScriptClass()
  {
    return scriptClass;
  }

  public void setScriptClass(Class< ? > scriptClass)
  {
    this.scriptClass = scriptClass;
  }
}
