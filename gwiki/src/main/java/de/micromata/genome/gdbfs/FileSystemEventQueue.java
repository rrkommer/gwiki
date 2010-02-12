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

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.types.Pair;

/**
 * Queue to send files.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FileSystemEventQueue
{
  public static class Listener
  {
    private final Matcher<String> matcher;

    private final FileSystemEventListener listener;

    /**
     * may be null
     */
    private final FileSystemEventType eventType;

    public Listener(FileSystemEventType eventType, Matcher<String> matcher, FileSystemEventListener listener)
    {
      this.matcher = matcher;
      this.listener = listener;
      this.eventType = eventType;
    }

    public Matcher<String> getMatcher()
    {
      return matcher;
    }

    public FileSystemEventListener getListener()
    {
      return listener;
    }

    public FileSystemEventType getEventType()
    {
      return eventType;
    }

  }

  private FileSystem fileSystem;

  protected List<Listener> listenerList = new ArrayList<Listener>();

  private List<Pair<Listener, FileSystemEvent>> events = new ArrayList<Pair<Listener, FileSystemEvent>>();

  public FileSystemEventQueue(FileSystem fileSystem)
  {
    this.fileSystem = fileSystem;
  }

  public void addListener(FileSystemEventType eventType, Matcher<String> matcher, FileSystemEventListener listener)
  {
    listenerList.add(new Listener(eventType, matcher, listener));
  }

  /**
   * send all events
   */
  public void sendEvents()
  {
    Map<FileSystemFinalizedEventListener, Void> allListener = new IdentityHashMap<FileSystemFinalizedEventListener, Void>();
    for (Pair<Listener, FileSystemEvent> event : events) {
      final FileSystemEventListener listener = event.getFirst().getListener();
      listener.onFileSystemChanged(event.getSecond());
      if (listener instanceof FileSystemFinalizedEventListener) {
        allListener.put((FileSystemFinalizedEventListener) listener, null);
      }
    }
    for (FileSystemFinalizedEventListener fl : allListener.keySet()) {
      fl.onFileSystemChangedFinalized();
    }
    events.clear();
  }

  protected void insertEvent(FileSystemEvent event)
  {
    for (Listener li : listenerList) {
      if (li.getEventType() != null && li.getEventType() != event.getEventType()) {
        continue;
      }
      if (li.getMatcher() == null || li.getMatcher().match(event.getFileName()) == true) {
        events.add(Pair.make(li, event));
      }
    }

  }

  public synchronized void addEvent(FileSystemEventType eventType, String fileName, long timeStamp)
  {
    insertEvent(new FileSystemEvent(eventType, fileSystem, fileName, timeStamp));
  }

  public synchronized void addEvent(FileSystemEventType eventType, String fileName, long timeStamp, String oldFileName)
  {
    insertEvent(new FileSystemEvent(eventType, fileSystem, fileName, timeStamp, oldFileName));
  }
}
