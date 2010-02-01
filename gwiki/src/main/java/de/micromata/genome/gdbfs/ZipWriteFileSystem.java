/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   27.11.2009
// Copyright Micromata 27.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * FileSystem only used to write (not to read) into a zip file.
 * 
 * The reason why not support read operations is, that implementation can write the zip file as stream.
 * 
 * For reading file system from zip file, use RamFileSystem and FileSystemUtils.copyFromZip
 * 
 * @author roger@micromata.de
 * 
 */
public class ZipWriteFileSystem extends AbstractFileSystem
{
  private OutputStream outputStream;

  private ZipOutputStream zout;

  public ZipWriteFileSystem(OutputStream os)
  {
    this.outputStream = os;
    zout = new ZipOutputStream(outputStream);
  }

  public void close()
  {
    IOUtils.closeQuietly(zout);
    IOUtils.closeQuietly(outputStream);
  }

  public void writeBinaryFile(String file, InputStream is, boolean overWrite)
  {
    ZipEntry ze = new ZipEntry(file);
    try {
      // if (source.isFile() == true) {
      zout.putNextEntry(ze);
      IOUtils.copy(is, zout);
      zout.closeEntry();
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  protected void unsupportedOp()
  {
    StackTraceElement[] els = new Throwable().getStackTrace();
    throw new FsException("Unsupported operation: " + els[1].getMethodName());
  }

  public void cleanupTempDirs()
  {
    unsupportedOp();
  }

  public FsDirectoryObject createTempDir(String name, long timeToLive)
  {
    unsupportedOp();
    return null;
  }

  public boolean delete(String name)
  {
    unsupportedOp();
    return false;
  }

  public boolean deleteRecursive(String name)
  {
    unsupportedOp();
    return false;
  }

  public void erase()
  {
    unsupportedOp();
  }

  public boolean exists(String name)
  {
    return false;
  }

  public FsObject getFileObject(String name)
  {
    unsupportedOp();
    return null;
  }

  public String getFileSystemName()
  {
    unsupportedOp();
    return "";
  }

  public long getModificationCounter()
  {
    unsupportedOp();
    return 0;
  }

  public boolean isReadOnly()
  {
    return false;
  }

  public List<FsObject> listFiles(String name, Matcher<String> matcher, Character searchType, boolean recursive)
  {
    unsupportedOp();
    return null;
  }

  public boolean mkdir(String name)
  {
    return true;
  }

  public boolean mkdirs(String name)
  {
    return true;
  }

  public void readBinaryFile(String file, OutputStream os)
  {
    unsupportedOp();
  }

  public byte[] readBinaryFile(String file)
  {
    unsupportedOp();
    return null;
  }

  public String readTextFile(String file)
  {
    unsupportedOp();
    return null;
  }

  public boolean rename(String oldName, String newName)
  {
    unsupportedOp();
    return false;
  }

  public long getLastModified(final String name)
  {
    return 0;
  }

  public <R> R runInTransaction(String lockFile, long timeOut, boolean noModFs, CallableX<R, RuntimeException> callback)
  {
    return callback.call();
  }

  public void setReadOnly(boolean readOnly)
  {
    unsupportedOp();
  }

}
