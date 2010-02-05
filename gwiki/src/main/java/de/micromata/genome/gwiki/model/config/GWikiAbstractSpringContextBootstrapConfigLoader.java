package de.micromata.genome.gwiki.model.config;

import javax.servlet.ServletConfig;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ConfigurableApplicationContext;

public abstract class GWikiAbstractSpringContextBootstrapConfigLoader implements GWikiBootstrapConfigLoader
{
  protected String fileName;

  protected abstract ConfigurableApplicationContext createApplicationContext(String fileName);

  public String getApplicationContextName()
  {
    if (StringUtils.isEmpty(fileName) == true) {

      fileName = "GWikiContext.xml";
    }
    return fileName;
  }

  public GWikiDAOContext loadConfig(ServletConfig config)
  {
    ConfigurableApplicationContext actx = createApplicationContext(getApplicationContextName());
    actx.addBeanFactoryPostProcessor(new GWikiDAOContextPropertyPlaceholderConfigurer(config));
    actx.refresh();
    return (GWikiDAOContext) actx.getBean("GWikiBootstrapConfig");
  }
}
