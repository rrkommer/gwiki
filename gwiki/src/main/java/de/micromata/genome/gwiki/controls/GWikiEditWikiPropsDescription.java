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

package de.micromata.genome.gwiki.controls;

/**
 * TODO gwiki delete me.
 * 
 * @deprecated now implemented via MetaTemplates.
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@Deprecated
public enum GWikiEditWikiPropsDescription implements GWikiEditPropsDescription
{
  TYPE(false, "Type of Page"), //
  PARENTPAGE(false, "Parent page id"), //
  AUTH_VIEW(true, "Right to view page"), //
  AUTH_EDIT(true, "Right to edit page"), //
  CONTENTYPE(true, "Content type delivered by page"), //
  WIKITEMPLATE(true, "Template page id"), //
  WIKICONTROLERCLASS(true, "Controler page id"), //
  ;
  private boolean editable;

  private String description;

  private GWikiEditWikiPropsDescription(boolean editable, String description)
  {
    this.editable = editable;
    this.description = description;
  }

  public String getName()
  {
    return name();
  }

  public String getDescription()
  {
    return description;
  }

  public boolean isEditable()
  {
    return editable;
  }

}
