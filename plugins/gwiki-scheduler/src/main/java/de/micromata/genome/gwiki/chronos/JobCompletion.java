//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

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
