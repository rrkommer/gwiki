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

package de.micromata.genome.gwiki.page.impl.wiki;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.logging.GWikiLogAttributeType;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroInfoBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroParamInfo;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroParamType;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiScriptMacro;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.GenomeAttributeType;
import de.micromata.genome.logging.LogAttribute;
import de.micromata.genome.logging.LogExceptionAttribute;

public class GWikiScriptMacroFactory implements GWikiMacroFactory, GWikiPropKeys, Serializable
{

  private static final long serialVersionUID = 396117543440444247L;

  private GWikiElementInfo elementInfo;

  public GWikiScriptMacroFactory(GWikiElementInfo elementInfo)
  {
    this.elementInfo = elementInfo;
  }

  @Override
  public String toString()
  {
    return "GWikiScriptMacro(" + elementInfo.getId() + ")";

  }

  @Override
  public GWikiMacro createInstance()
  {
    GWikiScriptMacro ma = new GWikiScriptMacro(this, GWikiWeb.get().getElement(elementInfo));
    return ma;
  }

  @Override
  public GWikiMacroInfo getMacroInfo()
  {
    GWikiMacroInfo defaultInfos = GWikiMacroFactory.super.getMacroInfo();
    ;
    String mi = elementInfo.getProps().getStringValue(MACRO_MACROINFO);
    if (StringUtils.isBlank(mi) == true) {
      return defaultInfos;
    }
    try {
      JsonObject jobj = Json.parse(mi).asObject();
      MacroInfoBean ret = new MacroInfoBean();
      ret.setInfo(jobj.getString("info", ""));

      ret.setHasBody(defaultInfos.hasBody());
      ret.setEvalBody(defaultInfos.evalBody());
      ret.setRteMacro(defaultInfos.isRteMacro());

      JsonValue pv = jobj.get("macroParams");
      if (pv.isArray() == true) {
        JsonArray jarr = pv.asArray();
        for (JsonValue pi : jarr) {
          JsonObject pinf = pi.asObject();
          MacroParamInfo paramInfo = new MacroParamInfo();
          paramInfo.setName(pinf.getString("name", "unkown"));
          paramInfo.setInfo(pinf.getString("info", ""));
          paramInfo.setRequired(pinf.getBoolean("required", false));
          paramInfo.setType(MacroParamType.valueOf(pinf.getString("type", MacroParamType.String.name())));
          JsonValue enums = pinf.get("enumValues");
          if (enums != null) {
            JsonArray ejarr = enums.asArray();
            for (JsonValue ea : ejarr) {
              paramInfo.getEnumValues().add(ea.asString());
            }
          }
          ret.getParamInfos().add(paramInfo);
        }
      }
      return ret;
    } catch (RuntimeException ex) {
      GLog.error(GWikiLogCategory.Wiki, "Invalid jspon found: " + ex.getMessage(),
          new LogAttribute(GWikiLogAttributeType.PageId, elementInfo.getId()),
          new LogAttribute(GenomeAttributeType.Miscellaneous, mi),
          new LogExceptionAttribute(ex));
      return defaultInfos;
    }

  }

  @Override
  public boolean evalBody()
  {
    return elementInfo.getProps().getBooleanValue(MACRO_EVAL_BODY);
  }

  @Override
  public boolean hasBody()
  {
    return elementInfo.getProps().getBooleanValue(MACRO_WITH_BODY);
  }

  @Override
  public boolean isRteMacro()
  {
    return false;
  }

  public GWikiElementInfo getElement()
  {
    return elementInfo;
  }

  public void setElement(GWikiElementInfo elementInfo)
  {
    this.elementInfo = elementInfo;
  }

}
