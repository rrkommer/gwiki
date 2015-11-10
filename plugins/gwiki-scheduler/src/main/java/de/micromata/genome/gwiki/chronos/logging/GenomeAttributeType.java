////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
////////////////////////////////////////////////////////////////////////////

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
