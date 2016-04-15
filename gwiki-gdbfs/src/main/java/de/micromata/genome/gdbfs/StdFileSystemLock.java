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

package de.micromata.genome.gdbfs;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * Implementa a lock on a operation system file system.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class StdFileSystemLock
{

  /**
   * The lock count.
   */
  private int lockCount;

  /**
   * The file.
   */
  private File file;

  /**
   * The channel.
   */
  private FileChannel channel;

  /**
   * The lock.
   */
  private FileLock lock;

  /**
   * Instantiates a new std file system lock.
   *
   * @param file the file
   */
  public StdFileSystemLock(File file)
  {
    this.file = file;
  }

  /**
   * Inc lock count.
   *
   * @return the int
   */
  public int incLockCount()
  {
    return ++lockCount;
  }

  /**
   * Dec lock count.
   *
   * @return the int
   */
  public int decLockCount()
  {
    return --lockCount;
  }

  /**
   * Aquire lock.
   *
   * @param timeOutMs the time out ms
   * @return true, if successful
   */
  public boolean aquireLock(long timeOutMs)
  {
    openLock();
    long start = System.currentTimeMillis();
    long end = start + timeOutMs;
    while (end > System.currentTimeMillis()) {
      try {
        if (aquireLock() == true) {
          return true;
        }
        Thread.sleep(200);
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
    }
    return false;
  }

  /**
   * Open lock.
   */
  public void openLock()
  {
    if (channel != null) {
      return;
    }
    try {
      channel = new RandomAccessFile(file, "rws").getChannel();
      // System.out.println("channel opened: " + file.getName());
    } catch (IOException ex) {
      throw new RuntimeIOException("Cannot aquire Lock file: " + file.getAbsolutePath() + "; " + ex.getMessage(), ex);
    }
  }

  /**
   * Aquire lock.
   *
   * @return true, if successful
   */
  public boolean aquireLock()
  {
    try {
      openLock();
      lock = channel.tryLock();
    } catch (IOException ex) {
      NIOUtils.closeQuitly(channel);
      throw new RuntimeIOException("Cannot aquire Lock: " + file + "; " + ex.getMessage(), ex);
    } catch (OverlappingFileLockException e) {
      NIOUtils.closeQuitly(channel);
      channel = null;
      return false;
    }
    return true;

  }

  /**
   * Release lock.
   */
  public void releaseLock()
  {
    try {
      // Release the lock
      if (lock != null) {
        lock.release();
      }
    } catch (IOException ex) {
      ;
    }
  }

  /**
   * Close lock.
   */
  public void closeLock()
  {
    if (channel == null) {
      return;
    }
    FileChannel fs = channel;
    channel = null;
    NIOUtils.closeQuitly(fs);
  }

  public int getLockCount()
  {
    return lockCount;
  }

  public void setLockCount(int lockCount)
  {
    this.lockCount = lockCount;
  }

  public File getFile()
  {
    return file;
  }

  public void setFile(File file)
  {
    this.file = file;
  }

  public FileChannel getChannel()
  {
    return channel;
  }

  public void setChannel(FileChannel channel)
  {
    this.channel = channel;
  }

  public FileLock getLock()
  {
    return lock;
  }

  public void setLock(FileLock lock)
  {
    this.lock = lock;
  }
}
