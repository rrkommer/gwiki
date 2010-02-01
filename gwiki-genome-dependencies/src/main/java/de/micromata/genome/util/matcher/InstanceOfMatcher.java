package de.micromata.genome.util.matcher;


/**
 * 
 * @author roger
 * 
 */
public class InstanceOfMatcher extends MatcherBase<Object>
{

  private static final long serialVersionUID = 1346403525135515211L;

  private Class< ? > clazz;

  public InstanceOfMatcher()
  {

  }

  public InstanceOfMatcher(String clazz)
  {
    try {
      this.clazz = Class.forName(clazz);
    } catch (ClassNotFoundException cne) {
      /**
       * @logging
       * @reason Kann die angegebene Klasse nicht laden
       * @action Konfiguration überprüfen
       */
      throw new RuntimeException("Could not load class: " + clazz + "; " + cne.getMessage(), cne);
    }
  }

  public boolean match(Object object)
  {
    return clazz.isAssignableFrom(object.getClass());
  }

  public String toString()
  {
    return "<EXPR> instanceof " + clazz.getCanonicalName();
  }

  public Class< ? > getClazz()
  {
    return clazz;
  }

  public void setClazz(Class< ? > clazz)
  {
    this.clazz = clazz;
  }

}
