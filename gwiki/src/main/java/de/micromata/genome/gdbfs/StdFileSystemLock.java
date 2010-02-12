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

  private int lockCount;

  private File file;

  private FileChannel channel;

  private FileLock lock;

  public StdFileSystemLock(File file)
  {
    this.file = file;
  }

  public int incLockCount()
  {
    return ++lockCount;
  }

  public int decLockCount()
  {
    return --lockCount;
  }

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

  public void openLock()
  {
    if (channel != null)
      return;
    try {
      channel = new RandomAccessFile(file, "rws").getChannel();
      // System.out.println("channel opened: " + file.getName());
    } catch (IOException ex) {
      throw new RuntimeIOException("Cannot aquire Lock file: " + file + "; " + ex.getMessage(), ex);
    }
  }

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

  public void releaseLock()
  {
    try {
      // Release the lock
      lock.release();
    } catch (IOException ex) {
      ;
    }
  }

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
