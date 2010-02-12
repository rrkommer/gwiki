/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   07.01.2007
// Copyright Micromata 07.01.2007
//
/////////////////////////////////////////////////////////////////////////////
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
