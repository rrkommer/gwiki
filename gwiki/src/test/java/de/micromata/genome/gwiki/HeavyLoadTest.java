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

package de.micromata.genome.gwiki;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class HeavyLoadTest extends TestCase
{
  public void testFs()
  {

    String s = ".\\..\\gwiki\\src\\main\\resources_zip\\gwiki\\admin\\templates";
    File f = new File(s);
    int loops = 10000;
    long start = System.currentTimeMillis();
    for (int i = 0; i < loops; ++i) {
      try {
        f.getCanonicalPath();
      } catch (IOException ex) {

      }
    }
    long end = System.currentTimeMillis();
    long diff = end - start;
    System.out.println("" + loops + " canonical: " + diff);
    f = new File("C:\\Users\\roger\\d\\dhl\\gwiki\\..\\gwiki\\src\\main\\resources_zip\\gwiki\\admin");
    start = System.currentTimeMillis();
    for (int i = 0; i < loops; ++i) {
      try {
        f.getCanonicalPath();
      } catch (IOException ex) {

      }
    }
    end = System.currentTimeMillis();
    diff = end - start;
    System.out.println("" + loops + " canonical abs: " + diff);
    f = new File("C:\\Users\\roger\\d\\dhl\\gwiki\\..\\gwiki\\src\\main\\resources_zip\\gwiki\\admin");
    start = System.currentTimeMillis();
    for (int i = 0; i < loops; ++i) {
      String absp = f.getAbsolutePath();
      // absp.length();
    }
    end = System.currentTimeMillis();
    diff = end - start;
    System.out.println("" + loops + " abs path: " + diff);

  }

  static class MyThread extends Thread
  {

    @Override
    public void run()
    {
      int[] data = new int[1000000];

      for (int j = 0; j < 1000; ++j) {

        for (int i = 0; i < data.length; ++i) {
          data[i] = i;
          double d = i / 2;
          d += 1;
        }
      }
    }
  }

  public void NotestLoad()
  {
    int threadc = 1;
    Thread[] threads = new Thread[threadc];

    for (int i = 0; i < threads.length; ++i) {
      threads[i] = new MyThread();
    }
    for (int i = 0; i < threads.length; ++i) {
      threads[i].start();
    }
    for (int i = 0; i < threads.length; ++i) {
      try {
        threads[i].join();
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}
