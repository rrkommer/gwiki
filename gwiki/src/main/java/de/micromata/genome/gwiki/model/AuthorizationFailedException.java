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

package de.micromata.genome.gwiki.model;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Exception thrown if authorization failed.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
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
