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

package de.micromata.genome.gwiki.page.gspt;

import java.util.Map;

/**
 * Implements a replacer to parse gspts.
 */
public interface Replacer
{
  /**
   * @param attr Attribute
   * @param isClosed wenn end == ">" && isClosed == true, dann "/>"
   */
  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed);

  public String getStart();

  public String getEnd();

}
