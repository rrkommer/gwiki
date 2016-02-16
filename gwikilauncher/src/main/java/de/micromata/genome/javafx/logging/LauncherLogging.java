package de.micromata.genome.javafx.logging;

import java.sql.Timestamp;
import java.util.List;

import de.micromata.genome.logging.BaseLogging;
import de.micromata.genome.logging.EndOfSearch;
import de.micromata.genome.logging.LogEntryCallback;
import de.micromata.genome.logging.LogWriteEntry;
import de.micromata.genome.util.types.Pair;

public class LauncherLogging extends BaseLogging
{
  @Override
  public void doLogImpl(LogWriteEntry lwe)
  {
    LoggingController lc = LoggingController.getInstance();
    if (lc == null) {
      return;
    }
    lc.doLogImpl(lwe);
  }

  @Override
  public boolean supportsSearch()
  {
    return false;
  }

  @Override
  public boolean supportsFulltextSearch()
  {
    return false;
  }

  @Override
  public String formatLogId(Object logId)
  {
    return "";
  }

  @Override
  public Object parseLogId(String logId)
  {
    return logId;
  }

  @Override
  protected void selectLogsImpl(Timestamp start, Timestamp end, Integer loglevel, String category, String msg,
      List<Pair<String, String>> logAttributes, int startRow, int maxRow, List<OrderBy> orderBy, boolean masterOnly,
      LogEntryCallback callback) throws EndOfSearch
  {

  }

  @Override
  protected void selectLogsImpl(List<Object> logId, boolean masterOnly, LogEntryCallback callback) throws EndOfSearch
  {

  }

}
