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
package de.micromata.genome.gwiki.plugin.vfolder_1_0;

import java.io.Serializable;
import java.util.List;

import de.micromata.genome.gdbfs.FileSystem;

/**
 * Configuration Bean for a VFolder Node
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiVFolderNode implements Serializable
{
  /**
   * Matcher role to find on file system.
   */
  private String matcherRule;

  private FileSystem fileSystem;

  /**
   * Has children
   */
  private List<GWikiVFolderNode> childs;

  /**
   * extract the the body
   */
  private boolean extractBody = true;

  /**
   * Only valid, if extractBody is false. Show pages in iframe
   */
  private boolean fullIframe = false;

  /**
   * A list of gwiki css file should be added inside the html.
   * 
   * Only valid if extractBody = true.
   */
  private List<String> addCss;

  public String getMatcherRule()
  {
    return matcherRule;
  }

  public void setMatcherRule(String matcherRule)
  {
    this.matcherRule = matcherRule;
  }

  public List<GWikiVFolderNode> getChilds()
  {
    return childs;
  }

  public void setChilds(List<GWikiVFolderNode> childs)
  {
    this.childs = childs;
  }

  public boolean isExtractBody()
  {
    return extractBody;
  }

  public void setExtractBody(boolean extractBody)
  {
    this.extractBody = extractBody;
  }

  public boolean isFullIframe()
  {
    return fullIframe;
  }

  public void setFullIframe(boolean fullIframe)
  {
    this.fullIframe = fullIframe;
  }

  public List<String> getAddCss()
  {
    return addCss;
  }

  public void setAddCss(List<String> addCss)
  {
    this.addCss = addCss;
  }

  public FileSystem getFileSystem()
  {
    return fileSystem;
  }

  public void setFileSystem(FileSystem fileSystem)
  {
    this.fileSystem = fileSystem;
  }

}
