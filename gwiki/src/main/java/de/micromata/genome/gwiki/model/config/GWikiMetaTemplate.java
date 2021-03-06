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

package de.micromata.genome.gwiki.model.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiPropsDescriptor;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * A meta template defines the internal structure of a GWikiElement.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiMetaTemplate implements Serializable, InitializingBean
{

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 7116843033071027993L;

  /**
   * This page id.
   */
  private String pageId;

  /**
   * Short name of the element type.
   */
  private String elementType;

  /**
   * right to create or/and edit page.
   */
  private String requiredViewRight;

  /**
   * The required edit right.
   */
  private String requiredEditRight;

  /**
   * The element can directly be viewed.
   */
  private boolean viewable = true;

  /**
   * should not display in children tocs.
   */
  private boolean noToc = false;

  /**
   * if false, do not cache.
   */
  private boolean cachable = true;

  /**
   * The no search index.
   */
  private boolean noSearchIndex = false;

  /**
   * The parts.
   */
  private Map<String, GWikiArtefakt<?>> parts = new HashMap<String, GWikiArtefakt<?>>();

  /**
   * Welche Seite soll als Template verwendet werden.
   */
  private String copyFromPageId;

  /**
   * Kein Template, mit dem man neue Seiten erstellen kann.
   */
  private boolean noNewPage = false;

  /**
   * In case of overwrite, do not archive this.
   */
  private boolean noArchiv = false;

  /**
   * Standard cache time for this element type.
   */
  private long elementLifeTime = TimeInMillis.HOUR;

  /**
   * Help page associated with this type of page.
   */
  private String helpPageId = null;

  /**
   * Help page if this element will be edited.
   */
  private String editHelpPageId = null;

  /**
   * The allowed new child meta templates rule.
   */
  private String allowedNewChildMetaTemplatesRule = null;

  /**
   * Template ids of allowed new childs of this element type.
   * 
   * If null, all types are allowed.
   */
  private Matcher<String> allowedNewChildMetaTemplates = null;

  /**
   * only allow to create new if parent metatemplate matches.
   */
  private Matcher<String> allowedNewParentMetaTemplates = null;

  /**
   * The allowed new parent meta templates rule.
   */
  private String allowedNewParentMetaTemplatesRule = null;

  /**
   * Additionally properties descriptor for settings.
   */
  private GWikiPropsDescriptor addPropsDescriptor = null;

  /**
   * Flag to display contained editors tab wise or row wise.
   * 
   * Todo not used yet.
   */
  private boolean tabbedEditor = true;

  /**
   * The Mime Type of the element.
   */
  private String contentType = null;

  /**
   * Instantiates a new g wiki meta template.
   */
  public GWikiMetaTemplate()
  {

  }

  /**
   * Instantiates a new g wiki meta template.
   *
   * @param pageId the page id
   */
  public GWikiMetaTemplate(String pageId)
  {
    this.pageId = pageId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() throws Exception
  {

    if (StringUtils.isNotBlank(allowedNewChildMetaTemplatesRule) == true) {
      allowedNewChildMetaTemplates = new BooleanListRulesFactory<String>()
          .createMatcher(allowedNewChildMetaTemplatesRule);
    }
    if (StringUtils.isNotBlank(allowedNewParentMetaTemplatesRule) == true) {
      allowedNewParentMetaTemplates = new BooleanListRulesFactory<String>()
          .createMatcher(allowedNewParentMetaTemplatesRule);
    }
  }

  public Map<String, GWikiArtefakt<?>> getParts()
  {
    return parts;
  }

  public void setParts(Map<String, GWikiArtefakt<?>> parts)
  {
    this.parts = parts;
  }

  public String getElementType()
  {
    return elementType;
  }

  public void setElementType(String elementType)
  {
    this.elementType = elementType;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getCopyFromPageId()
  {
    return copyFromPageId;
  }

  public void setCopyFromPageId(String copyFromPageId)
  {
    this.copyFromPageId = copyFromPageId;
  }

  public boolean isNoNewPage()
  {
    return noNewPage;
  }

  public void setNoNewPage(boolean noNewPage)
  {
    this.noNewPage = noNewPage;
  }

  public boolean isViewable()
  {
    return viewable;
  }

  public void setViewable(boolean viewable)
  {
    this.viewable = viewable;
  }

  public boolean isCachable()
  {
    return cachable;
  }

  public void setCachable(boolean cachable)
  {
    this.cachable = cachable;
  }

  public boolean isNoSearchIndex()
  {
    return noSearchIndex;
  }

  public void setNoSearchIndex(boolean noSearchIndex)
  {
    this.noSearchIndex = noSearchIndex;
  }

  public String getRequiredViewRight()
  {
    return requiredViewRight;
  }

  public void setRequiredViewRight(String requiredViewRight)
  {
    this.requiredViewRight = requiredViewRight;
  }

  public String getRequiredEditRight()
  {
    return requiredEditRight;
  }

  public void setRequiredEditRight(String requiredEditRight)
  {
    this.requiredEditRight = requiredEditRight;
  }

  public boolean isNoArchiv()
  {
    return noArchiv;
  }

  public void setNoArchiv(boolean noArchiv)
  {
    this.noArchiv = noArchiv;
  }

  public long getElementLifeTime()
  {
    return elementLifeTime;
  }

  public void setElementLifeTime(long lifeTime)
  {
    this.elementLifeTime = lifeTime;
  }

  public boolean isNoToc()
  {
    return noToc;
  }

  public void setNoToc(boolean noToc)
  {
    this.noToc = noToc;
  }

  public String getHelpPageId()
  {
    return helpPageId;
  }

  public void setHelpPageId(String helpPageId)
  {
    this.helpPageId = helpPageId;
  }

  public String getEditHelpPageId()
  {
    return editHelpPageId;
  }

  public void setEditHelpPageId(String editHelpPageId)
  {
    this.editHelpPageId = editHelpPageId;
  }

  public Matcher<String> getAllowedNewChildMetaTemplates()
  {
    return allowedNewChildMetaTemplates;
  }

  public void setAllowedNewChildMetaTemplates(Matcher<String> allowedNewChildMetaTemplates)
  {
    this.allowedNewChildMetaTemplates = allowedNewChildMetaTemplates;
  }

  public void setAllowedNewChildMetaTemplatesRule(String rule)
  {
    this.allowedNewChildMetaTemplates = new BooleanListRulesFactory<String>().createMatcher(rule);
  }

  public Matcher<String> getAllowedNewParentMetaTemplates()
  {
    return allowedNewParentMetaTemplates;
  }

  public void setAllowedNewParentMetaTemplates(Matcher<String> allowedNewParentMetaTemplates)
  {
    this.allowedNewParentMetaTemplates = allowedNewParentMetaTemplates;
  }

  public void setAllowedNewParentMetaTemplatesRule(String rule)
  {
    this.allowedNewParentMetaTemplates = new BooleanListRulesFactory<String>().createMatcher(rule);
  }

  public GWikiPropsDescriptor getAddPropsDescriptor()
  {
    return addPropsDescriptor;
  }

  public void setAddPropsDescriptor(GWikiPropsDescriptor addPropsDescriptor)
  {
    this.addPropsDescriptor = addPropsDescriptor;
  }

  public String getAllowedNewChildMetaTemplatesRule()
  {
    return allowedNewChildMetaTemplatesRule;
  }

  public String getAllowedNewParentMetaTemplatesRule()
  {
    return allowedNewParentMetaTemplatesRule;
  }

  /**
   * @param tabbedEditor the tabbedEditor to set
   */
  public void setTabbedEditor(boolean tabbedEditor)
  {
    this.tabbedEditor = tabbedEditor;
  }

  /**
   * @return the tabbedEditor
   */
  public boolean isTabbedEditor()
  {
    return tabbedEditor;
  }

  public String getContentType()
  {
    return contentType;
  }

  public void setContentType(String contentType)
  {
    this.contentType = contentType;
  }

}
