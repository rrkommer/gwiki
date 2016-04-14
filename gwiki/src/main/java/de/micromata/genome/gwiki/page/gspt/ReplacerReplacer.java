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

package de.micromata.genome.gwiki.page.gspt;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class ReplacerReplacer extends ReplacerBase
{
  GsptPreprocessor processor;

  public ReplacerReplacer(GsptPreprocessor processor)
  {
    this.processor = processor;
  }

  public String getStart()
  {
    return "<@gsptreplacer";
  }

  public String getEnd()
  {

    return "@>";
  }

  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String className = attr.get("className");
    try {
      Class< ? > cls = Thread.currentThread().getContextClassLoader().loadClass(className);
      Constructor< ? > cstr = null;
      try {
        cstr = cls.getConstructor(new Class[] { GsptPreprocessor.class});
      } catch (NoSuchMethodException ex) {
      }
      if (cstr != null) {
        Replacer replacer = (Replacer) cstr.newInstance(new Object[] { processor});
        processor.addReplacer(replacer);
      } else {
        Replacer replacer = (Replacer) cls.newInstance();
        processor.addReplacer(replacer);
      }

    } catch (Throwable ex) {
      throw new RuntimeException("error while gspt process ReplacerReplacer", ex);
    }
    return "";
  }

}
