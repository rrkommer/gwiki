package de.micromata.genome.util.matcher;

import org.apache.commons.beanutils.BeanUtils;

import de.micromata.genome.util.matcher.string.SimpleWildcardMatcherFactory;

/**
 * Matches against a property expression
 * 
 * @author roger, lado
 * 
 */
public class BeanPropertiesMatcher extends MatcherBase<Object>
{

  private static final long serialVersionUID = -4422921168954539863L;

  /**
   * property in the bean
   */
  private String property;

  /**
   * must have value
   */
  private String value;

  private Matcher<String> matcher = null;

  public BeanPropertiesMatcher()
  {

  }

  public BeanPropertiesMatcher(String property, String value)
  {
    this.property = property;
    this.value = value;

  }

  public boolean match(Object object)
  {
    try {
      String value = null;
      try {
        value = BeanUtils.getNestedProperty(object, property);
      } catch (NoSuchMethodException nsme) {
        return false;
      }
      initMatcher();
      return matcher.match(value);
    } catch (Exception e) {

      /**
       * @logging
       * @reason Kann das Match-Regel nicht ausweten
       * @action Konfiguration überprüfen
       */
      throw new RuntimeException("Can not execute match rule: " + object + "." + property, e);
    }
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public String getProperty()
  {
    return property;
  }

  public void setProperty(String property)
  {
    this.property = property;
  }

  private void initMatcher()
  {
    if (matcher != null) {
      return;
    }
    SimpleWildcardMatcherFactory<String> simpleMatcherFactory = new SimpleWildcardMatcherFactory<String>();
    matcher = simpleMatcherFactory.createMatcher(value);
  }
}
