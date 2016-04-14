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
