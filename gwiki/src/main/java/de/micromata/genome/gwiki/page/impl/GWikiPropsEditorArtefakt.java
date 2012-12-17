////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
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

package de.micromata.genome.gwiki.page.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHelpLinkMacro;
import de.micromata.genome.util.types.Converter;
import de.micromata.genome.util.types.Pair;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;
import de.micromata.genome.util.xml.xmlbuilder.XmlNode;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

/**
 * Editor artefakt for properties with a GWikiPropsDescriptor.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 * @param <T>
 */
public class GWikiPropsEditorArtefakt<T extends Serializable> extends GWikiEditorArtefaktBase<T>
{

  private static final long serialVersionUID = -987015918439198302L;

  private GWikiPropsArtefakt props;

  private GWikiPropsDescriptor propDescriptor;

  public GWikiPropsEditorArtefakt(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partName, GWikiPropsArtefakt props,
      GWikiPropsDescriptor propDescriptor)
  {
    super(elementToEdit, editBean, partName);
    this.props = props;
    this.propDescriptor = propDescriptor;
    mergeSettingsProps();
  }

  @Override
  public void prepareHeader(GWikiContext wikiContext)
  {
    wikiContext.getRequiredJs().add("/static/js/jquery.fieldset-collapsible.js");

    if (wikiContext.getRequestAttribute("propseditartefakt_collrendered") != Boolean.TRUE) {
      wikiContext.getRequiredJs().add("/static/js/fieldset-collapsible-config.js");
      wikiContext.setRequestAttribute("propseditartefakt_collrendered", Boolean.TRUE);
    }

    super.prepareHeader(wikiContext);
  }

  private void lookupPropDescriptor(GWikiContext ctx)
  {
    if (propDescriptor != null)
      return;
    String id = "admin/templates/" + partName + "PropDescriptor";
    GWikiElement el = ctx.getWikiWeb().findElement(id);
    if (el == null) {
      return;
    }
    if ((el instanceof GWikiConfigElement) == false) {
      return;
    }
    GWikiConfigElement cfel = (GWikiConfigElement) el;
    Object o = cfel.getConfig().getCompiledObject();
    if (o == null || (o instanceof GWikiPropsDescriptor) == false) {
      return;
    }
    propDescriptor = (GWikiPropsDescriptor) o;
    mergeSettingsProps();
  }

  private void mergeSettingsProps()
  {
    if (propDescriptor != null && elementToEdit != null && StringUtils.equals(partName, "Settings") == true) {
      GWikiMetaTemplate mt = elementToEdit.getMetaTemplate();
      if (mt != null && mt.getAddPropsDescriptor() != null) {
        List<GWikiPropsDescriptorValue> descriptors = new ArrayList<GWikiPropsDescriptorValue>();
        descriptors.addAll(propDescriptor.getDescriptors());
        descriptors.addAll(mt.getAddPropsDescriptor().getDescriptors());
        GWikiPropsDescriptor np = new GWikiPropsDescriptor();
        np.setDescriptors(descriptors);
        np.setGroups(propDescriptor.getGroups());
        this.propDescriptor = np;
      }
    }
  }

  private void initPropDescriptor(GWikiContext ctx)
  {
    lookupPropDescriptor(ctx);
    if (propDescriptor == null) {
      buildPropDescriptor(ctx);
    }

  }

  private void buildPropDescriptor(GWikiContext ctx)
  {
    propDescriptor = new GWikiPropsDescriptor();
    for (Map.Entry<String, String> me : props.getCompiledObject().getMap().entrySet()) {
      GWikiPropsDescriptorValue val = new GWikiPropsDescriptorValue();
      val.setKey(me.getValue());
      propDescriptor.getDescriptors().add(val);
    }
  }

  public void onParseRequest(PropsEditContext pct)
  {
    pct.getPropDescriptor().parseRequest(pct);
  }

  public void onValidate(PropsEditContext pct)
  {
    pct.getPropDescriptor().validate(pct);
  }

  public void onSave(GWikiContext ctx)
  {
    initPropDescriptor(ctx);

    GWikiElement el = ctx.getWikiElement();
    String metaTemplateId = el.getMetaTemplate().getPageId();
    for (GWikiPropsDescriptorValue d : propDescriptor.getDescriptors()) {
      if (isForThisElement(d, metaTemplateId) == false) {
        continue;
      }
      PropsEditContext pct = createPropContext(ctx, d);
      onParseRequest(pct);
      onValidate(pct);
    }
  }

  protected GWikiProps getCompiledProps()
  {
    if (props.getCompiledObject() == null) {
      props.setCompiledObject(new GWikiProps());
    }
    return props.getCompiledObject();
  }

  public boolean isForThisElement(GWikiPropsDescriptorValue d, String metaTemplateId)
  {
    if (metaTemplateId == null) {
      return true;
    }
    String s = d.getRequiredMetaTemplateId();
    if (StringUtils.isBlank(s) == true) {
      return true;
    }
    List<String> reqs = Converter.parseStringTokens(s, ", ", false);
    for (String req : reqs) {
      if (StringUtils.equals(metaTemplateId, req) == true) {
        return true;
      }
    }
    return false;
  }

  protected XmlNode[] renderOptions(PropsEditContext pct)
  {
    Map<String, String> m = pct.getOptionValues();
    XmlElement el = Xml.element("select", "name", pct.getRequestKey()/* , "value", pct.getPropsValue() */);
    for (Map.Entry<String, String> me : m.entrySet()) {
      XmlElement option;
      if (StringUtils.equals(pct.getPropsValue(), me.getKey()) == true) {
        option = Xml.element("option", "value", me.getKey(), "selected", "selected");
      } else {
        option = Xml.element("option", "value", me.getKey());
      }
      option.nest(Xml.text(me.getValue()));
      el.nest(option);
    }
    return Xml.nodes(el);
  }

  protected PropsEditContext createPropContext(GWikiContext ctx, GWikiPropsDescriptorValue d)
  {
    PropsEditContext pc = new PropsEditContext(ctx, props, partName, d);
    if (props.getCompiledObject() == null) {
      props.setCompiledObject(new GWikiProps());
    }
    return pc;
  }

  public void onRenderInternal(PropsEditContext pct)
  {

    String value = pct.getPropsValue();

    XmlNode[] controlNodes = new XmlNode[0];
    String type = pct.getControlType();
    if (StringUtils.equals(type, "BOOLEAN") == true) {
      List<String> attrs = Xml.asList("type", "checkbox", "name", pct.getRequestKey(), "value", "true");
      if (pct.isReadOnly() == true) {
        attrs = Xml.add(attrs, "disabled", "true");
      }
      if (StringUtils.equals(pct.getPropsValue(), "true") == true) {
        attrs = Xml.add(attrs, "checked", "true");
      }
      controlNodes = Xml.nodes(Html.input(Xml.listAsAttrs(attrs)));
    } else if (StringUtils.equals(type, "OPTION") == true) {
      controlNodes = renderOptions(pct);
    } else if (StringUtils.equals(type, "DATE") == true) {
      String dates = pct.getPropsValue();
      Date date = GWikiProps.parseTimeStamp(dates);
      String fdate = pct.getWikiContext().getUserDateString(date);
      List<String> attrs = Xml.asList("type", "text", "size", "30", "name", pct.getRequestKey(), "value", fdate);
      if (pct.isReadOnly() == true) {
        Xml.add(attrs, "disabled", "disabled");
      }
      controlNodes = Xml.nodes(Html.input(Xml.listAsAttrs(attrs)));
    } else {
      List<String> attrs = Xml.asList("type", "text", "size", "40", "name", pct.getRequestKey(), "value", value);
      if (pct.isReadOnly() == true) {
        Xml.add(attrs, "disabled", "disabled");
      }
      if (StringUtils.equals(type, "PAGEID") == true) {
        Xml.add(attrs, "class", "wikiPageEditText");
      }
      controlNodes = Xml.nodes(Html.input(Xml.listAsAttrs(attrs)));
    }
    for (XmlNode n : controlNodes) {
      pct.append(n.toString());
    }
  }

  public String onRender(PropsEditContext pct)
  {
    return pct.getPropDescriptor().render(this, pct);
  }

  public String renderHelpLink(GWikiPropsDescriptorValue d, GWikiContext ctx)
  {
    String link = d.getHelpLink();
    if (StringUtils.isEmpty(link) == true) {
      return "&nbsp;";
    }
    String pageId = link;
    int localp = link.indexOf('#');
    if (localp != -1) {
      pageId = link.substring(0, localp);
    }
    // GWikiElementInfo ei = ctx.getWikiWeb().findElementInfo(pageId);
    String lang = ctx.getWikiWeb().getAuthorization().getCurrentUserLocale(ctx).getLanguage();
    String l = GWikiHelpLinkMacro.getHelpPage(pageId, lang, ctx);
    if (StringUtils.isEmpty(l) == true) {
      return "&nbsp;";
    }
    String t = l;
    if (localp != -1) {
      t = t + link.substring(localp);
    }
    String lurl = ctx.localUrl(t);
    return "<a target=\"gwiki_help\" href=\"" + lurl + "\">?</a>";
    // return t;

  }

  protected GWikiPropsGroupDescriptor getGroupDescriptorByName(String key)
  {
    if (propDescriptor.getGroups() == null || propDescriptor.getGroups().isEmpty() == true) {
      return null;
    }
    for (GWikiPropsGroupDescriptor g : propDescriptor.getGroups()) {
      if (StringUtils.equals(key, g.getKey()) == true) {
        return g;
      }
    }
    return null;
  }

  protected Pair<GWikiPropsGroupDescriptor, List<GWikiPropsDescriptorValue>> getDescPairByName(
      List<Pair<GWikiPropsGroupDescriptor, List<GWikiPropsDescriptorValue>>> ret, String key)
  {
    for (Pair<GWikiPropsGroupDescriptor, List<GWikiPropsDescriptorValue>> p : ret) {
      if (StringUtils.equals(p.getFirst().getKey(), key) == true) {
        return p;
      }
    }
    return null;
  }

  protected List<Pair<GWikiPropsGroupDescriptor, List<GWikiPropsDescriptorValue>>> getGroupedProps(String metaTemplateId)
  {
    String STANDARDKEY = "STANDARD";
    GWikiPropsGroupDescriptor sg = new GWikiPropsGroupDescriptor();
    sg.setCollabsable(true);
    sg.setClosed(false);
    sg.setTitle("Standard");
    sg.setKey(STANDARDKEY);
    List<Pair<GWikiPropsGroupDescriptor, List<GWikiPropsDescriptorValue>>> ret = new ArrayList<Pair<GWikiPropsGroupDescriptor, List<GWikiPropsDescriptorValue>>>();
    Pair<GWikiPropsGroupDescriptor, List<GWikiPropsDescriptorValue>> standardPair = new Pair<GWikiPropsGroupDescriptor, List<GWikiPropsDescriptorValue>>();
    standardPair.setFirst(sg);
    standardPair.setSecond(new ArrayList<GWikiPropsDescriptorValue>());
    if (propDescriptor.getGroups() == null || propDescriptor.getGroups().isEmpty() == true) {
      standardPair.setValue(propDescriptor.getDescriptors());
      ret.add(standardPair);
      return ret;
    }
    for (GWikiPropsDescriptorValue d : propDescriptor.getDescriptors()) {
      if (isForThisElement(d, metaTemplateId) == false) {
        continue;
      }
      GWikiPropsGroupDescriptor group;
      if (StringUtils.isEmpty(d.getGroup()) == true) {
        group = sg;
      } else {
        group = getGroupDescriptorByName(d.getGroup());
        if (group == null) {
          group = sg;
          if (getDescPairByName(ret, STANDARDKEY) == null) {
            ret.add(standardPair);
          }
        }
      }
      Pair<GWikiPropsGroupDescriptor, List<GWikiPropsDescriptorValue>> pair = getDescPairByName(ret, group.getKey());

      if (pair == null) {
        if (group == sg) {
          pair = standardPair;
          ret.add(pair);
        } else {
          Pair<GWikiPropsGroupDescriptor, List<GWikiPropsDescriptorValue>> np = new Pair<GWikiPropsGroupDescriptor, List<GWikiPropsDescriptorValue>>();
          np.setKey(group);
          np.setValue(new ArrayList<GWikiPropsDescriptorValue>());
          pair = np;
          ret.add(pair);

        }
      }
      pair.getSecond().add(d);
    }
    return ret;
  }

  public void renderViaDescriptor(GWikiContext ctx)
  {
    boolean hasAnyDescription = false;
    for (GWikiPropsDescriptorValue d : propDescriptor.getDescriptors()) {
      if (StringUtils.isNotBlank(d.getDescription()) == true) {
        hasAnyDescription = true;
        break;
      }
    }
    // TODO Title rendering
    // XmlElement table = Html.table(Xml.attrs("width", "100%", "class", "gwikiProperties"), //
    // Html.tr( //
    // Html.th(Xml.attrs("width", "70", "align", "left"), Xml.code(ctx.getTranslated("gwiki.propeditor.title.key"))), //
    // Html.th(Xml.attrs("width", "300", "align", "left"), Xml.code(ctx.getTranslated("gwiki.propeditor.title.value"))), //
    // Html.th(Xml.attrs("width", "16", "align", "left"), Xml.code("&nbsp;")), //
    // Html.th(Xml.attrs("align", "left"), Xml.code(hasAnyDescription == false ? "" : ctx
    // .getTranslated("gwiki.propeditor.title.description")))));
    Object o = ctx.getRequest().getAttribute("form");
    String metaTemplateId = null;
    if (o instanceof GWikiEditPageActionBean) {
      GWikiEditPageActionBean bean = ((GWikiEditPageActionBean) ctx.getRequest().getAttribute("form"));
      metaTemplateId = bean.getMetaTemplate().getPageId();
    }
    // String metaTemplateId = el.getMetaTemplate().getPageId();

    List<Pair<GWikiPropsGroupDescriptor, List<GWikiPropsDescriptorValue>>> pl = getGroupedProps(metaTemplateId);
    for (Pair<GWikiPropsGroupDescriptor, List<GWikiPropsDescriptorValue>> p : pl) {
      ctx.append("<fieldset");
      if (p.getFirst().isCollabsable() == true) {
        if (p.getFirst().isClosed() == true) {
          ctx.append("  class=\"startClosed\"");
        } else {
          ctx.append("  class=\"collapsible\"");
        }
      }
      ctx.append("><legend class=\"ui-state-default ui-corner-top ui-tabs-selected ui-state-active\">")//
          .append(ctx.getTranslatedProp(p.getFirst().getTitle())).append("</legend><div>");
      for (GWikiPropsDescriptorValue d : p.getSecond()) {
        PropsEditContext pctx = createPropContext(ctx, d);
        if (pctx.isDisplayable() == false) {
          continue;
        }
        String nested = onRender(pctx);
        String label = d.getKey();
        if (d.getLabel() != null) {
          label = ctx.getTranslatedProp(d.getLabel());
        }
        ctx.append("<div style=\"clear:left; margin-top:0.5em\">");
        ctx.append("<label style=\"float:left; width:10em;\">").appendEscText(label).append("</label>");
        ctx.append("<div style=\"float:left; width:23em\">").append(nested).append("</div>");
        ctx.append("<span style=\"float:left; margin-left:1em; width:2em; height:28px\">").append(renderHelpLink(d, ctx)).append("</span>");
        ctx.append("<span>").append(StringUtils.defaultString(ctx.getTranslatedProp(d.getDescription()))).append("</span>\n");
        ctx.append("</div>");
        //
        // table.add( //
        // Html.tr( //
        // Html.td(Xml.code(label)), //
        // Html.td(Xml.code(nested)), //
        // Html.td(Xml.code(renderHelpLink(d, ctx))), //
        // Html.td(Xml.code(StringUtils.defaultString(ctx.getTranslatedProp(d.getDescription()))))));
        //

      }
      ctx.append("</div></fieldset>\n");
    }
    // for (GWikiPropsDescriptorValue d : propDescriptor.getDescriptors()) {
    // if (isForThisElement(d, metaTemplateId) == false)
    // continue;
    // PropsEditContext pctx = createPropContext(ctx, d);
    // if (pctx.isDisplayable() == false) {
    // continue;
    // }
    // String nested = onRender(pctx);
    // String label = d.getKey();
    // if (d.getLabel() != null) {
    // label = ctx.getTranslatedProp(d.getLabel());
    // }
    // table.add( //
    // Html.tr( //
    // Html.td(Xml.code(label)), //
    // Html.td(Xml.code(nested)), //
    // Html.td(Xml.code(renderHelpLink(d, ctx))), //
    // Html.td(Xml.code(StringUtils.defaultString(ctx.getTranslatedProp(d.getDescription()))))));
    // }
    // ctx.append(table.toString());

  }

  public boolean renderWithParts(GWikiContext ctx)
  {

    initPropDescriptor(ctx);

    renderViaDescriptor(ctx);
    return true;

  }

}
