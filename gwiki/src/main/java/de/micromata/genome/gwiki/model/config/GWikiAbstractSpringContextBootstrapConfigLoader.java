package de.micromata.genome.gwiki.model.config;

import javax.servlet.ServletConfig;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Base implementation to load GWikiDAOContext from a spring context file.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiAbstractSpringContextBootstrapConfigLoader implements GWikiBootstrapConfigLoader
{
  protected String fileName;

  protected abstract ConfigurableApplicationContext createApplicationContext(String fileName);

  protected BeanFactory beanFactory;

  public String getApplicationContextName()
  {
    if (StringUtils.isEmpty(fileName) == true) {

      fileName = "GWikiContext.xml";
    }
    return fileName;
  }

  public GWikiDAOContext loadConfig(ServletConfig config)
  {
    if (config != null) {
      fileName = config.getInitParameter("de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader.fileName");
    }
    ConfigurableApplicationContext actx = createApplicationContext(getApplicationContextName());
    actx.addBeanFactoryPostProcessor(new GWikiDAOContextPropertyPlaceholderConfigurer(config));
    actx.refresh();
    beanFactory = actx;
    return (GWikiDAOContext) actx.getBean("GWikiBootstrapConfig");
  }

  public String getFileName()
  {
    return fileName;
  }

  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }

  public BeanFactory getBeanFactory()
  {
    return beanFactory;
  }

  public void setBeanFactory(BeanFactory beanFactory)
  {
    this.beanFactory = beanFactory;
  }
}
