package de.micromata.genome.gwiki.launcher;

import org.apache.log4j.Logger;

import de.micromata.genome.gwiki.jetty.GWikiJettyStarter;
import de.micromata.genome.gwiki.jetty.JettyStartListener;
import de.micromata.genome.gwiki.jetty.JettyStartListener.StartSucces;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiLauncherServer
{
  private static final Logger LOG = Logger.getLogger(GWikiLauncherServer.class);
  GWikiJettyStarter starter = new GWikiJettyStarter();

  public class JettyThread extends Thread
  {
    JettyStartListener listener;

    public JettyThread(JettyStartListener listener)
    {
      this.listener = listener;

    }

    @Override
    public void run()
    {

      starter.start(listener);

    }

  }

  private JettyThread jettyThread;

  public boolean isRunning()
  {
    return jettyThread != null;
  }

  public void start()
  {
    start(new JettyStartListener()
    {
    });
  }

  public void start(JettyStartListener listener)
  {
    if (jettyThread != null) {
      listener.started(StartSucces.AlreadyRunning, null);
      return;
    }
    jettyThread = new JettyThread(listener);
    jettyThread.start();
  }

  public void stop()
  {
    stop(new JettyStartListener()
    {
    });
  }

  public void stop(JettyStartListener listener)
  {
    if (jettyThread == null) {
      listener.stopped(StartSucces.AlreadyStopped, null);
      return;
    }
    if (jettyThread.isAlive() == false) {
      return;
    }
    try {

      starter.getServer().stop();
      starter.getServer().join();
      jettyThread.join();
      jettyThread = null;
      LOG.info("Server stopped");
      listener.stopped(StartSucces.Success, null);
    } catch (Exception ex) {
      listener.stopped(StartSucces.Error, ex);
      throw new RuntimeException(ex);
    }
  }
}
