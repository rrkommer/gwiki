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
package de.micromata.genome.gwiki.pagelifecycle_1_0.model;

/**
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public interface PlcConstants
{
  public final static String DRAFT_ID = "_DRAFT";

  /**
   * location of filestats file in a tenant
   */
  public static final String FILE_STATS_LOCATION = "admin/branch/intern/BranchFileStats";

  public static final String BRANCH_INFO_LOCATION = "admin/branch/intern/BranchInfoElement";

  public static final String BRANCH_INFO_TEMPLATE_ID = "admin/templates/intern/GWikiBranchInfoElementTemplate";

  public static final String FILESTATS_TEMPLATE_ID = "admin/templates/intern/GWikiBranchFileStatsTemplate";

  /* Prop key of Branch Info element */
  public static final String BRANCH_INFO_BRANCH_ID = "BRANCH_ID";

  public static final String BRANCH_INFO_RELEASE_END_DATE = "RELEASE_END_DATE";

  public static final String BRANCH_INFO_DESCRIPTION = "DESCRIPTION";

  public static final String BRANCH_INFO_RELEASE_DATE = "RELEASE_DATE";

  public static final String BRANCH_INFO_BRANCH_STATE = "BRANCH_STATE";

}
