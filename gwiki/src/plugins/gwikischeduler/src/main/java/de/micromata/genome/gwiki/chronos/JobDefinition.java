/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: JobDefinition.java,v $
//
// Project   jchronos
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   27.12.2006
// Copyright Micromata 27.12.2006
//
// $Id: JobDefinition.java,v 1.3 2007/02/16 14:31:34 hx Exp $
// $Revision: 1.3 $
// $Date: 2007/02/16 14:31:34 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

/**
 * Factory für die {@link FutureJob} Instanzen.
 * 
 * @author Wolfgang Jung (w.jung@micromata.de)
 * 
 */
public interface JobDefinition
{
  /**
   * Factorymethode
   * 
   * @return eine Instanz des Jos.
   */
  public FutureJob getInstance();
}
