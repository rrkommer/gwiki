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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.MatcherFactory;

/**
 * all Access to to primaryMount will dispatch to primary.
 * 
 * All other access will be forwarded to secondary.
 * 
 * The primary must contains complete parent directories to mount point.
 * 
 * 
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class MountCombinedFileSystem extends CombinedFileSystem implements InitializingBean
{
  protected String primaryMountsRule = "";

  protected Matcher<String> primMountMatcher = null;// new ArrayList<Matcher<String>>();

  public MountCombinedFileSystem()
  {
    super();
  }

  public MountCombinedFileSystem(FileSystem primary, FileSystem secondary)
  {
    super(primary, secondary);
  }

  public MountCombinedFileSystem(FileSystem primary, String primMountRule, FileSystem secondary)
  {
    super(primary, secondary);
    this.primaryMountsRule = primMountRule;
  }

  public MountCombinedFileSystem(FileSystem primary, Matcher<String> primMountMatcher, FileSystem secondary)
  {
    super(primary, secondary);
    this.primMountMatcher = primMountMatcher;
  }

  public void afterPropertiesSet() throws Exception
  {
    if (primMountMatcher == null && StringUtils.isNotBlank(primaryMountsRule) == true) {
      MatcherFactory<String> fac = new BooleanListRulesFactory<String>();
      this.primMountMatcher = fac.createMatcher(primaryMountsRule);
    }

  }

  @Override
  public String getFileSystemName()
  {
    return super.getFileSystemName() + "(mountcombined, primary: " + primaryMountsRule + ")";
  }

  protected FileSystem getMount(String name)
  {
    if (name.startsWith("/") == true) {
      name = name.substring(1);
    }
    if (primMountMatcher == null) {
      return secondary;
    }
    if (primMountMatcher.match(name) == true) {
      return primary;
    }
    return secondary;
  }

  @Override
  public FileSystem getFsForRead(String name)
  {
    return getMount(name);
  }

  @Override
  public FileSystem getFsForWrite(String name)
  {
    return getMount(name);
  }

  @Override
  public List<FsObject> listFiles(String name, Matcher<String> matcher, Character searchType, boolean recursive)
  {
    List<FsObject> ret1 = secondary.listFiles(name, matcher, searchType, recursive);
    for (Iterator<FsObject> it = ret1.iterator(); it.hasNext();) {
      FsObject obj = it.next();
      if (getMount(obj.getName()) != secondary) {
        it.remove();
      }
    }
    List<FsObject> ret2 = primary.listFiles(name, matcher, searchType, recursive);
    for (Iterator<FsObject> it = ret2.iterator(); it.hasNext();) {
      FsObject obj = it.next();
      if (getMount(obj.getName()) != primary) {
        it.remove();
      }
    }
    List<FsObject> ret = new ArrayList<FsObject>();
    Set<String> names = new TreeSet<String>();
    ret.addAll(ret2);
    for (FsObject fs : ret2) {
      names.add(fs.getName());
    }
    for (FsObject fs : ret1) {
      if (names.contains(fs.name) == false) {
        ret.add(fs);
      }
    }
    return ret;
  }

  public String getPrimaryMountsRule()
  {
    return primaryMountsRule;
  }

  public void setPrimaryMountsRule(String primaryMountsRule)
  {
    this.primaryMountsRule = primaryMountsRule;
  }

  public Matcher<String> getPrimMountMatcher()
  {
    return primMountMatcher;
  }

  public void setPrimMountMatcher(Matcher<String> primMountMatcher)
  {
    this.primMountMatcher = primMountMatcher;
  }

}
