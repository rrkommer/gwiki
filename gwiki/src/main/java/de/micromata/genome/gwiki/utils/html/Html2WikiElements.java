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

package de.micromata.genome.gwiki.utils.html;

/**
 * Enumeration used by the html 2 wiki filter
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public enum Html2WikiElements implements Html2WikiElement
{
  Li("- ", "\n"), //
  LiStar("* ", "\n"), //
  LiNum("# ", "\n"), //
  B("*", "*"), //
  ;
  private String start;

  private String end;

  private Html2WikiElements(String start, String end)
  {
    this.start = start;
    this.end = end;
  }

  public String getEnd()
  {
    return end;
  }

  public String getStart()
  {
    return start;
  }

}
