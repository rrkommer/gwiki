package de.micromata.genome.gwiki.chronos.spi;

/**
 * Internal extended interface to Dispatcher interface
 * 
 * @author roger
 * 
 */
public interface DispatcherInternal extends Dispatcher
{
  /**
   * Create the thread group for dispatcher
   * 
   * @return
   */
  public ThreadGroup getCreateDispatcherThreadGroup();

  /**
   * return the name of the dispatcher
   * 
   * @return
   */
  public String getDispatcherName();

  /**
   * wake up sleeping dispatcher
   */
  public void wakeup();
}
