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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * Interface for a abstract file system.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface FileSystem
{

  /**
   * The Constant TYPE_FILE.
   */
  public static final char TYPE_FILE = 'F';

  /**
   * The Constant TYPE_DIR.
   */
  public static final char TYPE_DIR = 'D';

  /**
   * The data encoding only used for db.
   */
  public static final char DATAENCODING_BINARY = 'B';

  /**
   * The Constant DATAENCODING_TEXT.
   */
  public static final char DATAENCODING_TEXT = 'T';

  /**
   * The Constant DATAENCODING_LONGVALUE.
   */
  public static final char DATAENCODING_LONGVALUE = 'L';

  /**
   * The Constant DATAENCODING_SHORTVALUE.
   */
  public static final char DATAENCODING_SHORTVALUE = 'S';

  /**
   * return this on standard implementation.
   *
   * @param name the name
   * @return the fs for read
   */
  FileSystem getFsForRead(String name);

  /**
   * return this on standard implementation.
   *
   * @param name the name
   * @return the fs for write
   */
  FileSystem getFsForWrite(String name);

  String getFileSystemName();

  /**
   * See File.mkdir
   *
   * @param name the name
   * @return true, if successful
   */
  public boolean mkdir(String name);

  /**
   * See File.mkdirs.
   *
   * @param name the name
   * @return true, if successful
   */
  public boolean mkdirs(String name);

  /**
   * Exists.
   *
   * @param name the name
   * @return true if file exists (read fs)
   */
  boolean exists(String name);

  /**
   * same as exists, but on mounted filesystems check if file is on writeable filesystem.
   *
   * @param name the name
   * @return true, if successful
   */
  boolean existsForWrite(String name);

  /**
   * See File.rename
   *
   * @param oldName the old name
   * @param newName the new name
   * @return true, if successful
   */
  boolean rename(String oldName, String newName);

  /**
   * delete this file/directory.
   *
   * @param name the name
   * @return false if file cannot be deleted because has childs.
   */
  boolean delete(String name);

  /**
   * In case of file is a FsFileObject equals to delete.
   * 
   * Otherwise delete directory with all nested files an directories.
   *
   * @param name the name
   * @return false if file cannot be deleted.
   */
  boolean deleteRecursive(String name);

  /**
   * Gets the file object.
   *
   * @param name the name
   * @return null if not exists.
   */
  public FsObject getFileObject(final String name);

  /**
   * determine last modification date of file.
   * 
   * @param name file name
   * @return 0 if file does not exists
   */
  public long getLastModified(final String name);

  /**
   * Write binary file.
   *
   * @param file the file
   * @param is the is
   * @param overWrite the over write
   * @throws FsFileExistsException if file exists and overWrite is true
   */
  void writeBinaryFile(String file, InputStream is, boolean overWrite);

  /**
   * Write binary file.
   *
   * @param file the file
   * @param data the data
   * @param overWrite the over write
   * @throws FsFileExistsException if file exists and overWrite is true
   */
  void writeBinaryFile(String file, byte[] data, boolean overWrite);

  /**
   * Write text file.
   *
   * @param file the file
   * @param content the content
   * @param overWrite the over write
   * @throws FsFileExistsException if file exists and overWrite is true
   */
  void writeTextFile(String file, String content, boolean overWrite);

  /**
   * Write file.
   *
   * @param file the file
   * @param data the data
   * @param overWrite the over write
   * @throws FsFileExistsException if file exists and overWrite is true
   */
  void writeFile(String file, byte[] data, boolean overWrite);

  /**
   * Write file.
   *
   * @param file the file
   * @param is the is
   * @param overWrite the over write
   */
  void writeFile(String file, InputStream is, boolean overWrite);

  /**
   * Read binary file.
   *
   * @param file the file
   * @param os the os
   * @throws RuntimeIOException if any exception on underlying storage
   * @throws FsFileExistsException if file does not exists or is not a file
   */
  void readBinaryFile(String file, OutputStream os);

  /**
   * Read binary file.
   *
   * @param file the file
   * @return the byte[]
   * @throws RuntimeIOException if any exception on underlying storage
   * @throws FsFileExistsException if file does not exists or is not a file
   */
  byte[] readBinaryFile(String file);

  /**
   * Read text file.
   *
   * @param file the file
   * @return the string
   * @throws RuntimeIOException if any exception on underlying storage
   * @throws FsFileExistsException if file does not exists or is not a file
   */

  String readTextFile(String file);

  /**
   * remove all in this filesystem. Does not send any events to listener. Better only use it in unittests.
   */
  public void erase();

  /**
   * See listFiles with matcher.
   * 
   * Uses BooleanListMatcherFactory to parse rule to matcher.
   *
   * @param name the name
   * @param matcherPattern the matcher pattern
   * @param searchType the search type
   * @param recursive the recursive
   * @return the list
   */
  public List<FsObject> listFilesByPattern(String name, String matcherPattern, Character searchType, boolean recursive);

  /**
   * List files.
   *
   * @param name directory name starting with
   * @param matcher may null if all fileobject sould be return. Otherwise matcher will match to the complete file name.
   * @param searchType 'F', 'D' or null if both
   * @param recursive if false, only returns direct files/dirs name directory.
   * @return empty list if nothing found.
   */
  public List<FsObject> listFiles(String name, Matcher<String> matcher, Character searchType, boolean recursive);

  // public List<FsFileObject> grepFiles(String name, Matcher<String> fileMatcher, String textToSearch, boolean recursive);

  // boolean lockFile(String lockFile, long timeout);
  //
  // void unLock(String lockFile);
  /**
   * return a counter which increments each time a file in the filesystem changed.
   * 
   * todo not implemented on all interfaces.
   */
  public long getModificationCounter();

  /**
   * Ensure to run this operation in transactional mode. On File system, which does not support DB-Like Transaktions
   * (like StdFileSystem), at least protect concurent writing with locking.
   *
   * @param <R> the generic type
   * @param lockFile the lock file
   * @param timeOut the time out
   * @param noModFs no modification counter will be increased
   * @param callback the callback
   * @return the r
   */
  public <R> R runInTransaction(String lockFile, long timeOut, boolean noModFs,
      CallableX<R, RuntimeException> callback);

  /**
   * 
   * @return true if this filesystem is read only. Any write access will throw FsException.
   */
  boolean isReadOnly();

  /**
   * 
   * @param readOnly
   */
  void setReadOnly(boolean readOnly);

  /**
   * Create a temporary Directory.
   *
   * @param name simple name wich will be used to build the directory name.
   * @param timeToLive milliseconds to life. After the time elapsed the temp directory may be removed.
   * @return the fs directory object
   */
  FsDirectoryObject createTempDir(String name, long timeToLive);

  /**
   * clean up tempory files/Objects.
   */
  void cleanupTempDirs();

  /**
   * Gets the mime type.
   *
   * @param fileName the file name
   * @return the mime type
   */
  String getMimeType(String fileName);

  /**
   * Checks if is text mime type.
   *
   * @param mimType the mim type
   * @return true, if is text mime type
   */
  boolean isTextMimeType(String mimType);

  /**
   * Register listener to file system events.
   * 
   * The events will be send asynchrounus is a seperate thread.
   * 
   * @param eventType may be null if all type of events will be received.
   * @param fileNameMatcher may be null if all files will be received.
   * @param listener listener receiving events.
   */
  void registerListener(FileSystemEventType eventType, Matcher<String> fileNameMatcher,
      FileSystemEventListener listener);

  /**
   * Check if Events exists and send them.
   * 
   * @param force if force is true, the events will be send. Otherwise the internal implementation will queue events and
   *          send them after a timeout
   */
  void checkEvents(boolean force);

  /**
   * Automatically create parent directories when writing files.
   * 
   * @return
   */
  boolean isAutoCreateDirectories();

  /**
   * Automatically create parent directories when writing files.
   * 
   * @param autoCreateDirectories
   */
  void setAutoCreateDirectories(boolean autoCreateDirectories);
}
