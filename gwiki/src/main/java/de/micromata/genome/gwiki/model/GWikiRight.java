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

package de.micromata.genome.gwiki.model;

import java.io.Serializable;

import de.micromata.genome.util.matcher.Matcher;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiRight implements Serializable
{

  private static final long serialVersionUID = 5380752128076783419L;

  /**
   * the right is a page oriented right
   */
  public static final String RIGHT_CAT_PAGE_RIGHT = "Page";

  public static final String RIGHT_CAT_SYSTEM_RIGHT = "System";

  public static final String RIGHT_CAT_OTHER_RIGHT = "Other";

  /**
   * name of the right.
   * 
   * Must be set.
   */
  private String name;

  /**
   * optional. @ RIGHT_CAT_PAGE_RIGHT, RIGHT_CAT_SYSTEM_RIGHT
   */
  private String category;

  /**
   * The right will not be displayed in the users profile setting
   */
  private boolean privateRight = false;

  /**
   * Optional. May contain a matcher rule set.
   */
  private String definition;

  /**
   * Compiled version of definition
   */
  private Matcher<String> definitionRule;

  /**
   * Description of Right or role, displayed in the user profile settings.
   */
  private String description;

  public GWikiRight()
  {

  }

  public GWikiRight(String name)
  {
    this.name = name;
  }

  public GWikiRight(String name, String category, String definition)
  {
    this(name);
    this.category = category;
    this.definition = definition;

  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getCategory()
  {
    return category;
  }

  public void setCategory(String category)
  {
    this.category = category;
  }

  public String getDefinition()
  {
    return definition;
  }

  public void setDefinition(String definition)
  {
    this.definition = definition;
  }

  public boolean isPrivateRight()
  {
    return privateRight;
  }

  public void setPrivateRight(boolean privateRight)
  {
    this.privateRight = privateRight;
  }

  public Matcher<String> getDefinitionRule()
  {
    return definitionRule;
  }

  public void setDefinitionRule(Matcher<String> definitionRule)
  {
    this.definitionRule = definitionRule;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }
}
