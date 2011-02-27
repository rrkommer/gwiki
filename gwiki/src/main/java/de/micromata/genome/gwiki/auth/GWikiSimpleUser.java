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

package de.micromata.genome.gwiki.auth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.EveryMatcher;
import de.micromata.genome.util.matcher.Matcher;

/**
 * A Simple user implementation held by GWikiSimpleUserAuthorization.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSimpleUser implements Serializable
{

  private static final long serialVersionUID = -87139857211402475L;

  public static final String ANON_USER_NAME = "anon";

  public String user = ANON_USER_NAME;

  public String password = "";

  public String email = "genome@micromata.de";

  private String rightsMatcherRule = "+*";

  public Matcher<String> rightsMatcher = new EveryMatcher<String>();

  private Map<String, String> props = new HashMap<String, String>();

  public GWikiSimpleUser()
  {

  }

  public GWikiSimpleUser(GWikiSimpleUser other)
  {
    this.user = other.user;
    this.email = other.email;
    this.password = other.password;
    this.rightsMatcher = other.rightsMatcher;
    this.rightsMatcherRule = other.rightsMatcherRule;
    this.props.putAll(other.props);
  }

  public GWikiSimpleUser(String user, String password, String email, String rule)
  {
    this.user = user;
    this.password = password;
    this.email = email;
    setRightsMatcherRule(rule);
  }

  public String getRightsMatcherRule()
  {
    return rightsMatcherRule;

  }

  public void setRightsMatcherRule(String rule)
  {
    rightsMatcher = new BooleanListRulesFactory<String>().createMatcher(rule);
    rightsMatcherRule = rule;
  }

  public boolean isAnon()
  {
    return StringUtils.equals(user, ANON_USER_NAME);
  }

  public String getUser()
  {
    return user;
  }

  public void setUser(String user)
  {
    this.user = user;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public Matcher<String> getRightsMatcher()
  {
    return rightsMatcher;
  }

  public void setRightsMatcher(Matcher<String> rightsMatcher)
  {
    this.rightsMatcher = rightsMatcher;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public Map<String, String> getProps()
  {
    return props;
  }

  public void setProps(Map<String, String> props)
  {
    this.props = props;
  }
}