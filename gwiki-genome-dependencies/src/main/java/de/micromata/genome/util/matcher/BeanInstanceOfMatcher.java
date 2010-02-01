package de.micromata.genome.util.matcher;

/**
 * Matches if given Object is instanceof class
 * 
 * @author roger@micromata.de
 * 
 */
public class BeanInstanceOfMatcher extends MatcherBase<Object>
{

  private static final long serialVersionUID = 7753923065375842215L;

  private Class< ? > ofClass;

  public BeanInstanceOfMatcher()
  {

  }

  public BeanInstanceOfMatcher(Class< ? > ofClass)
  {
    this.ofClass = ofClass;
  }

  public boolean match(Object object)
  {
    if (object == null)
      return false;
    return ofClass.isAssignableFrom(object.getClass());
  }

  public Class< ? > getOfClass()
  {
    return ofClass;
  }

  public void setOfClass(Class< ? > ofClass)
  {
    this.ofClass = ofClass;
  }
}
