package de.micromata.genome.util.runtime;

/**
 * 
 * @author roger
 * 
 * @param <V>
 * @param <DECLEX>
 * @param <THROWEX>
 */
public abstract class WrappedExCallableX<V, DECLEX extends Throwable, THROWEX extends Throwable> extends AbtractCallableX<V, THROWEX>
{
  public Class< ? > getDeclaredWrappedException()
  {

    Class< ? > myClass = getClass();
    Class< ? > fcls = GenericsUtils.getConcretTypeParameter(WrappedExCallableX.class, myClass, 1);
    if (fcls != null)
      return fcls;
    // fcls = GenericsUtils.getClassGenericTypeFromSuperClass(myClass, 1, null);
    //
    // if (fcls != null)
    // return fcls;
    return Exception.class;
  }
  // Is not working with 1.5 compiler
  // public V callWrapped() throws DECLEX
  // {
  // try {
  // return call();
  // } catch (Throwable ex) {
  // Class< ? > declaredEx = getDeclaredWrappedException();
  // if (declaredEx.isAssignableFrom(ex.getClass()) == true) {
  // // Java 1.5 compile error:
  // // unreported exception java.lang.Throwable; must be caught or declared to be thrown
  // // 09-Jul-2009 10:44:44 [javac] throw (DECLEX) ex;
  // // 09-Jul-2009 10:44:44 [javac] ^
  // final DECLEX declEx = (DECLEX) ex;
  // throw declEx;
  // }
  // throw wrappException(ex);
  // }
  // }
}
