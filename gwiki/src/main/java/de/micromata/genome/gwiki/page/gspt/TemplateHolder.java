/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.10.2009
// Copyright Micromata 21.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import groovy.text.Template;

import java.io.Serializable;

/**
 * serializable wrapper
 * 
 * @author roger@micromata.de
 * 
 */
public class TemplateHolder implements Serializable
{

  private static final long serialVersionUID = -7749122008200810429L;

  private transient Template template;

  public TemplateHolder(Template template)
  {
    this.template = template;
  }

  public Template getTemplate()
  {
    return template;
  }

  public void setTemplate(Template template)
  {
    this.template = template;
  }

}
