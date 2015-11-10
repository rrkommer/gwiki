////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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
import java.util.List;

/**
 * A descriptor bean for properties.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPropsDescriptor implements Serializable
{

  private static final long serialVersionUID = -3438528857386553181L;

  private List<GWikiPropsGroupDescriptor> groups = new ArrayList<GWikiPropsGroupDescriptor>();

  public List<GWikiPropsDescriptorValue> descriptors = new ArrayList<GWikiPropsDescriptorValue>();

  public List<GWikiPropsDescriptorValue> getDescriptors()
  {
    return descriptors;
  }

  public void setDescriptors(List<GWikiPropsDescriptorValue> descriptors)
  {
    this.descriptors = descriptors;
  }

  public List<GWikiPropsGroupDescriptor> getGroups()
  {
    return groups;
  }

  public void setGroups(List<GWikiPropsGroupDescriptor> groups)
  {
    this.groups = groups;
  }

}
