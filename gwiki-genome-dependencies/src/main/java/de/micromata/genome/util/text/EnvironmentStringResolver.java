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

/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   09.01.2008
// Copyright Micromata 09.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.text;

import org.springframework.jndi.JndiObjectFactoryBean;

/**
 * Durchsuchen der Environment-Parameter der Webanwendung.
 * 
 * @author noodles@micromata.de
 */
public class EnvironmentStringResolver implements StringResolver
{

  /**
   * @param placeholder Namen der Environment-Variablen.
   * @return Nie <code>null</code>.
   * 
   * @see de.micromata.web.StringResolver#resolve(java.lang.String)
   */
  public String resolve(String placeholder)
  {
    try {
      final JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
      factory.setResourceRef(true);
      factory.setJndiName(placeholder);
      factory.afterPropertiesSet();

      final Object object = factory.getObject();

      if (object instanceof String == false) {
        throw new IllegalArgumentException("Invalid parameter (String expected or not found): " + placeholder);
      }

      return (String) object;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

}
