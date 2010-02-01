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
