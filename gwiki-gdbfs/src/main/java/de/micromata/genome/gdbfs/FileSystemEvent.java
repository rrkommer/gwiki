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

/**
 * Sent to FileSystemEventListener.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
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
