/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   03.11.2009
// Copyright Micromata 03.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.actionbean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActionMessages extends HashMap<String, List<ActionMessage>>
{

  private static final long serialVersionUID = -5958445295370932524L;

  public void put(String field, ActionMessage msg)
  {
    List<ActionMessage> am = get(field);
    if (am == null) {
      am = new ArrayList<ActionMessage>();
      super.put(field, am);
    }
    am.add(msg);
  }
}
