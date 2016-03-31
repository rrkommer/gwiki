package de.micromata.genome.gwiki.model.logging;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.logging.AttributeTypeDefaultFiller;
import de.micromata.genome.logging.LogWriteEntry;
import de.micromata.genome.logging.LoggingContext;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiRemoteHostFiller implements AttributeTypeDefaultFiller
{

  @Override
  public String getValue(LogWriteEntry lwe, LoggingContext ctx)
  {
    if (GWikiContext.getCurrent() == null || GWikiContext.getCurrent().getRequest() == null) {
      return null;
    }
    return GWikiContext.getCurrent().getRequest().getRemoteHost();
  }

}
