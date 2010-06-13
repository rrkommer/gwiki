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
package de.micromata.genome.gwiki.plugin.forum_1_0;

import de.micromata.genome.gwiki.model.GWikiElementInfo;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiForumPostDescription
{
  private GWikiElementInfo forum;

  private GWikiElementInfo post;

  public GWikiForumPostDescription(GWikiElementInfo forum, GWikiElementInfo post)
  {
    this.forum = forum;
    this.post = post;
  }

  public GWikiElementInfo getForum()
  {
    return forum;
  }

  public void setForum(GWikiElementInfo forum)
  {
    this.forum = forum;
  }

  public GWikiElementInfo getPost()
  {
    return post;
  }

  public void setPost(GWikiElementInfo post)
  {
    this.post = post;
  }
}
