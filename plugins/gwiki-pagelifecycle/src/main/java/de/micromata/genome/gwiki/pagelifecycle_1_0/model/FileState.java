////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010 Micromata GmbH
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
 * Possible states of a content object during lifecycle
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public enum FileState
{
  DRAFT,
  
  TO_REVIEW,
  
  APPROVED_CHIEF_EDITOR,
  
  APPROVED_CONTENT_ADMIN;
  
  public String getName() {
    return name();
  }
}
