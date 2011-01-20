/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   05.02.2008
// Copyright Micromata 05.02.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi;

import java.util.concurrent.ThreadFactory;

import org.springframework.util.ClassUtils;

import de.micromata.genome.gwiki.chronos.logging.GLog;
import de.micromata.genome.gwiki.chronos.logging.GenomeLogCategory;

public class SchedulerThreadFactory implements ThreadFactory {
	private final Object monitor = new Object();

	private String threadGroupName;

	private ThreadGroup threadGroup;

	private String threadNamePrefix = "";

	private int threadPriority = Thread.NORM_PRIORITY;

	private boolean daemon = false;

	private int threadCount = 0;

	public Thread newThread(Runnable r) {
		return createThread(r);
	}

	public Thread createThread(Runnable runnable) {
		SchedulerThread thread = new SchedulerThread(getThreadGroup(),
				runnable, nextThreadName());
		thread.setPriority(getThreadPriority());
		thread.setDaemon(isDaemon());
		/**
		 * @logging
		 * @reason Scheduler startet einen Job
		 * @action Keine
		 */
		GLog.note(GenomeLogCategory.Scheduler, "Chronos; Create Thread: "
				+ thread);
		return thread;
	}

	protected String getDefaultThreadNamePrefix() {
		return ClassUtils.getShortName(getClass()) + "-";
	}

	/**
	 * Return the thread name to use for a newly created thread.
	 * <p>
	 * Default implementation returns the specified thread name prefix with an
	 * increasing thread count appended: for example,
	 * "SimpleAsyncTaskExecutor-0".
	 * 
	 * @see #getThreadNamePrefix()
	 */
	protected String nextThreadName() {
		int threadNumber = 0;
		synchronized (this.monitor) {
			this.threadCount++;
			threadNumber = this.threadCount;
		}
		return getThreadNamePrefix() + threadNumber;
	}

	public String getThreadGroupName() {
		return threadGroupName;
	}

	public void setThreadGroupName(String threadGroupName) {
		this.threadGroupName = threadGroupName;
	}

	public String getThreadNamePrefix() {
		return threadNamePrefix;
	}

	public void setThreadNamePrefix(String threadNamePrefix) {
		this.threadNamePrefix = threadNamePrefix;
	}

	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}

	public void setThreadGroup(ThreadGroup threadGroup) {
		this.threadGroup = threadGroup;
	}

	public int getThreadPriority() {
		return threadPriority;
	}

	public void setThreadPriority(int threadPriority) {
		this.threadPriority = threadPriority;
	}

	public boolean isDaemon() {
		return daemon;
	}

	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

}
