package de.micromata.genome.util.runtime;

/**
 * 
 * @author roger
 * 
 * @param <V> return value
 * @param <EX> declared exception
 */
public abstract class AbtractCallableX<V, EX extends Throwable> implements CallableX<V, EX>
{

  public Class< ? > getDeclaredException()
  {
    Class< ? > cls = GenericsUtils.getConcretTypeParameter(CallableX.class, this.getClass(), 1);
    if (cls == null)
      return Exception.class;
    return cls;
  }

  public <V, EX extends Throwable> boolean isDeclaredException(CallableX<V, EX> callable, Throwable ex)
  {
    Class< ? > decl = getDeclaredException();
    return decl.isAssignableFrom(ex.getClass()) == true;
  }

  public boolean isDeclaredException(Throwable ex)
  {
    Class< ? > decl = getDeclaredException();
    return decl.isAssignableFrom(ex.getClass()) == true;
  }

  public RuntimeException wrappException(Throwable ex)
  {
    if (ex instanceof RuntimeException)
      return (RuntimeException) ex;
    if (ex instanceof Error)
      throw (Error) ex;
    return new RuntimeException(ex);
  }

  // public V callWrapped() throws EX
  // {
  // try {
  // return call();
  // } catch (Exception ex) {
  // if (isDeclaredException(ex) == true)
  // throw (EX) ex;
  // throw wrappException(ex);
  // }
  // }
}
