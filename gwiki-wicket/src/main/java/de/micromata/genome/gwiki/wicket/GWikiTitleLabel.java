package de.micromata.genome.gwiki.wicket;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import de.micromata.genome.gwiki.wicket.util.GWikiUtils;

/**
 * A {@link Label} representing the title of a GWiki page.<br/>
 * If no page is found a message will be returned, stating that no page was found. The i18n Key to that message is
 * <code>GWikiTitleLabel.Page_does_not_exist</code> .
 * 
 * @author fnaujoks
 * 
 */
public class GWikiTitleLabel extends Label
{

  private static final long serialVersionUID = -8974339515347265719L;

  /**
   * Creates a {@link Label} containing the title of a GWiki page identified by the given page id.
   * 
   * @param id component id
   * @param gwikiPageId GWiki page id
   */
  public GWikiTitleLabel(final String id, final String gwikiPageId)
  {
    super(id);

    String title = GWikiUtils.findWikiPageTitle(gwikiPageId);
    if (title == null) {
      title = getLocalizer().getString("GWikiTitleLabel.Page_does_not_exist", this);
    }

    IModel<String> model = new Model<String>(title);

    setDefaultModel(model);

    setEscapeModelStrings(false);
  }
}
