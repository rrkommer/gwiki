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
