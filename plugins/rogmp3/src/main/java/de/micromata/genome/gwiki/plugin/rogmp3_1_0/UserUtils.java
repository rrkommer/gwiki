package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

import de.micromata.genome.gwiki.page.GWikiContext;

public class UserUtils
{
  public static String getCurrentUserName()
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    return ctx.getWikiWeb().getAuthorization().getCurrentUserName(ctx);
  }
}
