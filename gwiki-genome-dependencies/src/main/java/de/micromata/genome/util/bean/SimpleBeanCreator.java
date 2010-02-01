/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
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
    Class< ? > cls = XmlBeanFactory.class;
    ClassLoader cll = cls.getClassLoader();
    XmlBeanFactory factory = new XmlBeanFactory(new ByteArrayResource(rt.getBytes("UTF-8")));
    Object o = factory.getBean(beanName);
    return o;
  }
}
