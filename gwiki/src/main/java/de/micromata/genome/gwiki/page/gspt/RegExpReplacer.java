//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.page.gspt;

import java.util.regex.Pattern;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class RegExpReplacer extends ReplacerBase
{
  private Pattern startPattern;

  private Pattern endPattern;

  public RegExpReplacer(String startPatternStr, String endPatternStr)
  {
    startPattern = Pattern.compile(startPatternStr, Pattern.MULTILINE + Pattern.DOTALL);
    endPattern = Pattern.compile(endPatternStr, Pattern.MULTILINE + Pattern.DOTALL);
  }

  public Pattern getStartPattern()
  {
    return startPattern;
  }

  public Pattern getEndPattern()
  {
    return endPattern;
  }

  public String getEnd()
  {
    return endPattern.pattern();
  }

  public String getStart()
  {
    return startPattern.pattern();
  }

}
