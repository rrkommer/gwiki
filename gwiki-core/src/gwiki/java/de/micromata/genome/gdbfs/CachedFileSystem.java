/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   20.11.2009
// Copyright Micromata 20.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * Experimental Caching file system.
 * 
 * @author roger@micromata.de
 * 
 */
public class CachedFileSystem extends FileSystemWrapper implements InitializingBean
{
  // private JCSCacheWrapper<String, byte[]> fileContentCache;

  private SoftReference<RamFileSystem> fileSystemCache = new SoftReference<RamFileSystem>(new RamFileSystem());

  private String filesToCacheMatcherRule = "-*";

  private Matcher<String> filesToCacheMatcher;

  private boolean dirty = true;

  private long lastFsUpdateCounter = 0;

  private long checkFsUpdateCounterTimeout = TimeInMillis.SECOND * 30;

  private long lastFsUpdateCounterTime = 0;

  public CachedFileSystem()
  {

  }

  public CachedFileSystem(FileSystem nested)
  {
    super(nested);
  }

  public void afterPropertiesSet() throws Exception
  {
    filesToCacheMatcher = new BooleanListRulesFactory<String>().createMatcher(filesToCacheMatcherRule);

  }

  public boolean exists(String name)
  {
    checkDirty();
    RamFileSystem rfsys = fetchRamFileSystem();
    boolean exits = rfsys.exists(name);
    boolean nex = nested.exists(name);
    if (exits == nex) {
      return exits;
    }
    dirty = true;
    return nex;
  }

  protected void reloadRamFS(RamFileSystem target)
  {
    List<FsObject> files = nested.listFiles("", null, null, true);
    for (FsObject f : files) {
      target.addFile(f);
    }
  }

  protected RamFileSystem fetchRamFileSystem()
  {
    RamFileSystem rfsys;
    if (dirty == false) {
      rfsys = fileSystemCache.get();
      if (rfsys != null) {
        return rfsys;
      }
    }
    rfsys = new RamFileSystem();
    reloadRamFS(rfsys);
    fileSystemCache = new SoftReference<RamFileSystem>(rfsys);
    dirty = false;
    return rfsys;
  }

  protected void checkDirtyInternal()
  {
    if (dirty == true) {
      return;
    }
    long now = System.currentTimeMillis();
    if (lastFsUpdateCounterTime + checkFsUpdateCounterTimeout > now) {
      long upc = nested.getModificationCounter();
      if (upc != lastFsUpdateCounter) {
        dirty = true;
        lastFsUpdateCounter = upc;
        lastFsUpdateCounterTime = now;
      }
    }

  }

  protected void checkDirty()
  {
    checkDirtyInternal();

  }

  @Override
  public boolean delete(String name)
  {
    dirty = true;
    return super.delete(name);
  }

  @Override
  public boolean deleteRecursive(String name)
  {
    dirty = true;
    return super.deleteRecursive(name);
  }

  @Override
  public void erase()
  {
    dirty = true;
    super.erase();
  }

  @Override
  public FsObject getFileObject(String name)
  {
    checkDirty();
    RamFileSystem rfsys = fetchRamFileSystem();
    return rfsys.getFileObject(name);
  }

  private boolean cacheFile(String file)
  {
    return filesToCacheMatcher.match(file);
  }

  @Override
  public void readBinaryFile(String file, OutputStream os)
  {
    checkDirty();
    if (cacheFile(file) == false) {
      super.readBinaryFile(file, os);
      return;
    }
    RamFileSystem rfsys = fetchRamFileSystem();
    FsObjectContainer fcont = rfsys.getFiles().get(file);
    if (fcont == null) {
      super.readBinaryFile(file, os);
      return;
    }
    if (fcont.getByteData() != null) {
      rfsys.readBinaryFile(file, os);
      return;
    }
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    super.readBinaryFile(file, bout);
    fcont.setByteData(bout.toByteArray());
    rfsys.readBinaryFile(file, os);
  }

  @Override
  public byte[] readBinaryFile(String file)
  {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    readBinaryFile(file, bout);
    return bout.toByteArray();
  }

  @Override
  public String readTextFile(String file)
  {
    checkDirty();
    if (cacheFile(file) == false) {
      return super.readTextFile(file);
    }
    RamFileSystem rfsys = fetchRamFileSystem();
    FsObjectContainer fcont = rfsys.getFiles().get(file);
    if (fcont == null) {
      return super.readTextFile(file);
    }
    if (fcont.getByteData() != null) {
      return rfsys.readTextFile(file);
    }
    String data = super.readTextFile(file);
    fcont.setByteData(stringToByte(data));
    return rfsys.readTextFile(file);
  }

  @Override
  public boolean rename(String oldName, String newName)
  {
    dirty = true;
    return super.rename(oldName, newName);
  }

  @Override
  public void writeFile(String file, byte[] data, boolean overWrite)
  {
    dirty = true;
    super.writeFile(file, data, overWrite);
  }

  public SoftReference<RamFileSystem> getFileSystemCache()
  {
    return fileSystemCache;
  }

  public void setFileSystemCache(SoftReference<RamFileSystem> fileSystemCache)
  {
    this.fileSystemCache = fileSystemCache;
  }

  public String getFilesToCacheMatcherRule()
  {
    return filesToCacheMatcherRule;
  }

  public void setFilesToCacheMatcherRule(String filesToCacheMatcherRule)
  {
    this.filesToCacheMatcherRule = filesToCacheMatcherRule;
  }

  public Matcher<String> getFilesToCacheMatcher()
  {
    return filesToCacheMatcher;
  }

  public void setFilesToCacheMatcher(Matcher<String> filesToCacheMatcher)
  {
    this.filesToCacheMatcher = filesToCacheMatcher;
  }

  public boolean isDirty()
  {
    return dirty;
  }

  public void setDirty(boolean dirty)
  {
    this.dirty = dirty;
  }

  public long getLastFsUpdateCounter()
  {
    return lastFsUpdateCounter;
  }

  public void setLastFsUpdateCounter(long lastFsUpdateCounter)
  {
    this.lastFsUpdateCounter = lastFsUpdateCounter;
  }

  public long getCheckFsUpdateCounterTimeout()
  {
    return checkFsUpdateCounterTimeout;
  }

  public void setCheckFsUpdateCounterTimeout(long checkFsUpdateCounterTimeout)
  {
    this.checkFsUpdateCounterTimeout = checkFsUpdateCounterTimeout;
  }

  public long getLastFsUpdateCounterTime()
  {
    return lastFsUpdateCounterTime;
  }

  public void setLastFsUpdateCounterTime(long lastFsUpdateCounterTime)
  {
    this.lastFsUpdateCounterTime = lastFsUpdateCounterTime;
  }

}
