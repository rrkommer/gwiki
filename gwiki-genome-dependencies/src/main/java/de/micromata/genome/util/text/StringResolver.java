/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   09.01.2008
// Copyright Micromata 09.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.text;

/**
 * Sollte selbsterklärend sein ...
 * 
 * @author noodles@micromata.de
 */
public interface StringResolver
{

  /**
   * Liefert den Wert für den Platzhalter.
   * 
   * @param placeholder Nie <code>null</code>
   * @return Nie <code>null</code>, ggf. eine RuntimeException
   */
  String resolve(String placeholder);

}