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

package de.micromata.genome.gwiki.plugin.vfolder_1_0;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiXmlConfigArtefakt;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;

/**
 * Configuration Bean for a VFolder Node
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiVFolderNode implements Serializable
{

  private static final long serialVersionUID = 7385172862632187575L;

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
   * 
   * Currently not supported.
   */
  private boolean fullIframe = false;

  /**
   * A Matcher expression to identify HTML pages.
   */
  private String htmlFileNameMatcherPattern;

  /**
   * Catched compiled htmlFileNameMatcherPattern
   */
  private Matcher<String> htmlFileNameMatcher;

  /**
   * Regular expression to extract body. The group(1) will be used.
   */
  private String extractHtmlBodyRePattern;

  /**
   * Catched compiled extractHtmlBodyRePattern
   */
  private Pattern extractHtmlBodyRePatternCompiled;

  private String htmlContentEncoding = "UTF-8";

  /**
   * Serve attachment directly, not with an attachment page.
   */
  private boolean directAttachments = false;

  /**
   * A list of gwiki css file should be added inside the html.
   * 
   * Only valid if extractBody = true.
   */
  private List<String> addCss;

  public static GWikiVFolderNode getVFolderFromElement(GWikiElement el)
  {
    GWikiXmlConfigArtefakt cfa = (GWikiXmlConfigArtefakt) el.getPart("VFolderConfig");
    GWikiVFolderNode fn = (GWikiVFolderNode) cfa.getCompiledObject();
    return fn;
  }

  public boolean isHtmlVFile(String pageId)
  {
    Matcher<String> m = getHtmlFileNameMatcher();
    if (m == null) {
      return false;
    }
    return m.match(pageId);
  }

  public String extractHtmlVFileBody(String content)
  {
    Pattern p = getExtractHtmlBodyRePatternCompiled();
    if (p == null) {
      return content;
    }
    java.util.regex.Matcher m = p.matcher(content);
    if (m.matches() == false || m.groupCount() < 1) {
      return content;
    }
    return m.group(1);
  }

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

  public String getHtmlFileNameMatcherPattern()
  {
    return htmlFileNameMatcherPattern;
  }

  public void setHtmlFileNameMatcherPattern(String htmlFileNameMatcherPattern)
  {
    this.htmlFileNameMatcherPattern = htmlFileNameMatcherPattern;
  }

  public Matcher<String> getHtmlFileNameMatcher()
  {
    if (htmlFileNameMatcher != null) {
      return htmlFileNameMatcher;
    }
    if (StringUtils.isBlank(htmlFileNameMatcherPattern) == true) {
      return htmlFileNameMatcher;
    }
    htmlFileNameMatcher = new BooleanListRulesFactory<String>().createMatcher(htmlFileNameMatcherPattern);
    return htmlFileNameMatcher;
  }

  public void setHtmlFileNameMatcher(Matcher<String> htmlFileNameMatcher)
  {
    this.htmlFileNameMatcher = htmlFileNameMatcher;
  }

  public String getExtractHtmlBodyRePattern()
  {
    return extractHtmlBodyRePattern;
  }

  public void setExtractHtmlBodyRePattern(String extractHtmlBodyRePattern)
  {
    this.extractHtmlBodyRePattern = extractHtmlBodyRePattern;
  }

  public Pattern getExtractHtmlBodyRePatternCompiled()
  {
    if (extractHtmlBodyRePatternCompiled != null) {
      return extractHtmlBodyRePatternCompiled;
    }
    if (StringUtils.isNotBlank(extractHtmlBodyRePattern) == true) {
      extractHtmlBodyRePatternCompiled = Pattern.compile(extractHtmlBodyRePattern, Pattern.DOTALL);
    }
    return extractHtmlBodyRePatternCompiled;
  }

  public void setExtractHtmlBodyRePatternCompiled(Pattern extractHtmlBodyRePatternCompiled)
  {
    this.extractHtmlBodyRePatternCompiled = extractHtmlBodyRePatternCompiled;
  }

  public String getHtmlContentEncoding()
  {
    return htmlContentEncoding;
  }

  public void setHtmlContentEncoding(String htmlContentEncoding)
  {
    this.htmlContentEncoding = htmlContentEncoding;
  }

  public boolean isDirectAttachments()
  {
    return directAttachments;
  }

  public void setDirectAttachments(boolean directAttachments)
  {
    this.directAttachments = directAttachments;
  }

}
