/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   03.11.2009
// Copyright Micromata 03.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Exception thrown if authorization failed.
 * 
 * @author roger@micromata.de
 * 
 */
public class AuthorizationFailedException extends RuntimeException
{

  private static final long serialVersionUID = -154857471880913789L;

  /**
   * The missing right.
   */
  private String right;

  private GWikiElementInfo elementInfo;

  public AuthorizationFailedException()
  {
  }

  public AuthorizationFailedException(String message)
  {
    super(message);
  }

  public AuthorizationFailedException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public AuthorizationFailedException(String message, String right)
  {
    super(message);
    this.right = right;
  }

  public AuthorizationFailedException(String message, String right, GWikiElementInfo elementInfo)
  {
    super(message);
    this.right = right;
    this.elementInfo = elementInfo;
  }

  public AuthorizationFailedException(Throwable cause)
  {
    super(cause);
  }

  public static void failRight(GWikiContext ctx, String right)
  {
    throw new AuthorizationFailedException("Authorization failed for: " + right, right);
  }

  public static void failRight(GWikiContext ctx, String right, GWikiElementInfo ei)
  {
    throw new AuthorizationFailedException("Authorization failed for: " + right, right, ei);
  }

  public String getRight()
  {
    return right;
  }

  public void setRight(String right)
  {
    this.right = right;
  }

  public GWikiElementInfo getElementInfo()
  {
    return elementInfo;
  }

  public void setElementInfo(GWikiElementInfo elementInfo)
  {
    this.elementInfo = elementInfo;
  }
}
