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
package de.micromata.genome.gwiki.admintools_1_0.pmprofiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.micromata.genome.util.types.Holder;
import de.micromata.genome.util.types.Pair;

/**
 * Poor mans Profiler.
 * 
 * In a thread every 5 milliseconds a Stacktrace from all threads are taken and put it into a statistics.
 * 
 * This profiling does not measure CPU usage, but where the code is staying.
 * 
 * Start this thread from your application or unit test.
 * 
 * At the end of the call stopAndWait();
 * 
 * call dump() to dump performance statistics to System.out.
 * 
 * Study code and/or try it for more information.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de/artefaktur.com)
 * 
 */
public class StacktracePerf extends Thread
{

  private long sleeptime = 5;

  private volatile boolean stop = false;

  private volatile boolean pause = false;

  public static class StackTracePart
  {
    private int visitCounter = 1;

    private StackTracePart parent;

    private List<StackTracePart> childs = new ArrayList<StackTracePart>();

    private StackTraceElement stackTraceElement;

    public StackTracePart(StackTracePart parent, StackTraceElement stackTraceElement)
    {
      this.parent = parent;
      this.stackTraceElement = stackTraceElement;
    }

    public StackTracePart(StackTracePart parent, StackTraceElement[] stackTraceElements, int offset)
    {
      this(parent, stackTraceElements[stackTraceElements.length - offset]);
      if (stackTraceElements.length > offset + 1) {
        StackTracePart nc = new StackTracePart(this, stackTraceElements, ++offset);
        childs.add(nc);
      }
    }

    public void visit(StackTraceElement[] stackTraceElements, int offset)
    {
      ++visitCounter;
      if (stackTraceElements.length <= offset) {
        return;
      }
      StackTraceElement nc = stackTraceElements[stackTraceElements.length - offset];
      for (StackTracePart c : childs) {
        if (c.getStackTraceElement().equals(nc) == true) {
          c.visit(stackTraceElements, ++offset);
          return;
        }
      }
      StackTracePart ncc = new StackTracePart(this, stackTraceElements, offset);
      childs.add(ncc);
    }

    public void dump(Appendable sb, String indend) throws IOException
    {
      sb.append(indend).append(stackTraceElement.toString()).append(" => " + visitCounter).append("\n");
      if (childs.isEmpty() == true) {
        return;
      }
      Collections.sort(childs, new Comparator<StackTracePart>() {

        public int compare(StackTracePart o1, StackTracePart o2)
        {
          if (o1.getVisitCounter() == o2.getVisitCounter()) {
            return 0;
          }
          if (o1.getVisitCounter() < o2.getVisitCounter()) {
            return 1;
          }
          return -1;
        }
      });
      indend += " ";
      for (StackTracePart sp : childs) {
        sp.dump(sb, indend);
      }
    }

    public StackTracePart getParent()
    {
      return parent;
    }

    public void setParent(StackTracePart parent)
    {
      this.parent = parent;
    }

    public StackTraceElement getStackTraceElement()
    {
      return stackTraceElement;
    }

    public void setStackTraceElement(StackTraceElement stackTraceElement)
    {
      this.stackTraceElement = stackTraceElement;
    }

    public int getVisitCounter()
    {
      return visitCounter;
    }

    public void setVisitCounter(int visitCounter)
    {
      this.visitCounter = visitCounter;
    }

  }

  Map<StackTraceElement, Holder<Integer>> stm = new HashMap<StackTraceElement, Holder<Integer>>();

  Map<StackTraceElement, StackTracePart> trees = new HashMap<StackTraceElement, StackTracePart>();

  public void collect(Thread thread, StackTraceElement[] se)
  {
    if (se.length == 0) {
      return;
    }
    for (StackTraceElement el : se) {
      Holder<Integer> i = stm.get(el);
      if (i == null) {
        stm.put(el, new Holder<Integer>(1));
      } else {
        i.set(i.get() + 1);
      }
    }

    StackTracePart part = trees.get(se[se.length - 1]);
    if (part == null) {
      part = new StackTracePart(null, se, 1);
      trees.put(se[se.length - 1], part);
    } else {
      part.visit(se, 1);
    }
  }

  public void collect()
  {
    Map<Thread, StackTraceElement[]> ms = Thread.getAllStackTraces();
    for (Map.Entry<Thread, StackTraceElement[]> e : ms.entrySet()) {
      if (e.getKey() == this) {
        continue;
      }
      collect(e.getKey(), e.getValue());

    }
  }

  public void dump()
  {
    try {
      dumpStList(System.out);
      dumpStackTracePart(System.out);
    } catch (IOException ex) {

    }
  }

  public void dumpStackTracePart(Appendable sb) throws IOException
  {
    for (Map.Entry<StackTraceElement, StackTracePart> e : trees.entrySet()) {
      sb.append("\nThread Tree\n");
      e.getValue().dump(sb, " ");
    }
  }

  public void dumpStList(Appendable sb) throws IOException
  {
    List<Pair<Integer, StackTraceElement>> sl = new ArrayList<Pair<Integer, StackTraceElement>>(stm.size());
    for (Map.Entry<StackTraceElement, Holder<Integer>> e : stm.entrySet()) {
      sl.add(Pair.make(e.getValue().get(), e.getKey()));
    }
    Collections.sort(sl, new Comparator<Pair<Integer, StackTraceElement>>() {

      public int compare(Pair<Integer, StackTraceElement> o1, Pair<Integer, StackTraceElement> o2)
      {
        if (o1.getFirst().equals(o2.getFirst()) == true) {
          return 0;
        }
        if (o1.getFirst() < o2.getFirst()) {
          return 1;
        }
        return -1;
      }
    });
    sb.append("\n\nAll Method:\n");
    for (Pair<Integer, StackTraceElement> p : sl) {
      sb.append(p.getFirst().toString()).append(" ").append(p.getSecond().toString()).append("\n");
    }

  }

  public void run()
  {
    while (stop == false) {
      if (pause == false) {
        collect();
      }
      try {
        Thread.sleep(sleeptime);
      } catch (InterruptedException ex) {
        break;
      }
    }
  }

  public void setPause(boolean pause)
  {
    if (pause == true && this.pause == false) {
      suspend();
    } else if (pause == false && this.pause == true) {
      resume();
    }
    this.pause = pause;

  }

  public void stopAndWait()
  {
    setStop(true);
    setPause(false);
    try {
      this.join();
    } catch (InterruptedException ex) {
      throw new RuntimeException(ex);
    }
  }

  public boolean isStop()
  {
    return stop;
  }

  public void setStop(boolean stop)
  {
    this.stop = stop;
  }

  public long getSleeptime()
  {
    return sleeptime;
  }

  public void setSleeptime(long sleeptime)
  {
    this.sleeptime = sleeptime;
  }

  public boolean isPause()
  {
    return pause;
  }

}