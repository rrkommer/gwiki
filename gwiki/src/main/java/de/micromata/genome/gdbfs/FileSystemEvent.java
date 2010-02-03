/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   08.01.2010
// Copyright Micromata 08.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs;

/**
 * Sent to FileSystemEventListener.
 * 
 * @author roger@micromata.de
 * 
 */
public class FileSystemEvent
{
  private FileSystemEventType eventType;

  private FileSystem fileSystem;

  private String fileName;

  /**
   * is only set if FileSystemEventType.Renamed
   */
  private String oldFileName;

  /**
   * Timestamp of the modification/creation/deletion
   */
  private long timeStamp;

  public FileSystemEvent(FileSystemEventType eventType, FileSystem fileSystem, String fileName, long timeStamp)
  {
    this.eventType = eventType;
    this.fileSystem = fileSystem;
    this.fileName = fileName;
    this.timeStamp = timeStamp;
  }

  public FileSystemEvent(FileSystemEventType eventType, FileSystem fileSystem, String fileName, long timeStamp, String oldFileName)
  {
    this(eventType, fileSystem, fileName, timeStamp);
    this.oldFileName = oldFileName;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(eventType).append(": ");
    if (oldFileName != null) {
      sb.append(oldFileName).append(" -> ").append(fileName);
    } else {
      sb.append(fileName);
    }
    return sb.toString();
  }

  public FileSystemEventType getEventType()
  {
    return eventType;
  }

  public void setEventType(FileSystemEventType eventType)
  {
    this.eventType = eventType;
  }

  public FileSystem getFileSystem()
  {
    return fileSystem;
  }

  public void setFileSystem(FileSystem fileSystem)
  {
    this.fileSystem = fileSystem;
  }

  public String getFileName()
  {
    return fileName;
  }

  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }

  public String getOldFileName()
  {
    return oldFileName;
  }

  public void setOldFileName(String oldFileName)
  {
    this.oldFileName = oldFileName;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp)
  {
    this.timeStamp = timeStamp;
  }

}
