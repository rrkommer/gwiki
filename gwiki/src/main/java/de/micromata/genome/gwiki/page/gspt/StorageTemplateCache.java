/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    lado@micromata.de
// Created   Aug 24, 2008
// Copyright Micromata Aug 24, 2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import groovy.text.Template;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.micromata.genome.util.types.Pair;

public class StorageTemplateCache implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 5828684238348595014L;

  private Map<String, Pair<Template, Date>> compiledPages = Collections.synchronizedMap(new HashMap<String, Pair<Template, Date>>());

  public Map<String, Pair<Template, Date>> getCompiledPages()
  {
    return compiledPages;
  }

  public void setCompiledPages(Map<String, Pair<Template, Date>> compiledPages)
  {
    this.compiledPages = compiledPages;
  }

}
