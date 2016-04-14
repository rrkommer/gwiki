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

package de.micromata.genome.gwiki.admintools_1_0.pmprofiler;

import java.io.IOException;

import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class PmProfilerActionBean extends ActionBeanBase
{
  private int millisecondsToSleep = 5;

  private static StacktracePerf thread = null;

  private boolean dumped = false;

  private boolean dumpThreadList;

  private boolean dumpStdList;

  private boolean dumpStacktrace;

  public Object onInit()
  {
    return null;
  }

  public Object onStart()
  {
    thread = new StacktracePerf();
    thread.setSleeptime(millisecondsToSleep);
    thread.start();
    return null;
  }

  public Object onPause()
  {
    thread.setPause(true);
    return null;
  }

  public Object onResume()
  {
    thread.setPause(false);
    return null;
  }

  public Object onStop()
  {
    thread.stopAndWait();
    return null;
  }

  public Object onDump()
  {
    dumped = true;
    return null;
  }

  public Object onClear()
  {
    thread = null;
    return null;
  }

  public Object onDumpStacktraces()
  {
    dumpStacktrace = true;
    dumped = true;
    return null;
  }

  public void dumpThreads()
  {
    if (dumpStacktrace == true) {
      StringBuilder sb = new StringBuilder();
      try {
        new DumpStacktraces().dumpStackTracesToLog(sb);
        wikiContext.append(sb.toString());
      } catch (IOException ex) {
        wikiContext.addSimpleValidationError("IO Error in dumping: " + ex.getMessage());
      }
      return;
    }
    if (isActive() == false) {
      wikiContext.addSimpleValidationError("Profile did not run");
      return;
    }
    if (isRunning() == true) {
      wikiContext.addSimpleValidationError("Profiler is still running");
      return;
    }
    StringBuilder sb = new StringBuilder();

    try {
      if (dumpStdList == true) {
        thread.dumpStList(sb);
        sb.append("\n\n");
      }

      if (dumpThreadList == true) {
        thread.dumpStackTracePart(sb);
      }
    } catch (IOException ex) {
      wikiContext.addSimpleValidationError("IO Error in dumping: " + ex.getMessage());
    }
    wikiContext.append(sb.toString());
  }

  public boolean isCanStart()
  {
    return isActive() == false || isRunning() == false;
  }

  public boolean isCanPause()
  {
    return isRunning() == true;
  }

  public boolean isCanStop()
  {
    return isRunning() == true;
  }

  public boolean isCanResume()
  {
    return isActive() == true && isPausing() == true;
  }

  public boolean isActive()
  {
    return thread != null;
  }

  public boolean isPausing()
  {
    return isActive() == true && thread.isPause() == true && thread.isStop() == false;
  }

  public boolean isRunning()
  {
    return isActive() == true && (thread.isPause() == false && thread.isStop() == false);
  }

  public int getMillisecondsToSleep()
  {
    return millisecondsToSleep;
  }

  public void setMillisecondsToSleep(int millisecondsToSleep)
  {
    this.millisecondsToSleep = millisecondsToSleep;
  }

  public boolean isDumped()
  {
    return dumped;
  }

  public void setDumped(boolean dumped)
  {
    this.dumped = dumped;
  }

  public boolean isDumpThreadList()
  {
    return dumpThreadList;
  }

  public void setDumpThreadList(boolean dumpThreadList)
  {
    this.dumpThreadList = dumpThreadList;
  }

  public boolean isDumpStdList()
  {
    return dumpStdList;
  }

  public void setDumpStdList(boolean dumpStdList)
  {
    this.dumpStdList = dumpStdList;
  }

  public boolean isDumpStacktrace()
  {
    return dumpStacktrace;
  }

  public void setDumpStacktrace(boolean dumpStacktrace)
  {
    this.dumpStacktrace = dumpStacktrace;
  }
}
