/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    michael@micromata.de
// Created   Feb 21, 2008
// Copyright Micromata Feb 21, 2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.db.spring;

/**
 * Common interface für Datenbank Tablen. Normal als ein Enum implementiert
 * @author michael@micromata.de
 *
 */
public interface DbTableIdentifier
{
  String name();
}