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

package de.micromata.genome.gwiki.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.springframework.beans.factory.FactoryBean;

/**
 * Spring factory bean to create a mail session.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SessionBeanFactory implements FactoryBean
{
  private Map<String, String> properties = new HashMap<String, String>();

  public SessionBeanFactory()
  {

  }

  public SessionBeanFactory(Map<String, String> properties)
  {
    this.properties = properties;
  }

  protected Session createSession()
  {
    final Properties props = new Properties();
    props.putAll(properties);
    Session sess;

    if (StringUtils.isNotBlank(props.getProperty("mail.smtp.password")) == true) {
      sess = Session.getDefaultInstance(props, new Authenticator() {

        @Override
        protected PasswordAuthentication getPasswordAuthentication()
        {
          return new PasswordAuthentication(props.getProperty("mail.smtp.user"), props.getProperty("mail.smtp.password"));
        }
      });
    } else {
      sess = Session.getDefaultInstance(props);
    }
    return sess;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.beans.factory.FactoryBean#getObject()
   */
  public Object getObject() throws Exception
  {
    return createSession();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.beans.factory.FactoryBean#getObjectType()
   */
  public Class< ? > getObjectType()
  {
    return Session.class;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.beans.factory.FactoryBean#isSingleton()
   */
  public boolean isSingleton()
  {
    return true;
  }

  public Map<String, String> getProperties()
  {
    return properties;
  }

  public void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

}
