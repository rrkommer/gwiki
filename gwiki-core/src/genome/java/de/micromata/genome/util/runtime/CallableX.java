package de.micromata.genome.util.runtime;

/**
 * Like callable, but also allow to customize Exception to throw
 * 
 * @author roger
 * 
 * @param <V>
 * @param <EX>
 */
public interface CallableX<V, EX extends Throwable>
{
  public V call() throws EX;
}
