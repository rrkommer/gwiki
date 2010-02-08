/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   24.10.2009
// Copyright Micromata 24.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.IOUtils;

import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.types.Converter;

/**
 * Base implementation methods for a FileSystem.
 * 
 * @author roger@micromata.de
 * 
 */
public abstract class AbstractFileSystem implements FileSystem
{
  public String STANDARD_STRING_ENCODING = "UTF-8";

  public String defaultMimeType = "application/data";

  private String tempDirName = "tmp/tempdirs";

  private FileSystemEventQueue eventQueue = new FileSystemEventQueue(this);

  /**
   * file suffix -> mime type.
   */
  protected Map<String, String> mimeTypes = new TreeMap<String, String>(new Comparator<String>() {

    public int compare(String o1, String o2)
    {
      return o2.compareTo(o1);
    }
  });

  protected boolean readOnly = false;

  public FileSystem getFsForRead(String name)
  {
    return this;
  }

  public FileSystem getFsForWrite(String name)
  {
    return this;
  }

  public void addMimeType(String extension, String mimeType)
  {
    mimeTypes.put(extension, mimeType);
  }

  public void setAddMimeTypes(Map<String, String> mimeTypes)
  {
    this.mimeTypes.putAll(mimeTypes);
  }

  public String getMimeType(String fname)
  {
    for (Map.Entry<String, String> me : mimeTypes.entrySet()) {
      if (fname.endsWith(me.getKey()) == true) {
        return me.getValue();
      }
    }
    return defaultMimeType;
  }

  public boolean isTextMimeType(String mimeType)
  {
    return StringUtils.startsWith(mimeType, "text/") == true;
  }

  public boolean existsForWrite(String name)
  {
    return exists(name);
  }

  protected void checkReadOnly()
  {
    if (readOnly == false)
      return;
    throw new FsIOException("FileSystem is read only: " + getFileSystemName());
  }

  public static String getParentDirString(String name)
  {
    int lidx = name.lastIndexOf('/');
    if (lidx == -1)
      return "";
    return name.substring(0, lidx);
  }

  public void writeBinaryFile(String file, byte[] data, boolean overWrite)
  {
    writeBinaryFile(file, new ByteArrayInputStream(data), overWrite);
  }

  public byte[] readBinaryFile(String file)
  {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    readBinaryFile(file, bout);
    return bout.toByteArray();
  }

  protected void ensureFileObject(String file)
  {
    FsObject f = getFileObject(file);
    if (f == null) {
      throw new FsFileExistsException("Cannot find file: " + file);
    }
    if (f.isFile() == false) {
      throw new FsFileExistsException("Found file is not a file: " + file);
    }
  }

  protected String byteToString(byte[] data)
  {
    if (data == null)
      return null;
    try {
      return new String(data, STANDARD_STRING_ENCODING);
    } catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }

  protected byte[] stringToByte(String data)
  {
    if (data == null)
      return null;
    try {
      return data.getBytes(STANDARD_STRING_ENCODING);
    } catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }

  public String readTextFile(String file)
  {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    readBinaryFile(file, bout);
    return byteToString(bout.toByteArray());
  }

  protected void ensureFileToWrite(String file, boolean overWrite)
  {
    if (overWrite == false && exists(file) == true) {
      throw new FsFileExistsException("Cannot create file because already exists: " + file);
    }
    final String pdirname = getParentDirString(file);
    if (exists(pdirname) == false) {
      throw new FsFileExistsException("Cannot create/write file because parent directory does not exists: " + file);
    }
  }

  public void writeTextFile(String file, String content, boolean overWrite)
  {
    writeBinaryFile(file, stringToByte(content), overWrite);
  }

  // protected boolean isTextFileBySuffix(String file)
  // {
  // for (String t : textMimeSuffixes) {
  // if (file.endsWith(t) == true) {
  // return true;
  // }
  // }
  // return false;
  // }

  public boolean mkdirs(String name)
  {
    if (existsForWrite(name) == true)
      return false;
    String p = getParentDirString(name);
    if (existsForWrite(p) == false) {
      if (mkdirs(p) == false)
        return false;
    }
    return mkdir(name);
  }

  public void writeFile(String file, InputStream is, boolean overWrite)
  {
    byte[] data;
    try {
      data = IOUtils.toByteArray(is);
    } catch (IOException ex) {
      throw new FsIOException("Cannot read source data: " + ex.getMessage(), ex);
    }
    writeFile(file, data, overWrite);

  }

  public void writeFile(String file, byte[] data, boolean overWrite)
  {
    String mimeType = getMimeType(file);
    if (isTextMimeType(mimeType) == true) {
      writeTextFile(file, Converter.stringFromBytes(data), overWrite);
    } else {
      writeBinaryFile(file, data, overWrite);
    }
  }

  public List<FsObject> listFilesByPattern(String name, String matcherRule, Character searchType, boolean recursive)
  {
    if (matcherRule == null) {
      return listFiles(name, (Matcher<String>) null, searchType, recursive);
    }
    Matcher<String> m = new BooleanListRulesFactory<String>().createMatcher(matcherRule);
    return listFiles(name, m, searchType, recursive);
  }

  public boolean deleteRecursive(String name)
  {
    checkReadOnly();
    FsObject obj = getFileObject(name);
    if (obj == null)
      return false;
    if (obj.isFile() == true) {
      return delete(name);
    }
    List<FsObject> nested = listFiles(name, null, null, false);
    for (FsObject o : nested) {
      if (deleteRecursive(o.getName()) == false) {
        return false;
      }
    }
    return delete(name);
  }

  public void cleanupTempDirs()
  {
    FsObject fsobj = getFileObject(tempDirName);
    if (fsobj == null || fsobj.exists() == false || fsobj.isDirectory() == false) {
      return;
    }
    FsDirectoryObject dir = (FsDirectoryObject) fsobj;
    long now = System.currentTimeMillis();
    for (FsObject chd : dir.getChilds('D')) {
      String p = chd.getNamePart();
      try {
        Date d = GWikiProps.parseTimeStamp(p);
        if (d.getTime() > now) {
          continue;
        }
        this.deleteRecursive(chd.getName());
      } catch (RuntimeException ex) {
        continue;
      }
    }
  }

  public FsDirectoryObject createTempDir(String name, long timeToLive)
  {
    cleanupTempDirs();
    long ts = System.currentTimeMillis() + timeToLive;
    String ptname = GWikiProps.formatTimeStamp(new Date(ts));
    String ptdir = FileNameUtils.join(tempDirName, ptname);
    if (StringUtils.isNotEmpty(name) == true) {
      ptdir = FileNameUtils.join(ptdir, name);
    }
    if (mkdirs(ptdir) == false) {
      return null;
    }
    return (FsDirectoryObject) getFileObject(ptdir);
  }

  protected void addEvent(FileSystemEventType eventType, String fileName, long timeStamp)
  {
    eventQueue.addEvent(eventType, fileName, timeStamp);
  }

  protected void addEvent(FileSystemEventType eventType, String fileName, long timeStamp, String oldFileName)
  {
    eventQueue.addEvent(eventType, fileName, timeStamp, oldFileName);
  }

  public void registerListener(FileSystemEventType eventType, Matcher<String> fileNameMatcher, FileSystemEventListener listener)
  {
    eventQueue.addListener(eventType, fileNameMatcher, listener);
  }

  public synchronized void checkEvents(boolean force)
  {
    eventQueue.sendEvents();
  }

  public boolean isReadOnly()
  {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly)
  {
    this.readOnly = readOnly;
  }

  public String getTempDirName()
  {
    return tempDirName;
  }

  public void setTempDirName(String tempDirName)
  {
    this.tempDirName = tempDirName;
  }

  public String getDefaultMimeType()
  {
    return defaultMimeType;
  }

  public void setDefaultMimeType(String defaultMimeType)
  {
    this.defaultMimeType = defaultMimeType;
  }

}
