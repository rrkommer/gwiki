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

package de.micromata.genome.gwiki.fssvn;

import java.util.List;

import org.junit.Test;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;

public class MyFirstTest
{
  @Test
  public void dummy()
  {

  }

  // @Test
  public void myTest()
  {
    String url = "https://svn.micromata.de/svn/testsvn/trunk";
    try {

      Sardine sardine = SardineFactory.begin("", "", SslUtils.createEasySSLSocketFactory());
      List<DavResource> resources = sardine.getResources(url);
      for (DavResource res : resources) {

        System.out.println(res.getName());
        System.out.println("  " + res);

        System.out.println("  " + res.getCustomProps().toString());
        res.isDirectory();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
