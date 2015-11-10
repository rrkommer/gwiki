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

package de.micromata.genome.gwiki.page.impl.wiki.fragment;

public class GWikiFragmentParseError extends GWikiFragmentDecorator
{

  private static final long serialVersionUID = -3118476516172674397L;

  private String text;

  private int lineNo;

  public GWikiFragmentParseError(GWikiFragmentParseError other)
  {
    super(other);
    this.text = other.text;
    this.lineNo = other.lineNo;
  }

  public GWikiFragmentParseError(String text)
  {
    this(text, 0);
  }

  public GWikiFragmentParseError(String text, int lineNo)
  {
    super("<color=\"red\">", "</color>");
    this.text = text;
    this.lineNo = lineNo;
    addChild(new GWikiFragmentText(text));
  }

  public void getSource(StringBuilder sb)
  {
    sb.append(text);
  }

  public String getText()
  {
    return text;
  }

  public void setText(String text)
  {
    this.text = text;
  }

  public int getLineNo()
  {
    return lineNo;
  }

  public void setLineNo(int lineNo)
  {
    this.lineNo = lineNo;
  }

}
