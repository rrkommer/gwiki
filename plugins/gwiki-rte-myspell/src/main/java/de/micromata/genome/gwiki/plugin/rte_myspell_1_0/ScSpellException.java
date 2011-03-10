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
package de.micromata.genome.gwiki.plugin.rte_myspell_1_0;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class ScSpellException extends RuntimeException
{

  private static final long serialVersionUID = 5738200754042625084L;

  public ScSpellException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public ScSpellException(String message)
  {
    super(message);
  }

  public ScSpellException(Throwable cause)
  {
    super(cause);
  }

}