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

package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import de.micromata.genome.gwiki.utils.WebUtils;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiFragmentText extends GWikiFragmentHtml
{

  private static final long serialVersionUID = 7127807753044155663L;

  private StringBuilder text = new StringBuilder();

  public GWikiFragmentText(String text)
  {
    super((String) null);
    this.text.append(text);
  }

  public GWikiFragmentText(GWikiFragmentText other)
  {
    super(other);
  }

  public void addText(String text)
  {
    this.text.append(text);
    this.html = null;
  }

  @Override
  public Object clone()
  {
    return new GWikiFragmentText(this);
  }

  @Override
  public String getHtml()
  {
    if (html == null) {
      html = WebUtils.escapeHtml(text.toString());
    }
    return html;
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    sb.append(text.toString());
  }

  public StringBuilder getText()
  {
    return text;
  }

}
