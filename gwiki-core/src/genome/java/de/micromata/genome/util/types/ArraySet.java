package de.micromata.genome.util.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Add items to a ArrayList, but discarges duplicated elements.
 * 
 * @author roger
 * 
 * @param <T>
 */
public class ArraySet<T> extends ArrayList<T> implements Set<T>
{

  private static final long serialVersionUID = -1762503806954764180L;

  public ArraySet()
  {
    super();
    // TODO Auto-generated constructor stub
  }

  public ArraySet(Collection< ? extends T> c)
  {
    super(c.size());
    for (T e : c) {
      add(e);
    }
  }

  public ArraySet(int initialCapacity)
  {
    super(initialCapacity);
  }

  @Override
  public boolean add(T e)
  {
    if (contains(e) == true) {
      return false;
    }
    return super.add(e);
  }

}
