package de.micromata.genome.gdbfs.jpa;

import javax.persistence.EntityManager;

import de.micromata.genome.jpa.DefaultEmgr;
import de.micromata.genome.jpa.EmgrFactory;
import de.micromata.genome.jpa.EmgrTx;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GwikiEmgrFactory extends EmgrFactory<DefaultEmgr>
{
  private static GwikiEmgrFactory INSTANCE;

  /**
   * Gets the.
   *
   * @return the jpa genome core ent mgr factory
   */
  public static synchronized GwikiEmgrFactory get()
  {
    if (INSTANCE != null) {
      return INSTANCE;
    }
    INSTANCE = new GwikiEmgrFactory();
    return INSTANCE;
  }

  public GwikiEmgrFactory()
  {
    super("de.micromata.genome.gdbfs.jpa");
  }

  @Override
  protected DefaultEmgr createEmgr(EntityManager entityManager, EmgrTx<DefaultEmgr> emgrTx)
  {
    return new DefaultEmgr(entityManager, this, emgrTx);
  }

}
