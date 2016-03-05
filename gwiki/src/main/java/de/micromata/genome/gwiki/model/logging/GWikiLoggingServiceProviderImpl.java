package de.micromata.genome.gwiki.model.logging;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.logging.LoggingContextService;
import de.micromata.genome.logging.LoggingContextServiceDefaultImpl;
import de.micromata.genome.logging.LoggingServiceManager;
import de.micromata.genome.logging.spi.LoggingServiceProvider;

/**
 * Overwrites via ServiceLoader some specifics for gwiki and logging.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiLoggingServiceProviderImpl implements LoggingServiceProvider
{

  @Override
  public LoggingServiceManager getLoggingServiceManager()
  {
    GWikiLogAttributeType.values();
    GWikiLogCategory.values();

    LoggingContextServiceDefaultImpl logContext = new LoggingContextServiceDefaultImpl()
    {

      @Override
      public String getCurrentUserName()
      {
        GWikiContext wikicontext = GWikiContext.getCurrent();
        if (wikicontext == null) {
          return super.getCurrentUserName();
        }
        return wikicontext.getWikiWeb().getDaoContext().getAuthorization().getCurrentUserName(wikicontext);
      }

    };

    return new LoggingServiceManager()
    {

      @Override
      public LoggingContextService getLoggingContextService()
      {
        return logContext;
      }

    };
  }

}
