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

package de.micromata.genome.gwiki.page.impl.wiki.macros.registry;

import java.util.Map;
import java.util.TreeMap;

import de.micromata.genome.gwiki.page.impl.i18n.GWikiI18nMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentUnsecureHtml;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiBreadcrumbsMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiChangeCommentMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiChildrenMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiChunkMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiCodeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiColumnMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiFancyBoxMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiGroovyScriptMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHTMLcommentMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHelpLinkMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHiddenMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHierarchicalBreadcrumbMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyPTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiIfMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiImageGalleryMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiIncludeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiLocalAnchorMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiNewElementMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiNoFormatBodyMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiPageAttachmentsMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiPageCommentMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiPageIntroMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiPageTreeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiQuoteMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiScrollNextPrevPageMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiSectionMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiSwitchSpaceMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiTocMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiUseMacroMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlBlockquoteMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlDivMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlFieldsetMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlLegendMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlPreMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlSpanMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlTableMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlTdMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlThMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlTrMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlVideoMacro;

/**
 * the build in macros.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiBuildinProviderServiceImpl implements GWikiMacroProviderService
{

  @Override
  public Map<String, GWikiMacroFactory> getMacros()
  {
    Map<String, GWikiMacroFactory> ret = new TreeMap<String, GWikiMacroFactory>();
    ret.put("p", new GWikiMacroClassFactory(GWikiHtmlBodyPTagMacro.class));
    ret.put("pre", new GWikiMacroClassFactory(GWikiHtmlPreMacro.class));
    ret.put("blockquote", new GWikiMacroClassFactory(GWikiHtmlBlockquoteMacro.class));
    ret.put("span", new GWikiMacroClassFactory(GWikiHtmlSpanMacro.class));

    ret.put("breadcrumbs", new GWikiMacroClassFactory(GWikiBreadcrumbsMacroBean.class));
    ret.put("toc", new GWikiMacroClassFactory(GWikiTocMacro.class));
    ret.put("code", new GWikiMacroClassFactory(GWikiCodeMacro.class));
    ret.put("children", new GWikiMacroClassFactory(GWikiChildrenMacro.class));
    ret.put("pagecomments", new GWikiMacroClassFactory(GWikiPageCommentMacro.class));
    ret.put("anchor", new GWikiMacroClassFactory(GWikiLocalAnchorMacroBean.class));
    ret.put("msg", new GWikiMacroClassFactory(GWikiI18nMacroBean.class));
    ret.put("pageintro", new GWikiMacroClassFactory(GWikiPageIntroMacroBean.class));
    ret.put("html", GWikiFragmentUnsecureHtml.getFactory());
    ret.put("noformat", new GWikiMacroClassFactory(GWikiNoFormatBodyMacro.class));
    ret.put("if", new GWikiMacroClassFactory(GWikiIfMacro.class));
    ret.put("quote", new GWikiMacroClassFactory(GWikiQuoteMacroBean.class));
    ret.put("usemacro", new GWikiMacroClassFactory(GWikiUseMacroMacro.class));
    ret.put("include", new GWikiMacroClassFactory(GWikiIncludeMacro.class));
    ret.put("HTMLcomment", new GWikiMacroClassFactory(GWikiHTMLcommentMacro.class));
    ret.put("ScrollNextPrevPage", new GWikiMacroClassFactory(GWikiScrollNextPrevPageMacro.class));
    ret.put("groovy", new GWikiMacroClassFactory(GWikiGroovyScriptMacro.class));
    ret.put("pageattachments", new GWikiMacroClassFactory(GWikiPageAttachmentsMacro.class));
    ret.put("chunk", new GWikiMacroClassFactory(GWikiChunkMacro.class));
    //        ret.put("form", new GWikiMacroClassFactory(GWikiFormMacro.class));
    //    ret.put("formlabel", new GWikiMacroClassFactory(GWikiFormLabelMacro.class));
    //    ret.put("input", new GWikiMacroClassFactory(GWikiFormInputMacro.class));
    ret.put("column", new GWikiMacroClassFactory(GWikiColumnMacro.class));
    ret.put("section", new GWikiMacroClassFactory(GWikiSectionMacro.class));
    ret.put("gallery", new GWikiMacroClassFactory(GWikiImageGalleryMacro.class));
    ret.put("changecomment", new GWikiMacroClassFactory(GWikiChangeCommentMacroBean.class));
    ret.put("fancybox", new GWikiMacroClassFactory(GWikiFancyBoxMacroBean.class));
    ret.put("pagetree", new GWikiMacroClassFactory(GWikiPageTreeMacro.class));
    ret.put("navbreadcrumb", new GWikiMacroClassFactory(GWikiHierarchicalBreadcrumbMacroBean.class));
    ret.put("hidden", new GWikiMacroClassFactory(GWikiHiddenMacro.class));
    ret.put("table", new GWikiMacroClassFactory(GWikiHtmlTableMacro.class));
    ret.put("th", new GWikiMacroClassFactory(GWikiHtmlThMacro.class));
    ret.put("td", new GWikiMacroClassFactory(GWikiHtmlTdMacro.class));
    ret.put("tr", new GWikiMacroClassFactory(GWikiHtmlTrMacro.class));
    ret.put("style", new GWikiMacroClassFactory(GWikiHtmlFieldsetMacro.class));
    ret.put("fieldset", new GWikiMacroClassFactory(GWikiHtmlFieldsetMacro.class));
    ret.put("legend", new GWikiMacroClassFactory(GWikiHtmlLegendMacro.class));
    ret.put("div", new GWikiMacroClassFactory(GWikiHtmlDivMacro.class));
    ret.put("audio", new GWikiMacroClassFactory(GWikiHtmlVideoMacro.class));
    ret.put("video", new GWikiMacroClassFactory(GWikiHtmlVideoMacro.class));
    ret.put("helplink", new GWikiMacroClassFactory(GWikiHelpLinkMacro.class));
    ret.put("newpage", new GWikiMacroClassFactory(GWikiNewElementMacro.class));
    ret.put("switchspace", new GWikiMacroClassFactory(GWikiSwitchSpaceMacro.class));
    return ret;
  }

}
