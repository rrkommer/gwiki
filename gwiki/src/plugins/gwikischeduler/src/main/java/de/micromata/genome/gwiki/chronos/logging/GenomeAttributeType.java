/*
 Created on 08.01.2008
 */
package de.micromata.genome.gwiki.chronos.logging;

/**
 * Logging Attributes used inside Genome
 * 
 * @author roger@micromata.de
 * 
 */
public enum GenomeAttributeType implements LogAttributeType
{
  // RequestUrl(new RequestUrlDefaultFiller()), //
  // HttpSessionId("HTTPSESSIONID", 120, new HttpSessionIdFiller(), new SearchKeyLogAttributeRenderer()), //
  // NodeName("NODENAME", 32, new HostNameDefaultFiller(), new SearchKeyLogAttributeRenderer()), //
  ThreadContext, //
  TechReasonException, //
  Stacktrace, //
  XMLDump, //
  HttpClientDump, //
  // SqlStatement(new CurrentSqlFiller()), //
  SqlArgs, //
  SqlResolvedStatement, //
  Miscellaneous, //
  Miscellaneous2, //
  TimependingTable, //
  // ConfigTime(new ConfigTimeFiller()), //
  RequestParams, //
  OutRequestUrl, //
  /**
   * A complete dump of a request
   */
  HttpRequestDump, //
  /**
   * A complete Dump of a response
   */
  HttpResponseDump, //

  TimeInMs, //
  ResourceId, //
  ConfigDomain, //
  ResourceName, //
  ResVersionId, //
  PluginName, //
  ClassName, //
  JobEvent, //
  Purpose, //
  // AdminUserName("USERNAME", 50, new AdminUserDefaultFiller(), new SearchKeyLogAttributeRenderer()), //
  I18NKey, //
  HttpStatus, //
  BackTrace, //
  PerfType, //
  PerfAvgTime, //
  PerfMaxTime, //
  PerfMinTime, //
  PerfSampleCount, //
  GenomeJobId, //
  /**
   * URL of the outgoing communication
   */
  OutHttpUrl, //
  /**
   * Name of the call of the outgpoing communication
   */
  OutHttpCall, //

  EmailMessage, //
  UserEmail, //
  FtpTerminalLog, //
  FlowLog, //
  FileName, //
  UserAgent, //
  RootLogCategory, //
  RootLogMessage, //
  Pattern, OrgUnitName, //
  MessageStackTrace, //

  /*
   * Exit Status eines Prozesses
   */
  ExitStatus, //
  ExpetectedExitStatus, //
  Stderr, // Error
  Stdout, // Out
  ; //

  private String columnName;

  private int maxSize = -1;

  // private AttributeTypeDefaultFiller attributeDefaultFiller;

  // private LogAttributeRenderer renderer = null;

  private GenomeAttributeType()
  {

  }

  private GenomeAttributeType(String columnName, int maxSize)
  {
    this.columnName = columnName;
    this.maxSize = maxSize;
  }

  public String columnName()
  {
    return columnName;
  }

  public boolean isSearchKey()
  {
    return columnName != null;
  }

  public int maxValueSize()
  {
    return maxSize;
  }

}
