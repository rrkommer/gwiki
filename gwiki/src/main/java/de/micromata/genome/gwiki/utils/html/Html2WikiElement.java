/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   29.12.2009
// Copyright Micromata 29.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.utils.html;

/**
 * Interface to a Wiki element used by the html 2 wiki filter
 * 
 * @author roger
 * 
 */
public interface Html2WikiElement
{
  public String name();

  public String getStart();

  public String getEnd();
}
