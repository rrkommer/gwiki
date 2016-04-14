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

package de.micromata.genome.gwiki.page.impl.wiki;

public class GWikiMacroSourceFormattingRule
{
  private String beforeStart = "";

  private String afterStart = "";

  private String beforeEnd = "";

  private String afterEnd = "";

  private boolean trimContontent;

  public GWikiMacroSourceFormattingRule(boolean trimContontent, String beforeStart, String afterStart)
  {
    this.trimContontent = trimContontent;
    this.beforeStart = beforeStart;
    this.afterStart = afterStart;
  }

  public GWikiMacroSourceFormattingRule(boolean trimContontent, String beforeStart, String afterStart, String beforeEnd, String afterEnd)
  {
    this.trimContontent = trimContontent;
    this.beforeStart = beforeStart;
    this.afterStart = afterStart;
    this.beforeEnd = beforeEnd;
    this.afterEnd = afterEnd;
  }

  public String getBeforeStart()
  {
    return beforeStart;
  }

  public void setBeforeStart(String beforeStart)
  {
    this.beforeStart = beforeStart;
  }

  public String getAfterStart()
  {
    return afterStart;
  }

  public void setAfterStart(String afterStart)
  {
    this.afterStart = afterStart;
  }

  public String getBeforeEnd()
  {
    return beforeEnd;
  }

  public void setBeforeEnd(String beforeEnd)
  {
    this.beforeEnd = beforeEnd;
  }

  public String getAfterEnd()
  {
    return afterEnd;
  }

  public void setAfterEnd(String afterEnd)
  {
    this.afterEnd = afterEnd;
  }

  public boolean isTrimContontent()
  {
    return trimContontent;
  }

  public void setTrimContontent(boolean trimContontent)
  {
    this.trimContontent = trimContontent;
  }
}
