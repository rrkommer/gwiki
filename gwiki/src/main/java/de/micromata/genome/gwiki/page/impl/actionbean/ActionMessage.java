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

import java.io.Serializable;
import java.util.Locale;

public interface ActionMessage extends Serializable
{
  String getMessage(Locale locale);
}
