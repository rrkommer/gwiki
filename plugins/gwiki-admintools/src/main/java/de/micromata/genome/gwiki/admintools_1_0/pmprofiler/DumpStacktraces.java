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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class DumpStacktraces
{
  public void dumpStack(Map.Entry<ThreadGroup, List<Map.Entry<Thread, StackTraceElement[]>>> tme, String indent, Appendable sb)
      throws IOException
  {
    ThreadGroup tg = tme.getKey();
    sb.append("\n").append(indent).append("ThreadGroup: ").append(tg.getName());
    indent += "  ";
    sb.append("\n").append(indent).append(indent).append("class: ").append(tg.getClass().getName());
    indent += "  ";
    for (Map.Entry<Thread, StackTraceElement[]> me : tme.getValue()) {
      Thread t = me.getKey();

      ClassLoader contextClassLoader = t.getContextClassLoader();
      StringBuilder sb2 = new StringBuilder();

      String indent2 = indent + "    ";
      ClassLoader cl = contextClassLoader;
      sb2.append(cl).append("\n");
      if (cl != null) {
        cl = cl.getParent();
        while (cl != null) {
          sb2.append(indent2).append("pcl: ").append(cl).append("\n");
          if (cl == cl.getParent())
            break;
          cl = cl.getParent();
          indent2 += "  ";
        }
      }
      sb.append("\n").append(indent).append("Thread: ").append(Long.toString(t.getId()))//
          .append("\n").append(indent).append("  ").append("name: ").append(t.getName())//
          .append("\n").append(indent).append("  ").append("class: ").append(t.getClass().getName()) //
          // .append("\n group: ").append(t.getThreadGroup().getName())//
          .append("\n").append(indent).append("  ").append("prio: ").append(Integer.toString(t.getPriority())) //
          .append("\n").append(indent).append("  ").append("state: ").append(t.getState().toString()) //
          .append("\n").append(indent).append("  ").append("classLoader: ").append(sb2) //
          .append("\n").append(indent).append("  ").append("stack:\n");
      for (StackTraceElement se : me.getValue()) {
        sb.append(indent).append("    ").append(se.toString()).append("\n");
      }
    }

  }

  public void dumpStack(Map<ThreadGroup, List<Map.Entry<Thread, StackTraceElement[]>>> threadGroups, ThreadGroup parent, String indent,
      Appendable sb) throws IOException
  {
    for (Map.Entry<ThreadGroup, List<Map.Entry<Thread, StackTraceElement[]>>> tge : threadGroups.entrySet()) {
      if (tge.getKey().getParent() == parent) {
        dumpStack(tge, indent, sb);
        dumpStack(threadGroups, tge.getKey(), indent + "  ", sb);
      }
    }
  }

  public void dumpStackTracesToLog(Appendable sb) throws IOException
  {
    Map<ThreadGroup, List<Map.Entry<Thread, StackTraceElement[]>>> threadGroups = new HashMap<ThreadGroup, List<Map.Entry<Thread, StackTraceElement[]>>>();

    Map<Thread, StackTraceElement[]> allStacks = Thread.getAllStackTraces();
    for (Map.Entry<Thread, StackTraceElement[]> me : allStacks.entrySet()) {
      List<Map.Entry<Thread, StackTraceElement[]>> tl = threadGroups.get(me.getKey().getThreadGroup());
      if (tl == null) {
        tl = new ArrayList<Map.Entry<Thread, StackTraceElement[]>>();
        threadGroups.put(me.getKey().getThreadGroup(), tl);
      }
      tl.add(me);
    }

    dumpStack(threadGroups, null, "", sb);

  }
}
