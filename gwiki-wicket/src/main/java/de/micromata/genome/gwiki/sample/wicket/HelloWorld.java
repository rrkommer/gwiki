package de.micromata.genome.gwiki.sample.wicket;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

import de.micromata.genome.gwiki.wicket.GWikiLabel;

public class HelloWorld extends WebPage
{
  /**
   * Constructor
   */
  public HelloWorld()
  {
    // add(new Label("message", "Hello <b>World</b>!"));

    add(new Label("message", "Skin Label: " + getLocalizer().getString("gwiki.profile.skin.label", this)));

    // add(new Label("text", text));
    add(new GWikiLabel("text", "home/rkommer/WikiFragmentBeispiel"));
  }
}
