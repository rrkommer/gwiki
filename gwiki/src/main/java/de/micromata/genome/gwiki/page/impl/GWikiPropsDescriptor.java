/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   27.10.2009
// Copyright Micromata 27.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A descriptor bean for properties.
 * 
 * @author roger
 * 
 */
public class GWikiPropsDescriptor implements Serializable
{

  private static final long serialVersionUID = -3438528857386553181L;

  public List<GWikiPropsDescriptorValue> descriptors = new ArrayList<GWikiPropsDescriptorValue>();

  public List<GWikiPropsDescriptorValue> getDescriptors()
  {
    return descriptors;
  }

  public void setDescriptors(List<GWikiPropsDescriptorValue> descriptors)
  {
    this.descriptors = descriptors;
  }

}
