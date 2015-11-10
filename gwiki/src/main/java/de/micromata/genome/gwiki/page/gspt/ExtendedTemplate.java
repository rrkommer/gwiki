////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

package de.micromata.genome.gwiki.page.gspt;

import groovy.lang.Binding;
import groovy.lang.Script;
import groovy.lang.Writable;
import groovy.text.Template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.runtime.InvokerHelper;

import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.page.gspt.ExtendedTemplate.ParseElement.Type;

/**
 * Internal for parsing gspt
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class ExtendedTemplate implements Template
{
  /**
   * The script executed
   */
  private Script script;

  public static enum Flags
  {
    /**
     * Wirft von ConstantString die fuehrenden Ws weg
     */
    StripLeadingWs(0x0001), //
    /**
     * Fasst mehrere ConstantString-Elemente in eines
     */
    CompressOutWriter(0x0002), //
    /**
     * Innerhalb ConstantString-Elemente werden mehrer ' ' als eines zusammengefasst
     */
    CompressWs(0x0004), //
    /**
     * Statt escaping von String-Literalen wird groovy ''' ''' Strings verwendet
     */
    UseHereConstString(0x0008), //
    /**
     * Statt escaping von String-Literalen wird groovy """ """ Strings verwendet. $-Ausdruecke werden ausgewertet
     */
    UseHereExprString(0x0010),
    /**
     * ${} expression are evaluated as El-Expressions not as rt-Expressions
     */
    UseElInlineExpressions(0x0020), //
    /**
     * Generate GsptPageBase base class
     */
    GenGsptPageBase(0x0040), //
    ;
    int flags;

    private Flags(int flags)
    {
      this.flags = flags;
    }

    boolean isFlagSet(int combFlags)
    {
      return (combFlags & flags) == flags;
    }

    public static int combineFlags(Flags... flags)
    {
      int compflags = 0;
      for (Flags f : flags) {
        compflags |= f.getFlags();
      }
      return compflags;
    }

    public int getFlags()
    {
      return flags;
    }
  }

  private int flags = Flags.combineFlags(//
      Flags.StripLeadingWs, //
      Flags.CompressOutWriter, //
      Flags.UseHereConstString, //
      Flags.UseElInlineExpressions/* , Flags.CompressWs */);

  public ExtendedTemplate()
  {

  }

  public Script getScript()
  {
    return script;
  }

  @SuppressWarnings("rawtypes")
  public Writable make()
  {
    return make((Map) null);
  }

  @SuppressWarnings("rawtypes")
  public Writable make(final Map map)
  {
    return make(map == null ? new Binding() : new Binding(map));
  }

  public Writable make(final Binding binding)
  {
    return new Writable() {
      /**
       * Write the template document with the set binding applied to the writer.
       * 
       * @see groovy.lang.Writable#writeTo(java.io.Writer)
       */
      public Writer writeTo(Writer writer)
      {
        Script scriptObject = InvokerHelper.createScript(script.getClass(), binding);
        PrintWriter pw = new PrintWriter(writer);
        scriptObject.setProperty("out", pw);
        scriptObject.run();
        pw.flush();
        return writer;
      }

      /**
       * Convert the template and binding into a result String.
       * 
       * @see java.lang.Object#toString()
       */
      @Override
      public String toString()
      {
        try {
          StringWriter sw = new StringWriter();
          writeTo(sw);
          return sw.toString();
        } catch (Exception e) {
          return e.toString();
        }
      }
    };
  }

  public static class ParseElement
  {
    public static enum Type
    {
      // constant template
      ConstString, //

      Statement,
      /**
       * <%= ... %> becomes ${...}
       */
      AssignExpr, //

      Comment, //
      Code,
      /* <%! ... %> */
      ClassCode,
      /* <%!! ... %> */
      GlobalCode,
    }

    public Type type;

    public StringBuilder text;

    public ParseElement(Type type, String text)
    {
      this.type = type;
      this.text = new StringBuilder(text);
    }
  }

  private void stripLeadingWs(List<ParseElement> elements)
  {
    for (int i = 0; i < elements.size(); ++i) {
      if (elements.get(i).type == Type.Comment
          || elements.get(i).type == Type.Code
          || elements.get(i).type == Type.GlobalCode
          || elements.get(i).type == Type.ClassCode
          || elements.get(i).type == Type.Statement)
        continue;
      if (elements.get(i).type != Type.ConstString)
        break;
      String t = StringUtils.stripStart(elements.get(i).text.toString(), " \t\r\n");
      if (StringUtils.isEmpty(t) == true) {
        elements.remove(i);
        --i;
      } else {
        elements.get(i).text = new StringBuilder(t);
        break;
      }
    }
  }

  private void compressOutWriter(List<ParseElement> elements)
  {
    ParseElement lout = null;
    for (int i = 0; i < elements.size(); ++i) {
      ParseElement e = elements.get(i);
      if (e.type != Type.ConstString) {
        lout = null;
        continue;
      }
      if (lout == null) {
        lout = e;
        continue;
      }
      lout.text.append(e.text);
      elements.remove(i);
      --i;
    }
  }

  private String escapeLiteral(String text)
  {
    StringWriter ret = new StringWriter();
    for (int i = 0; i < text.length(); ++i) {
      char c = text.charAt(i);
      switch (c) {
        case '"':
          ret.append("\\\"");
          break;
        case '\\':
          ret.append("\\\\");
          break;
        case '\n':
          ret.append("\\n");
          break;
        case '\r':
          ret.append("\\r");
          break;
        default:
          ret.append(c);
          break;
      }
    }
    return ret.toString();
  }

  public static String escapeQuote(String text, char quoteChar)
  {
    StringWriter ret = new StringWriter();
    for (int i = 0; i < text.length(); ++i) {
      char c = text.charAt(i);
      if (c == quoteChar)
        ret.append("\\").append(c);
      else
        ret.append(c);
    }
    return ret.toString();
  }

  private String patchStatement(String s)
  {
    String t = s.trim();
    if (t.endsWith("}") == true || t.endsWith(";") == false)
      return s + "\n";
    return s;
  }

  private String elementToCode(List<ParseElement> elements)
  {
    StringWriter sw = new StringWriter();
    boolean genClass = (getFlags() & Flags.GenGsptPageBase.getFlags()) == Flags.GenGsptPageBase.getFlags();
    // TODO roger here flag for servlet
    sw.append("import javax.servlet.*;\n");
    sw.append("import javax.servlet.jsp.*;\n");
    sw.append("\nServletConfig getServletConfig() { return servletConfig; }\n\n");

    for (ParseElement el : elements) {
      switch (el.type) {
        case GlobalCode:
          sw.append(el.text);
          break;
      }
    }

    if (genClass == true) {
      sw.append("class TClass extends de.micromata.genome.web.gspt.GsptPageBase { \n");
    }
    for (ParseElement el : elements) {
      switch (el.type) {
        case ClassCode:
          sw.append(el.text);
          break;
      }
    }
    if (genClass == true) {
      sw.append("  public void _gsptService(javax.servlet.jsp.PageContext pageContext) {\n");
      sw.append("   javax.servlet.http.HttpServletResponse response = pageContext.getResponse();\n");
      sw.append("   javax.servlet.http.HttpServletRequest request = pageContext.getRequest();\n");

    }
    for (ParseElement el : elements) {
      switch (el.type) {
        case AssignExpr:
          // sw.append("${").append(el.text).append("}");
          sw.append("out.print(").append(el.text).append(");");
          break;
        case ClassCode:
        case GlobalCode:
          break;
        case Code:
          sw.append(el.text);
          break;
        case Comment:
          // sw.append("/* ").append(el.text).append(" */");
          break;
        case ConstString:
          if (Flags.UseHereConstString.isFlagSet(flags) == true)
            sw.append("out.print('''").append(escapeQuote(el.text.toString(), '\'')).append("''');\n");
          else if (Flags.UseHereExprString.isFlagSet(flags) == true)
            sw.append("out.print(\"\"\"").append(escapeQuote(el.text.toString(), '"')).append("\"\"\");\n");
          else
            sw.append("out.print(\"").append(escapeLiteral(el.text.toString())).append("\");\n");
          break;
        case Statement:
          sw.append(patchStatement(el.text.toString()));
          break;
      }
    }
    if (genClass == true) {
      sw.append("}\n}\n") //
          .append("TClass tcls = new TClass();\n") //
          .append("tcls.setServletConfig(servletConfig);\n") //
          .append("tcls.service(pageContext);\n"); //
    }
    return sw.toString();
  }

  private void compressWsString(StringBuilder text)
  {
    char lastChar = 0;
    for (int i = 0; i < text.length(); ++i) {
      char cc = text.charAt(i);
      if (lastChar == ' ' && cc == ' ') {
        text.deleteCharAt(i);
        --i;
        continue;
      }
      lastChar = cc;
    }
  }

  private void compressWsInConstantString(List<ParseElement> elements)
  {
    for (ParseElement el : elements) {
      if (el.type != Type.ConstString)
        continue;
      compressWsString(el.text);
    }
  }

  private int replaceElInlineExpressions(List<ParseElement> elements, int elIdx)
  {
    if (elements.get(elIdx).text.indexOf("${") == -1)
      return elIdx;

    StringBuilder sb = elements.get(elIdx).text;
    elements.remove(elIdx);

    for (int i = 0; i < sb.length(); ++i) {
      char c = sb.charAt(i);
      if (c == '$' && sb.length() > i + 1 && sb.charAt(i + 1) == '{') {
        int startIdx = i;
        int endIdx = -1;
        i += 2;
        for (; i < sb.length(); ++i) {
          if (sb.charAt(i) == '}') {
            endIdx = i;
            break;
          }
        }
        if (endIdx == -1)
          continue;
        String rt = sb.substring(0, startIdx);
        elements.add(elIdx++, new ParseElement(Type.ConstString, rt));

        String text = sb.substring(startIdx, endIdx + 1);

        String r = "TagSupport.printEvalInlineElExpression(pageContext, \"\\" + escapeLiteral(text) + "\");";
        elements.add(elIdx++, new ParseElement(Type.Statement, r));
        sb.replace(0, endIdx + 1, "");
        i = 0;
      }
    }
    if (sb.length() > 0)
      elements.add(elIdx++, new ParseElement(Type.ConstString, sb.toString()));
    return elIdx;
  }

  private void processElInlineExpressions(List<ParseElement> elements)
  {
    for (int i = 0; i < elements.size(); ++i) {
      ParseElement el = elements.get(i);
      if (el.type != Type.ConstString)
        continue;
      i = replaceElInlineExpressions(elements, i);
    }
  }

  /**
   * Parse the text document looking for <% or <%= and then call out to the appropriate handler, otherwise copy the text directly into the
   * script while escaping quotes.
   * 
   * @param reader
   * @return
   * @throws IOException
   */
  protected String parse(Reader reader) throws IOException
  {
    List<ParseElement> elements = parseToElements(reader);
    if (Flags.StripLeadingWs.isFlagSet(flags) == true)
      stripLeadingWs(elements);
    if (Flags.CompressOutWriter.isFlagSet(flags) == true)
      compressOutWriter(elements);
    if (Flags.CompressWs.isFlagSet(flags) == true)
      compressWsInConstantString(elements);
    if (Flags.UseElInlineExpressions.isFlagSet(flags) == true)
      processElInlineExpressions(elements);
    String result = elementToCode(elements);
    return result;
  }

  protected List<ParseElement> parseToElements(Reader reader) throws IOException
  {
    if (!reader.markSupported()) {
      reader = new BufferedReader(reader);
    }
    // StringWriter sw = new StringWriter();
    List<ParseElement> elements = new ArrayList<ParseElement>();
    startScript(elements);
    // boolean start = false;
    int c;
    while ((c = reader.read()) != -1) {
      if (c == '<') {
        reader.mark(1);
        c = reader.read();
        if (c != '%') {
          // sw.write('<');
          elements.add(new ParseElement(Type.ConstString, "<"));
          reader.reset();
        } else {
          reader.mark(1);
          c = reader.read();
          if (c == '=') {
            groovyExpression(reader, elements);
          } else if (c == '-') {
            reader.read();
            groovyComment(reader, elements);
          } else if (c == '#') {
            groovySection(Type.GlobalCode, reader, elements);
          } else if (c == '!') {
            groovySection(Type.ClassCode, reader, elements);
          } else {
            reader.reset();
            groovySection(Type.Statement, reader, elements);
          }
        }
        continue; // at least '<' is consumed ... read next chars.
      } else if (c == '-') {
        reader.mark(4);
        if (reader.read() == '-' && reader.read() == '%' && reader.read() == '>') {
          /**
           * @logging
           * @reason Innerhalb einer GSPT-Datei ist ein Kommentarendesequenz ohne oeffnendes
           * @action GSPT korrigieren
           */
          GWikiLog.warn("In gspt --%> comment without open");
        }
        reader.reset();
      }
      if (elements.size() == 0 || elements.get(elements.size() - 1).type != Type.ConstString)
        elements.add(new ParseElement(Type.ConstString, ""));
      elements.get(elements.size() - 1).text.append((char) c);

    }
    return elements;
  }

  private void startScript(List<ParseElement> el)
  {
    el.add(new ParseElement(Type.Comment, "Generated by Genome GsptTemplate"));
    // sw.write("out.print(\"");
  }

  /**
   * Closes the currently open write and writes out the following text as a GString expression until it reaches an end %>.
   * 
   * @param reader
   * @param sw
   * @throws IOException
   */
  private void groovyExpression(Reader reader, List<ParseElement> elements) throws IOException
  {
    // sw.write("\");out.print(\"${");
    StringBuilder sw = new StringBuilder();
    int c;
    while ((c = reader.read()) != -1) {
      if (c == '%') {
        c = reader.read();
        if (c != '>') {
          // sw.write('%');
          sw.append('%');
        } else {
          break;
        }
      }
      if (c != '\n' && c != '\r') {
        sw.append((char) c);
      }
    }
    elements.add(new ParseElement(Type.AssignExpr, sw.toString()));
    // sw.write("}\");\nout.print(\"");
  }

  /**
   * Closes the currently open write and writes the following text as normal Groovy script code until it reaches an end %>.
   * 
   * @param reader
   * @param sw
   * @throws IOException
   */
  private void groovySection(Type global, Reader reader, List<ParseElement> elements) throws IOException
  {
    // sw.write("\");");
    int c;
    StringBuilder sw = new StringBuilder();
    while ((c = reader.read()) != -1) {
      if (c == '%') {
        c = reader.read();
        if (c != '>') {
          sw.append('%');
        } else {
          break;
        }
      }
      /*
       * Don't eat EOL chars in sections - as they are valid instruction separators. See http://jira.codehaus.org/browse/GROOVY-980
       */
      // if (c != '\n' && c != '\r') {
      sw.append((char) c);
      // }
    }
    elements.add(new ParseElement(global, sw.toString()));
    // sw.write(";\nout.print(\"");
  }

  private void groovyComment(Reader reader, List<ParseElement> elements) throws IOException
  {
    StringBuilder sw = new StringBuilder();
    int c;
    while ((c = reader.read()) != -1) {
      if (c == '-') {
        c = reader.read();
        if (c == '-') {
          c = reader.read();
          if (c == '%') {
            c = reader.read();
            if (c == '>') {
              break;
            } else {
              sw.append("--%");
            }
          } else {
            sw.append("--");
          }
        } else {
          sw.append("-");
        }
      } else {
        sw.append((char) c);
      }
    }
    elements.add(new ParseElement(Type.Comment, sw.toString()));
  }

  public int getFlags()
  {
    return flags;
  }

  public void setFlags(int flags)
  {
    this.flags = flags;
  }

  public void setScript(Script script)
  {
    this.script = script;
  }
}
