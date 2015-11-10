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
 * Pagelifeycycle specific rights
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public enum GWikiPlcRights
{
  /** right to view plc menu */
  PLC_VIEW_MENU,
  
  /** right to edit an article */
  PLC_EDIT_ARTICLE,
  
  /** right to send a draft article to a reviewer */
  PLC_APPROVE_DRAFT_ARTICLE,

  /** right to reject a draft article */
  PLC_REJECT_DRAFT_ARTICLE,
  
  /** right to approve a article */
  PLC_APPROVE_ARTICLE,
  
  /** right to reject a article */
  PLC_REJECT_ARTICLE,

  /** right to release an article */
  PLC_RELEASE_ARTICLE,

  /** right to create a new branch */
  PLC_CREATE_BRANCH,
  
  /** right to view all branches */
  PLC_VIEW_ALL_BRANCHES,
  
  /** right to assign objects to a branch */
  PLC_ASSIGN_BRANCH,
  
  /** right to release a branch */
  PLC_RELEASE_BRANCH;
}
