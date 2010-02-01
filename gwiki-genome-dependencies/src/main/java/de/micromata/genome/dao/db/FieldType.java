/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   16.02.2008
// Copyright Micromata 16.02.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.dao.db;

/**
 * 
 * @author roger@micromata.de
 * 
 */
public interface FieldType
{
  String name();

  int getSqlType();

  int getMaxSize();

  boolean isUnicode();
}
