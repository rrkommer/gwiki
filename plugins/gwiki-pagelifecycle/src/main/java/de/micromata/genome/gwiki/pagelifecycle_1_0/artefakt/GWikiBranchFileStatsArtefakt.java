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
package de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiTextArtefaktBase;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.FileState;
import de.micromata.genome.util.text.PipeValueList;

/**
 * @author Stefan Stuetzer (s.stuetzer@micromata.de)
 */
public class GWikiBranchFileStatsArtefakt extends GWikiTextArtefaktBase<BranchFileStats>
{
  private static final long serialVersionUID = -6539670372439797336L;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiTextArtefaktBase#renderWithParts(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiPersistArtefakt#getFileSuffix()
   */
  public String getFileSuffix()
  {
    return ".txt";
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiArtefaktBase#getCompiledObject()
   */
  @Override
  public BranchFileStats getCompiledObject()
  {
    if (super.getCompiledObject() != null) {
      return super.getCompiledObject();
    }
    BranchFileStats fileStats = compileObject();
    setCompiledObject(fileStats);
    return fileStats;
  }

  /**
   * Compiles branchfilestats from file
   */
  private BranchFileStats compileObject()
  {
    if (StringUtils.isBlank(super.getStorageData()) == true) {
      return new BranchFileStats();
    }
    String[] lines = StringUtils.split(super.getStorageData(), "\n");
    BranchFileStats fileStats = new BranchFileStats();

    for (final String line : lines) {
      FileStatsDO fileStatsDO = new FileStatsDO();
      Map<String, String> map = PipeValueList.decode(line);

      fileStatsDO.setPageId(map.get(FileStatsDO.PAGE_ID));
      fileStatsDO.setFileState(FileState.valueOf(map.get(FileStatsDO.FILE_STATE)));
      fileStatsDO.setCreatedAt(map.get(FileStatsDO.CREATED_AT));
      fileStatsDO.setCreatedBy(map.get(FileStatsDO.CREATED_BY));
      fileStatsDO.setLastModifiedAt(map.get(FileStatsDO.MODIFIED_AT));
      fileStatsDO.setLastModifiedBy(map.get(FileStatsDO.MODIFIED_BY));
      fileStatsDO.setAssignedTo(map.get(FileStatsDO.ASSIGNED_TO));
      fileStatsDO.setStartAt(map.get(FileStatsDO.START_AT));
      fileStatsDO.setEndAt(map.get(FileStatsDO.END_AT));
      fileStatsDO.setPreviousAssignee(map.get(FileStatsDO.PREVIOUS_ASSIGNEE));
      String operatorsString = map.get(FileStatsDO.OPERATORS);
      String[] operatorArray = StringUtils.split(operatorsString, ",");
      if (operatorArray != null) {
        Set<String> operators = new HashSet<String>();
        for (String operator : operatorArray) {
          operators.add(operator); 
        }
        fileStatsDO.setOperators(operators);
      }
      fileStats.addFileStats(fileStatsDO);
    }
    return fileStats;
  }
}