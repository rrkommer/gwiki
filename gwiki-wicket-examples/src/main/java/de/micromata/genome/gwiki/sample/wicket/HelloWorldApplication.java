package de.micromata.genome.gwiki.sample.wicket;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.resource.locator.IResourceStreamLocator;

import de.micromata.genome.gwiki.wicket.GWikiLocalizer;
import de.micromata.genome.gwiki.wicket.GWikiResourceStreamLocator;

public class HelloWorldApplication extends WebApplication {
  /**
   * Constructor.
   */
  public HelloWorldApplication() {

  }

  /**
   * @see org.apache.wicket.Application#getHomePage()
   */
  public Class<HelloWorld> getHomePage() {
	return HelloWorld.class;
  }

  @Override
  protected void init() {

	getResourceSettings().setLocalizer(new GWikiLocalizer("edit/StandardI18n"));
	//
	// getResourceSettings().addStringResourceLoader(0, new IStringResourceLoader() {
	//
	// public String loadStringResource(Component component, String key)
	// {
	// return null;
	// }
	//
	// public String loadStringResource(Class< ? > clazz, String key, Locale locale, String style)
	// {
	// return null;
	// }
	// });
	final IResourceStreamLocator oldResLoc = getResourceSettings().getResourceStreamLocator();
	getResourceSettings().setResourceStreamLocator(new GWikiResourceStreamLocator(oldResLoc));
  }
}
