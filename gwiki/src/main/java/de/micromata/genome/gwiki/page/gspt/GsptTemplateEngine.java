/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.11.2006
// Copyright Micromata 21.11.2006
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import groovy.lang.GroovyShell;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import de.micromata.genome.gwiki.page.gspt.ExtendedTemplate.Flags;

/**
 * Internal for parsing gspt
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GsptTemplateEngine extends SimpleTemplateEngine
{
  public String groovySource;

  public int templateFlags = Flags.combineFlags(//
      Flags.StripLeadingWs, //
      Flags.CompressOutWriter, //
      Flags.UseHereConstString, //
      Flags.UseElInlineExpressions/* , Flags.CompressWs */);

  public GsptTemplateEngine()
  {
  }

  public GsptTemplateEngine(int templateFlags)
  {
    this.templateFlags = templateFlags;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Template createTemplate(Reader reader) throws CompilationFailedException, IOException
  {
    return createTemplate(reader, (Map<String, Object>) Collections.EMPTY_MAP);
  }

  public Template createTemplate(String templateText, Map<String, Object> context) throws CompilationFailedException,
      FileNotFoundException, ClassNotFoundException, IOException
  {
    return createTemplate(new StringReader(templateText), context);
  }

  public Template createTemplate(Reader reader, Map<String, Object> context) throws CompilationFailedException, IOException
  {
    ExtendedTemplate template = new ExtendedTemplate();
    template.setFlags(templateFlags);
    GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader());
    for (Map.Entry<String, Object> me : context.entrySet()) {
      shell.setVariable(me.getKey(), me.getValue());
    }
    groovySource = template.parse(reader);
    template.setScript(shell.parse(groovySource));
    return template;
  }

  public String getGroovySource()
  {
    return groovySource;
  }

  public void setGroovySource(String groovySource)
  {
    this.groovySource = groovySource;
  }

  public int getTemplateFlags()
  {
    return templateFlags;
  }

  public void setTemplateFlags(int templateFlags)
  {
    this.templateFlags = templateFlags;
  }

  public void addTemplateFlag(Flags templateFlag)
  {
    this.templateFlags |= templateFlag.getFlags();
  }
}
