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

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.util.runtime.RuntimeIOException;
import groovy.lang.Script;
import groovy.text.Template;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GenomeTemplateUtils
{

  /**
   * The servlet api24.
   */
  private static boolean servletApi24 = true;

  /**
   * The save gsp.
   */
  private static boolean saveGsp = false;

  /**
   * The save groovy.
   */
  private static boolean saveGroovy = false;

  /**
   * The in unitest.
   */
  public static boolean IN_UNITEST = false;

  /**
   * Inits the page context.
   *
   * @param ctx the ctx
   * @return the page context
   */
  public static PageContext initPageContext(GWikiContext ctx)
  {
    if (IN_UNITEST == false) {
      JspFactory fac = JspFactory.getDefaultFactory();
      if (fac == null) {
        fac = new StandaloneJspFactory();
      }
      return initPageContext2(ctx, fac);
    }
    return initPageContext2(ctx, new StandaloneJspFactory());

  }

  /**
   * for GWAR2.
   *
   * @param ctx the ctx
   * @param factory the factory
   * @return the page context
   */
  public static PageContext initPageContext2(GWikiContext ctx, JspFactory factory)
  {
    ServletContext servletContext = null;// this.getServletContext();
    HttpServlet servlet = ctx.getServlet();
    servletContext = servlet.getServletContext();
    PageContext orgPageContext = ctx.getPageContext();
    PageContext rpageContext = orgPageContext;
    if (rpageContext == null) {
      rpageContext = factory.getPageContext(servlet, ctx.getRequest(), // req
          ctx.getResponse(), // res
          null, // error page url
          true, // need session
          ctx.getResponse().getBufferSize(), true); // autoflush
    }
    GspPageContext pageContext = new GspPageContext(rpageContext);
    pageContext.setAttribute(PageContext.APPLICATION, servletContext);
    // pageContext.setAttribute(PageContext.CONFIG, servletConfig);
    pageContext.setAttribute(PageContext.PAGE, ctx.getCurrentElement());
    pageContext.setAttribute("pageContext", pageContext);
    pageContext.setAttribute("gspPageContext", pageContext);
    if (pageContext instanceof GspPageContext) {
      ChildPageContext gsptContext = pageContext;
      gsptContext.setEvaluateTagAttributes(true);
    }
    for (Map.Entry<String, GWikiArtefakt<?>> me : ctx.getParts().entrySet()) {
      pageContext.setAttribute(me.getKey(), me.getValue());
    }
    return pageContext;
  }

  /**
   * Inits the page context.
   *
   * @param ctx the ctx
   * @param factory the factory
   * @return the page context
   */
  public static PageContext initPageContext(GWikiContext ctx, JspFactory factory)
  {
    ServletContext servletContext = null;// this.getServletContext();
    servletContext = ctx.getServlet().getServletContext();
    // GspStandardApplication app = (GspStandardApplication) ctx.getRequest().getAttribute("application");
    HttpServlet gwarServlet = (HttpServlet) ctx.getRequest().getAttribute("GWarServlet");

    PageContext rpageContext = factory.getPageContext(gwarServlet, ctx.getRequest(), // req
        ctx.getResponse(), // res
        null, // error page url
        true, // need session
        ctx.getResponse().getBufferSize(), true); // autoflush

    PageContext pageContext = rpageContext;
    ServletConfig servletConfig = ctx.getServlet().getServletConfig();
    if (servletContext == null) {
      servletContext = (ServletContext) ctx.getRequestAttribute(PageContext.APPLICATION);
    }

    pageContext.setAttribute(PageContext.APPLICATION, servletContext);
    pageContext.setAttribute(PageContext.CONFIG, servletConfig);
    pageContext.setAttribute(PageContext.PAGE, ctx.getCurrentElement());
    pageContext.setAttribute("pageContext", pageContext);
    pageContext.setAttribute("gspPageContext", pageContext);
    if (pageContext instanceof GspPageContext) {
      ChildPageContext gsptContext = (ChildPageContext) pageContext;
      gsptContext.setEvaluateTagAttributes(true);
    }
    for (Map.Entry<String, GWikiArtefakt<?>> me : ctx.getParts().entrySet()) {
      pageContext.setAttribute(me.getKey(), me.getValue());
    }
    return pageContext;
  }

  /**
   * Creates the binding.
   *
   * @param ctx the ctx
   * @return the map
   */
  public static Map<String, Object> createBinding(GWikiContext ctx)
  {
    Map<String, Object> context = new HashMap<String, Object>();
    if (ctx.getParts() != null) {
      for (Map.Entry<String, GWikiArtefakt<?>> me : ctx.getParts().entrySet()) {
        context.put(me.getKey(), me.getValue());
      }
    }
    context.put("pageContext", ctx.getCreatePageContext());
    context.put("wikiContext", ctx);
    context.put("wikiPage", ctx.getCurrentElement());
    return context;
  }

  /**
   * Compile.
   *
   * @param ctx the ctx
   * @param text the text
   * @return the template
   */
  public static Template compile(GWikiContext ctx, String text)
  {
    boolean store = false;
    GsptTemplateEngine engine = new GsptTemplateEngine();
    Map<String, Object> context = createBinding(ctx);

    String page = ctx.getCurrentElement().getElementInfo().getId() + "Template.gspt";
    GsptPreprocessor processor = new GsptPreprocessor(page, context, servletApi24);
    processor.addReplacer(new GWikiIncludeReplacer(ctx));
    processor.useStdReplacer(context);
    ReplacerContext replctx = ReplacerContext.createReplacer(page, context);
    text = processor.preprocess(replctx, text);
    if (store == true && saveGsp == true) {
      // TODO gwiki later appStorage.storeFile(page.replace(".gspt", ".gsp"), text.getBytes());
    }
    Template template = null;
    try {
      template = engine.createTemplate(text, context);
      if (store == true && saveGroovy == true) {
        // TODO appStorage.storeFile(page.replace(".gspt", ".groovy"), engine.groovySource.getBytes());
      }

      return template;
    } catch (Exception ex) {
      if (store == true && saveGroovy == true && engine.groovySource != null) {
        // TODO gwiki appStorage.storeFile(page.replace(".gspt", ".groovy"), engine.groovySource.getBytes());
      }

      final List<Integer> errorLineNumbers = new ArrayList<Integer>();
      if (ex instanceof MultipleCompilationErrorsException) {
        List<?> errorMessages = ((MultipleCompilationErrorsException) ex).getErrorCollector().getErrors();
        for (Object m : errorMessages) {
          if (m instanceof SyntaxErrorMessage) {
            errorLineNumbers.add(((SyntaxErrorMessage) m).getCause().getLine());
          }
        }
      }
      final String textOut;
      if (errorLineNumbers.size() > 0) {
        textOut = getLinesWithContext(engine.groovySource, errorLineNumbers, 15);
      } else {
        textOut = text;
      }
      /**
       * @logging
       * @reason Fehler beim Kompilieren eines gspt
       * @action TechAdmin oder Entwickler kontaktieren
       */
      throw new RuntimeException("Could not compile gspt page:" + page + "; " + ex.getMessage() + "\n\n" + textOut, ex);
    }
  }

  /**
   * Process page.
   *
   * @param templ the templ
   * @param ctx the ctx
   * @param parentScript the parent script
   */
  public static void processPage(Template templ, GWikiContext ctx, Script parentScript)
  {
    // ClassLoader cl = Thread.currentThread().getContextClassLoader();
    // Validate.isTrue(cl instanceof StorageClassLoader, "Aufruf von GspPageServlet ohne aktuellem StorageClassLoader");

    // String npage = "/" + page;
    // ctx.getRequest().setAttribute("gsptSourcePage", npage);
    ctx.getRequest().setAttribute("gsptServlet", ctx.getServlet());
    ExtendedTemplate template = (ExtendedTemplate) templ;
    if (template == null) {
      try {
        ctx.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
      } catch (IOException ex) {
        throw new RuntimeIOException(ex);
      }
      return;
    }
    PageContext pageContext = ctx.getPageContext();
    if (pageContext == null || (pageContext instanceof ChildPageContext) == false) {
      pageContext = initPageContext(ctx);
      ctx.setPageContext(pageContext);
    }
    Map<String, Object> m = new HashMap<String, Object>();
    for (Map.Entry<String, GWikiArtefakt<?>> me : ctx.getParts().entrySet()) {
      m.put(me.getKey(), me.getValue());
    }
    JspWriter orgOut = pageContext.getOut();

    m.put("pageContext", pageContext);
    m.put("application", pageContext.getRequest().getAttribute("application"));
    m.put("wikiPage", ctx.getCurrentElement());
    m.put("wikiContext", ctx);
    pageContext.setAttribute("wikiPage", ctx.getCurrentElement());
    pageContext.setAttribute("wikiContext", ctx);
    if (ctx.getNativeArgs() != null) {
      m.putAll(ctx.getNativeArgs());
      for (Map.Entry<String, Object> me : ctx.getNativeArgs().entrySet()) {
        pageContext.getRequest().setAttribute(me.getKey(), me.getValue());
      }
    }
    // in case struts is used.
    // pageContext.getRequest().setAttribute("org.apache.struts.action.MODULE", new DynModuleConfig());

    try {
      ((HttpServletRequest) pageContext.getRequest()).setCharacterEncoding("utf-8");
      template.make(m).writeTo(orgOut);
      if (orgOut instanceof BodyContent) {
        // nothing//((BodyContent)orgOut).f
      } else {
        orgOut.flush();
      }
      // Thread problem:
      // if (parentScript != null)
      // parentScript.setBinding(script.getBinding());
    } catch (IOException ex) {
      // handled outside
      throw new RuntimeIOException("IO failure execution Groovy", ex);
    } catch (RuntimeIOException ex) {
      throw ex;

    } catch (RuntimeException ex) {
      if (GWikiServlet.isIgnorableAppServeIOException(ex) == true) {
        throw ex;
      }
      throw new RuntimeException(
          "Gspt; Exception in gspt. id: " + ctx.getCurrentElement() == null ? "null" : ctx.getCurrentElement()
              .getElementInfo().getId()
              + "; "
              + ex.getMessage(),
          ex);

    } catch (Exception e) {
      throw new RuntimeException(
          "Gspt; Exception in gspt. id: " + ctx.getCurrentElement() == null ? "null" : ctx.getCurrentElement()
              .getElementInfo().getId()
              + "; "
              + e.getMessage(),
          e);

    }
  }

  /**
   * Gets the lines with context.
   *
   * @param text the text
   * @param errorLineNumbers the error line numbers
   * @param contextRadius the context radius
   * @return the lines with context
   */
  private static String getLinesWithContext(String text, List<Integer> errorLineNumbers, int contextRadius)
  {
    StringWriter sw = new StringWriter();
    LineNumberReader reader = new LineNumberReader(new StringReader(text));
    int i = 1;
    int remainingContextLines = 0;
    contextRadius++;
    String[] contextLines = new String[contextRadius];
    while (true) {
      try {
        contextLines[i % contextRadius] = reader.readLine();
      } catch (IOException ex) {
        sw.append("\nERROR\n");
        break;
      }
      boolean errorLine = errorLineNumbers.contains(i);
      if (errorLine == true) {
        remainingContextLines = contextRadius;
        int before = (i + 1) % contextRadius;
        int beforeInitial = i % contextRadius;
        do {
          if (contextLines[before] != null) {
            sw.append(contextLines[before]).append('\n');
            contextLines[before] = null;
          }
          before = (before + 1) % contextRadius;
        } while (before != beforeInitial);
        sw.append("ERROR IS HERE --> ");
      } else {
        if (contextLines[i % contextRadius] == null) {
          break;
        }
      }
      if (remainingContextLines > 0) {
        sw.append(contextLines[i % contextRadius]).append('\n');
        contextLines[i % contextRadius] = null;
        --remainingContextLines;
      }
      ++i;
    }
    return sw.toString();
  }
}
