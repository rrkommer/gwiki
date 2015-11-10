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

////////////////////////////////////////////////////////////////////////////


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

////////////////////////////////////////////////////////////////////////////


package de.micromata.genome.gwiki;

import junit.framework.TestCase;
import de.micromata.genome.gwiki.model.GWikiWeb;

public class GWikiPageCacheTest extends TestCase
{
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
  }

  public static boolean someFailed = false;

  public static int loopCount = 100;

  public static class AccessThread extends Thread
  {

    @Override
    public void run()
    {
      try {
        GWikiTestBuilder tb = new GWikiTestBuilder();
        tb.serve("index");
        tb.login("gwikisu", "gwiki").followRedirect();
        // tb.dumpLastResponseToOut();
        for (int i = 0; i < loopCount; ++i) {
          tb.serve("index");
        }
      } catch (RuntimeException ex) {
        someFailed = true;
        throw ex;
      }
    }

  }

  public void testSingle()
  {
    AccessThread nax = new AccessThread();
    nax.run();
  }

  public void testThreaded()
  {
    someFailed = false;
    long start = System.currentTimeMillis();
    AccessThread nax = new AccessThread();
    nax.run();
    long end = System.currentTimeMillis();
    System.out.println("Served " + loopCount + " index pages in: " + (end - start) + " ms");
    int threadCount = 10;
    Thread[] tl = new Thread[threadCount];
    for (int i = 0; i < threadCount; ++i) {
      tl[i] = new AccessThread();
    }
    start = System.currentTimeMillis();
    for (int i = 0; i < threadCount; ++i) {
      tl[i].start();
    }
    for (int i = 0; i < threadCount; ++i) {
      try {
        tl[i].join();
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
    }
    end = System.currentTimeMillis();
    System.out.println("Served " + (loopCount * threadCount) + " index pages in " + threadCount + " threads in: " + (end - start) + " ms");
    assertFalse(someFailed);
  }

  public static boolean stopResetPageThread = false;

  public static class ResetPageThread extends Thread
  {
    @Override
    public void run()
    {
      while (stopResetPageThread) {
        GWikiTestBuilder tb = new GWikiTestBuilder();
        tb.serve("index");
        GWikiWeb.get().reloadPage("index");
        try {
          sleep(100);
        } catch (InterruptedException ex) {
          throw new RuntimeException(ex);
        }
      }
    }
  }

  public void testThreadedWithCacheDisturb()
  {
    stopResetPageThread = false;
    someFailed = false;
    long start = System.currentTimeMillis();
    AccessThread nax = new AccessThread();
    nax.run();
    long end = System.currentTimeMillis();
    System.out.println("Served " + loopCount + " index pages in: " + (end - start) + " ms");
    int threadCount = 10;
    Thread[] tl = new Thread[threadCount];
    for (int i = 0; i < threadCount; ++i) {
      tl[i] = new AccessThread();
    }
    ResetPageThread rpt = new ResetPageThread();
    start = System.currentTimeMillis();
    for (int i = 0; i < threadCount; ++i) {
      tl[i].start();
    }
    rpt.start();
    try {
      for (int i = 0; i < threadCount; ++i) {
        tl[i].join();
      }
      stopResetPageThread = true;
      rpt.join();
    } catch (InterruptedException ex) {
      throw new RuntimeException(ex);
    }
    end = System.currentTimeMillis();
    System.out.println("Served with cleared " + (loopCount * threadCount) + " index pages in " + threadCount + " threads in: " + (end - start) + " ms");
    assertFalse(someFailed);
  }
}
