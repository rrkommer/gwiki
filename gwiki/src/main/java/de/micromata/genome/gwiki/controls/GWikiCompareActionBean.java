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

package de.micromata.genome.gwiki.controls;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.GWikiTextArtefaktBase;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.utils.AbstractAppendable;
import de.micromata.genome.gwiki.utils.DiffBuilder;
import de.micromata.genome.gwiki.utils.DiffLine;
import de.micromata.genome.gwiki.utils.DiffSet;
import de.micromata.genome.gwiki.utils.PropDiffLine;
import de.micromata.genome.gwiki.utils.WordDiffBuilder;
import de.micromata.genome.gwiki.utils.DiffLine.DiffType;
import de.micromata.genome.gwiki.utils.DiffSet.DiffSetStatus;
import de.micromata.genome.util.types.Pair;

/**
 * ActionBean to compare two wiki elements.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiCompareActionBean extends ActionBeanBase
{
  private String leftPageId;

  private String rightPageId;

  private GWikiElement leftEl;

  private GWikiElement rightEl;

  private Map<String, DiffSet> diffSets = Collections.emptyMap();

  private String backUrl;

  private boolean fullDiff;

  protected DiffSet buildPartDiffset(String partName, GWikiArtefakt< ? > la, GWikiArtefakt< ? > ra)
  {
    if (la == null) {
      return new DiffSet(DiffSetStatus.NoLeft);
    }
    if (ra == null) {
      return new DiffSet(DiffSetStatus.NoRight);
    }
    if (la.getClass() != ra.getClass()) {
      return new DiffSet(DiffSetStatus.Incompatible);
    }
    if (la instanceof GWikiTextArtefaktBase< ? >) {
      String lt = ((GWikiTextArtefaktBase< ? >) la).getStorageData();
      String rt = ((GWikiTextArtefaktBase< ? >) ra).getStorageData();
      return new DiffBuilder().parse(lt, rt);
    } else if (la instanceof GWikiPropsArtefakt) {
      GWikiPropsArtefakt lp = (GWikiPropsArtefakt) la;
      GWikiPropsArtefakt rp = (GWikiPropsArtefakt) ra;
      Map<String, String> rm = rp.getStorageData();
      Map<String, String> lm = lp.getStorageData();
      if (partName.equals("Settings") == true) {
        rm.remove(GWikiPropKeys.PAGEID);
        lm.remove(GWikiPropKeys.PAGEID);
      }
      return new DiffBuilder().parse(lm, rm);
    } else {
      return new DiffSet(DiffSetStatus.Unsupported);
    }
  }

  protected Map<String, DiffSet> buildElDiffset()
  {
    Map<String, GWikiArtefakt< ? >> la = new TreeMap<String, GWikiArtefakt< ? >>();
    Map<String, GWikiArtefakt< ? >> ra = new TreeMap<String, GWikiArtefakt< ? >>();
    leftEl.collectParts(la);
    rightEl.collectParts(ra);
    Set<String> partNames = new TreeSet<String>();
    partNames.addAll(la.keySet());
    partNames.addAll(ra.keySet());
    Map<String, DiffSet> ret = new TreeMap<String, DiffSet>();
    for (String pn : partNames) {
      ret.put(pn, buildPartDiffset(pn, la.get(pn), ra.get(pn)));
    }
    return ret;
  }

  protected Pair<String, String> getLineDiff(String left, String right)
  {
    WordDiffBuilder diffBuilder = new WordDiffBuilder();
    DiffSet df = diffBuilder.parse(left, right);
    StringBuilder sb = new StringBuilder();
    DiffType lastDiffType = null;
    for (DiffLine dl : df.getLines()) {
      DiffType dt = dl.getDiffType();
      String ls = esc(dl.getLeft());
      if (dt == lastDiffType) {
        sb.append(ls);
        continue;
      }
      switch (dt) {
        case LeftNew:
        case Differ:
          sb.append("<u>");
          sb.append(ls);
          break;
        case RightNew:
          sb.append("<del>");
          sb.append(ls);
          break;
        case Equal:
          if (lastDiffType == null) {
            sb.append(ls);
            break;
          }
          switch (lastDiffType) {
            case LeftNew:
            case Differ:
              sb.append("</u>");
              break;
            case RightNew:
              sb.append("</del>");
              break;
          }
          sb.append(ls);
          break;
      }
      lastDiffType = dt;
    }
    if (lastDiffType != null) {
      switch (lastDiffType) {
        case LeftNew:
        case Differ:
          sb.append("</u>");
          break;
        case RightNew:
          sb.append("</del>");
          break;
      }
    }
    String leftC = sb.toString();
    sb = new StringBuilder();
    lastDiffType = null;
    for (DiffLine dl : df.getLines()) {
      DiffType dt = dl.getDiffType();
      String rs = esc(dl.getRight());
      if (dt == lastDiffType) {
        sb.append(rs);
        continue;
      }
      switch (dt) {
        case RightNew:
        case Differ:
          sb.append("<del>");
          sb.append(rs);
          break;
        case LeftNew:
          sb.append("<del>");
          sb.append(rs);
          break;
        case Equal:
          if (lastDiffType == null) {
            sb.append(rs);
            break;
          }
          switch (lastDiffType) {
            case LeftNew:
            case Differ:
              sb.append("</del>");
              break;
            case RightNew:
              sb.append("</u>");
              break;
          }
          sb.append(rs);
          break;
      }
      lastDiffType = dt;
    }
    if (lastDiffType != null) {
      switch (lastDiffType) {
        case LeftNew:
        case Differ:
          sb.append("</del>");
          break;
        case RightNew:
          sb.append("</u>");
          break;
      }
    }
    String rightC = sb.toString();
    return Pair.make(leftC, rightC);
  }

  public void renderCompareDiffSet(AbstractAppendable sb, DiffSet diffset)
  {
    // fullDiff = true;

    sb.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\">\n");
    boolean isPropertyDiff = false;
    if (diffset.getLines().size() > 0 && diffset.getLines().get(0) instanceof PropDiffLine) {
      isPropertyDiff = true;
    }
    sb.append("<tr><th>&nbsp;</th><th>&nbsp;</th><th>");
    if (isPropertyDiff == true) {
      sb.append("&nbsp;</th><th>");
    }
    sb.append(wikiContext.getUserDateString(leftEl.getElementInfo().getModifiedAt())).append(" by ").append(
        leftEl.getElementInfo().getModifiedBy()).append("</th><th>&nbsp;</th><th>") //
        .append(wikiContext.getUserDateString(rightEl.getElementInfo().getModifiedAt())).append(" by ").append(
            rightEl.getElementInfo().getModifiedBy()) //
        .append("</th></tr>\n");
    ;

    DiffType lastDiffType = null;
    for (DiffLine line : diffset.getLines()) {

      String leftIndex = "" + (line.getLeftIndex() + 1);
      String rightIndex = "" + (line.getRightIndex() + 1);
      if (line instanceof PropDiffLine) {
        leftIndex = ((PropDiffLine) line).getKey();
        rightIndex = "&nbsp;";
      }
      DiffType dt = line.getDiffType();
      String frame = " style=\"border-width:1px;border-left-style:solid;border-right-style:solid;border-bottom-style:none;border-top-style:solid; \"";
      if (lastDiffType == dt) {
        frame = " style=\"border-width:1px;border-left-style:solid;border-right-style:solid;border-bottom-style:none;border-top-style:none; \"";
      }
      switch (dt) {
        case Differ:
          Pair<String, String> p = getLineDiff(line.getLeft(), line.getRight());
          sb.append("<tr><td ").append(frame).append(">")
              //
              .append("&lt;&gt;</td><td ").append(frame).append(">")
              //
              .append(leftIndex)
              //
              .append("</td><td bgcolor='#FFAAFF' ").append(frame).append(">").append(p.getFirst()).append("</td><td ").append(frame)
              .append(">")//
              .append(rightIndex).append("</td><td bgcolor='#FFAAFF' ").append(frame).append(">")//
              .append(p.getSecond()).append("</td></tr>\n");
          break;
        case Equal:
          if (fullDiff == false) {
            break;
          }
          sb.append("<tr><td ").append(frame).append(">") //
              .append("==</td><td ").append(frame).append(">") //
              .append(leftIndex)//
              .append("</td><td ").append(frame).append(">").append(esc(line.getLeft())).append("</td><td ").append(frame).append(">")//
              .append(rightIndex).append("</td><td ").append(frame).append(">")//
              .append(esc(line.getRight())).append("</td></tr>\n");
          break;
        case LeftNew:

          sb.append("<tr><td ").append(frame).append(">")
              //
              .append("&gt;</td><td ").append(frame).append(">")
              //
              .append(leftIndex)
              //
              .append("</td><td bgcolor='#77FF77' ").append(frame).append(">").append(esc(line.getLeft())).append("</td><td ")
              .append(frame).append(">")//
              .append("&nbsp").append("</td><td ").append(frame).append(">")//
              .append("&nbsp").append("</td></tr>\n");
          break;
        case RightNew:
          sb.append("<tr><td ").append(frame).append(">")//
              .append("&lt;</td><td ").append(frame).append(">") //
              .append("&nbsp")//
              .append("</td><td ").append(frame).append(">").append("&nbsp").append("</td><td ").append(frame).append(">")//
              .append(rightIndex).append("</td><td ").append(frame).append(">")//
              .append(esc(line.getRight())).append("</td></tr>\n");
          break;
      }
      lastDiffType = dt;
    }

    sb.append("</table>\n");

  }

  public void renderDiffSet(String name, DiffSet diffset)
  {
    AbstractAppendable sb = wikiContext;

    switch (diffset.getStatus()) {
      case Incompatible:
        sb.append("<h3>").append(esc(name)).append("</h3>\n");
        sb.append(translate("gwiki.page.edit.Compare.message.cannotcompare"));
        break;
      case Unsupported:
        // sb.append("Cannot compare because format ist not supported");
        break;
      case NoLeft:
      case NoRight:
      case Compare:
        if (isDifferent(diffset)) {
          sb.append("<h3>").append(esc(name)).append("</h3>\n");
          renderCompareDiffSet(sb, diffset);
        } else {
          sb.append("<h3>").append(translate("gwiki.page.edit.ComparePages.noChange"));
          sb.append(" ").append(esc(name)).append("</h3>\n");
        }
        break;
    }
  }

  private boolean isDifferent(DiffSet diffset)
  {
    for (DiffLine line : diffset.getLines()) {
      if (line.getDiffType() != DiffType.Equal) {
        return true;
      }
    }
    return false;
  }

  public void doRenderDiffSets()
  {
    for (Map.Entry<String, DiffSet> me : diffSets.entrySet()) {
      renderDiffSet(me.getKey(), me.getValue());
    }
  }

  public Object onInit()
  {
    if (StringUtils.isEmpty(leftPageId) == true || StringUtils.isEmpty(rightPageId) == true) {
      wikiContext.addValidationError("gwiki.page.edit.Compare.message.leftorrightpageidnotset");
      return null;
    }
    leftEl = wikiContext.getWikiWeb().findElement(leftPageId);
    rightEl = wikiContext.getWikiWeb().findElement(rightPageId);
    if (leftEl == null) {
      wikiContext.addValidationError("gwiki.page.edit.Compare.message.leftpageidnotfound", leftPageId);
    }
    if (rightEl == null) {
      wikiContext.addValidationError("gwiki.page.edit.Compare.message.rightpageidnotfound", rightPageId);
    }
    if (wikiContext.hasValidationErrors() == true) {
      return null;
    }
    if (StringUtils.equals(rightEl.getElementInfo().getProps().getStringValue(GWikiPropKeys.WIKIMETATEMPLATE), leftEl.getElementInfo()
        .getProps().getStringValue(GWikiPropKeys.WIKIMETATEMPLATE)) == false) {
      wikiContext.addValidationError("gwiki.page.edit.Compare.message.canonlycompareequalmetas", leftEl.getElementInfo().getMetaTemplate()
          .getPageId(), leftEl.getElementInfo().getMetaTemplate().getPageId());
      return null;
    }
    diffSets = buildElDiffset();
    return null;

  }

  public String getLeftPageId()
  {
    return leftPageId;
  }

  public void setLeftPageId(String leftPageId)
  {
    this.leftPageId = leftPageId;
  }

  public String getRightPageId()
  {
    return rightPageId;
  }

  public void setRightPageId(String rightPageId)
  {
    this.rightPageId = rightPageId;
  }

  public GWikiElement getLeftEl()
  {
    return leftEl;
  }

  public void setLeftEl(GWikiElement leftEl)
  {
    this.leftEl = leftEl;
  }

  public GWikiElement getRightEl()
  {
    return rightEl;
  }

  public void setRightEl(GWikiElement rightEl)
  {
    this.rightEl = rightEl;
  }

  public Map<String, DiffSet> getDiffSets()
  {
    return diffSets;
  }

  public void setDiffSets(Map<String, DiffSet> diffSets)
  {
    this.diffSets = diffSets;
  }

  public String getBackUrl()
  {
    return backUrl;
  }

  public void setBackUrl(String backUrl)
  {
    this.backUrl = backUrl;
  }

  public boolean isFullDiff()
  {
    return fullDiff;
  }

  public void setFullDiff(boolean fullDiff)
  {
    this.fullDiff = fullDiff;
  }
}
