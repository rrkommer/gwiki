////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
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

package de.micromata.genome.gwiki.model;

import java.util.Map;

/**
 * Provides a service to send emails.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiEmailProvider
{
  /**
   * Context contains following keys:
   * 
   * TO
   * 
   * FROM
   * 
   * CC
   * 
   * BCC
   * 
   * SUBJECT (max 100 chars) TEXT (max 500 chars)
   * 
   * custom fields.
   * 
   * @param mailContext
   */
  public static final String TO = "TO";

  public static final String FROM = "FROM";

  public static final String CC = "CC";

  public static final String BCC = "BCC";

  public static final String SUBJECT = "SUBJECT";

  public static final String TEXT = "TEXT";

  /**
   * if given, mail will be evaluated by given pageId mailtemplate. All Parameter are passed pageAttributes.
   */
  public static final String MAILTEMPLATE = "MAILTEMPLATE";

  void sendEmail(Map<String, String> mailContext);
}
