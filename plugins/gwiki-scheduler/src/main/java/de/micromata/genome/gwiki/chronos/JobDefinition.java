/////////////////////////////////////////////////////////////////////////////
//
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
