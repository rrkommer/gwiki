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

package de.micromata.genome.gwiki.model;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.ClassUtils;

/**
 * GWikiSchedulerProvider using a transient thread.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiStandardSchedulerProvider extends GWikiSchedulerProviderBase implements RejectedExecutionHandler
{

  class SchedulerThread extends Thread
  {
    GWikiSchedulerProvider schedulerProvider;

    GWikiSchedulerJob job;

    Map<String, String> args;

    public SchedulerThread(GWikiSchedulerProvider schedulerProvider, GWikiSchedulerJob job, Map<String, String> args)
    {
      super("gwikithread");
      setPriority(MIN_PRIORITY);
      this.schedulerProvider = schedulerProvider;
      this.job = job;
      this.args = args;
    }

    @Override
    public void run()
    {
      try {
        job.call(args);
      } finally {
        synchronized (schedulerProvider) {
          thread = null;
        }

      }
    }
  }

  private Thread thread = null;

  private ThreadPoolExecutor executor;

  private BlockingQueue<Runnable> queue;

  private int maxThreadCount = 2;

  public GWikiStandardSchedulerProvider()
  {
  }

  public synchronized ThreadPoolExecutor getExecutor()
  {
    if (executor != null) {
      return executor;
    }
    queue = new LinkedBlockingQueue<Runnable>();
    executor = new ThreadPoolExecutor(maxThreadCount, maxThreadCount, 60, TimeUnit.SECONDS, queue, this);
    return executor;

  }

  public synchronized boolean execAsyncMultiple(final GWikiContext wikiContext, final Class< ? extends GWikiSchedulerJob> callback,
      final Map<String, String> args)
  {
    getExecutor().submit(new Callable<Void>() {

      public Void call() throws Exception
      {
        ClassUtils.createDefaultInstance(callback).call(args);
        return null;
      }
    });
    return true;
  }

  public synchronized boolean execAsyncOne(GWikiContext wikiContext, final Class< ? extends GWikiSchedulerJob> callback,
      final Map<String, String> args)
  {

    if (thread != null) {
      wikiContext.addSimpleValidationError("A Job is already running: " + thread.getClass().getName());
      return false;
    }
    prepareContext(wikiContext, args);
    thread = new SchedulerThread(this, ClassUtils.createDefaultInstance(callback), args);
    thread.start();
    return false;
  }

  public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
  {

  }

}
