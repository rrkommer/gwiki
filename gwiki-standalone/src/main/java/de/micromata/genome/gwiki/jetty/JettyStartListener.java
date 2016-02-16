package de.micromata.genome.gwiki.jetty;

/**
 * Callback on start and stop.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public interface JettyStartListener
{
  public static enum StartSucces
  {
    Success, Error, AlreadyRunning, AlreadyStopped, NoConfiguration
  }

  default void started(StartSucces success, Exception exception)
  {

  }

  default void stopped(StartSucces success, Exception exception)
  {

  }
}
