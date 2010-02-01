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
