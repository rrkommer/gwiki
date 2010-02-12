/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   08.12.2009
// Copyright Micromata 08.12.2009
//
/////////////////////////////////////////////////////////////////////////////
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

  void sendEmail(Map<String, String> mailContext);
}
