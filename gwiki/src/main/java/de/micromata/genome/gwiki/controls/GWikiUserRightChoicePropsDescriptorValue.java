package de.micromata.genome.gwiki.controls;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.GWikiAuthorizationExt;
import de.micromata.genome.gwiki.model.GWikiRight;
import de.micromata.genome.gwiki.page.impl.GWikiPropsDescriptorValue;
import de.micromata.genome.gwiki.page.impl.GWikiPropsEditorArtefakt;
import de.micromata.genome.gwiki.page.impl.PropsEditContext;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiUserRightChoicePropsDescriptorValue extends GWikiPropsDescriptorValue
{
  @Override
  public String render(GWikiPropsEditorArtefakt<?> editor, PropsEditContext pct)
  {
    GWikiAuthorization auth = pct.getWikiContext().getWikiWeb().getAuthorization();
    if ((auth instanceof GWikiAuthorizationExt) == false) {
      return super.render(editor, pct);
    }
    GWikiAuthorizationExt authx = (GWikiAuthorizationExt) auth;
    SortedMap<String, GWikiRight> systemRights = authx.getSystemRights(pct.getWikiContext());
    String pval = pct.getPropsValue();
    SortedMap<String, GWikiRight> usr = authx.getUserRight(pct.getWikiContext(), systemRights, pval);
    for (Map.Entry<String, GWikiRight> me : usr.entrySet()) {
      if (systemRights.containsKey(me.getKey()) == false) {
        systemRights.put(me.getKey(), me.getValue());
      }
    }

    String id = pct.getWikiContext().genHtmlId("gwikirightl");
    String value = pct.getPropsValue();
    List<String> attrs = Xml.asList("id", id, "type", "text", "size", "40", "name", pct.getRequestKey(), "value",
        value);
    if (pct.isReadOnly() == true) {
      Xml.add(attrs, "disabled", "disabled");
    }

    Xml.add(attrs, "class", "wikiRightChoice");
    StringBuilder sb = new StringBuilder();
    sb.append(Html.input(Xml.listAsAttrs(attrs)).toString());
    sb.append(
        "<script type=\"text/javascript\">\n$(document).ready({$('# " + id + ").autocomplete({source: [");
    sb.append("''");
    for (String rs : systemRights.keySet()) {
      sb.append(", '").append(pct.getWikiContext().escape(rs)).append("'");
    }
    sb.append(" ]});});\n</script>\n");
    return sb.toString();

  }
}
