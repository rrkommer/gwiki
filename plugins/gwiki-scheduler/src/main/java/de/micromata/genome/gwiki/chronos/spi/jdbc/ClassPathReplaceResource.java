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

package de.micromata.genome.gwiki.chronos.spi.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ClassPathReplaceResource extends ClassPathResource
{
  private Map<String, String> replaceMap = new HashMap<String, String>();

  public ClassPathReplaceResource(String path, Class< ? > clazz)
  {
    super(path, clazz);
  }

  public ClassPathReplaceResource(String path, ClassLoader classLoader, Class< ? > clazz)
  {
    super(path, classLoader, clazz);
  }

  public ClassPathReplaceResource(String path, ClassLoader classLoader)
  {
    super(path, classLoader);
  }

  public ClassPathReplaceResource(String path)
  {
    super(path);
  }

  @Override
  public InputStream getInputStream() throws IOException
  {
    return super.getInputStream();
  }

  @Override
  public Resource createRelative(String relativePath)
  {
    return super.createRelative(relativePath);
  }

  public Map<String, String> getReplaceMap()
  {
    return replaceMap;
  }

  public void setReplaceMap(Map<String, String> replaceMap)
  {
    this.replaceMap = replaceMap;
  }

}
