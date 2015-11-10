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
// Project Genome Core
//
// Author    roger@micromata.de
// Created   19.03.2007
// Copyright Micromata 19.03.2007
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.bean;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ByteArrayResource;

/**
 * Creates a Bean via spring context
 * 
 * @author roger@micromata.de
 * 
 */
public class SimpleBeanCreator
{
  /**
   * Erstellt ein Bean
   * 
   * @param content "<bean>" definition
   * @param beanName Name des Beans das zuzuckgegeben werden soll
   * @return das Konstruierte bean
   * @throws Exception wenn bean nicht erstellt werden kann
   */
  public static final String oldBeanPrefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<!DOCTYPE beans PUBLIC \"-//SPRING//DTD BEAN//EN\" \"http://www.springframework.org/dtd/spring-beans.dtd\">\n<beans>";

  public static final String noBeanPrefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<beans>";

  // TODO SqlConsoleReportDefinition.xml cannot be parsed with newBeanPrefix, why
  public static final String newBeanPrefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<beans xmlns=\"http://www.springframework.org/schema/beans\"\n"
      + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
      + "xmlns:util=\"http://www.springframework.org/schema/util\"\n"
      + "xsi:schemaLocation=\"\n"
      + "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.0.xsd\n"
      + "http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-2.0.xsd\">";

  public static Object createBean(String content, String beanName) throws Exception
  {

    String rt = newBeanPrefix + //
        content
        + //
        "</beans>\n";
    // Class< ? > cls = XmlBeanFactory.class;
    // ClassLoader cll = cls.getClassLoader();
    XmlBeanFactory factory = new XmlBeanFactory(new ByteArrayResource(rt.getBytes("UTF-8")));
    Object o = factory.getBean(beanName);
    return o;
  }
}
