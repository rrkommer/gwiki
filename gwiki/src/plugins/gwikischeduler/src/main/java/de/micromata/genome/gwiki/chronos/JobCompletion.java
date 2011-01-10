/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: JobCompletion.java,v $
//
// Project   jchronos
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   04.01.2007
// Copyright Micromata 04.01.2007
//
// $Id: JobCompletion.java,v 1.2 2007/02/25 13:38:59 roger Exp $
// $Revision: 1.2 $
// $Date: 2007/02/25 13:38:59 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

public enum JobCompletion
{
  SERVICE_UNAVAILABLE, //
  EXCEPTION, //
  EXPECTED_RETRY, //
  THREAD_POOL_EXHAUSTED, //
  JOB_COMPLETED //
  ;
}
