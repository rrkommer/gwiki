/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   19.10.2009
// Copyright Micromata 19.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import groovy.lang.Script;
import groovy.text.Template;

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
import de.micromata.genome.util.runtime.RuntimeIOException;

public class GenomeTemplateUtils
{
  private static boolean servletApi24 = true;

  private static boolean saveGsp = false;

  private static boolean saveGroovy = false;

  public static PageContext initPageContext(GWikiContext ctx)
  {
    // return initPageContext(ctx, JspFactory.getDefaultFactory());
    return initPageContext2(ctx, JspFactory.getDefaultFactory());
  }

  /**
   * for GWAR2
   * 
   * @param ctx
   * @param factosry
   * @return
   */
  public static PageContext initPageContext2(GWikiContext ctx, JspFactory factory)
  {
    ServletContext servletContext = null;// this.getServletContext();
    HttpServlet servlet = ctx.getServlet();
    servletContext = servlet.getServletContext();
    // de.micromata.web.gwar.GspStandardApplication app = (de.micromata.web.gwar.GspStandardApplication) ctx.getRequest().getAttribute(
    // "application");
    // HttpServlet gwarServlet = (HttpServlet) ctx.getRequest().getAttribute("GWarServlet");

    PageContext rpageContext = factory.getPageContext(servlet, ctx.getRequest(), // req
        ctx.getResponse(), // res
        null, // error page url
        true, // need session
        ctx.getResponse().getBufferSize(), true); // autoflush
    // PageContext pageContext = rpageContext;
    GspPageContext pageContext = new GspPageContext(rpageContext);
    // String newRoot = app.getLocalURI();
    // HttpServletRequest newReq = new NestedHttpServletRequest((HttpServletRequest) rpageContext.getRequest(), newRoot);
    //
    // GspPageContext pageContext = new GspPageContext(rpageContext);
    // pageContext.setNewRootRequest(newReq);
    // pageContext.setServletContext(new GServletContext(gwarServlet.getServletContext(), app));
    //
    // ServletConfig servletConfig = ctx.getServlet().getServletConfig();
    // if (servletContext == null)
    // servletContext = (ServletContext) ctx.getRequestAttribute(PageContext.APPLICATION);

    // pageContext.getAttribute(PageContext.PAGE)
    // SESSION
    // REQUEST
    // PAGECONTEXT
    // OUT
    // EXCEPTION
    pageContext.setAttribute(PageContext.APPLICATION, servletContext);
    // pageContext.setAttribute(PageContext.CONFIG, servletConfig);
    pageContext.setAttribute(PageContext.PAGE, ctx.getWikiElement());
    pageContext.setAttribute("pageContext", pageContext);
    pageContext.setAttribute("gspPageContext", pageContext);
    if (pageContext instanceof GspPageContext) {
      ChildPageContext gsptContext = (ChildPageContext) pageContext;
      gsptContext.setEvaluateTagAttributes(true);
    }
    for (Map.Entry<String, GWikiArtefakt< ? >> me : ctx.getParts().entrySet()) {
      pageContext.setAttribute(me.getKey(), me.getValue());
    }
    return pageContext;
  }

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

    // String newRoot = app.getLocalURI();
    // HttpServletRequest newReq = new NestedHttpServletRequest((HttpServletRequest) rpageContext.getRequest(), newRoot);
    // HttpServletRequest newReq = ctx.getRequest();
    // GspPageContext pageContext = new GspPageContesxt(rpageContext);
    // pageContext.setNewRootRequest(newReq);
    // pageContext.setServletContext(new GServletContext(gwarServlet.getServletContext(), app));
    PageContext pageContext = rpageContext;
    ServletConfig servletConfig = ctx.getServlet().getServletConfig();
    if (servletContext == null)
      servletContext = (ServletContext) ctx.getRequestAttribute(PageContext.APPLICATION);

    // pageContext.getAttribute(PageContext.PAGE)
    // SESSION
    // REQUEST
    // PAGECONTEXT
    // OUT
    // EXCEPTION
    pageContext.setAttribute(PageContext.APPLICATION, servletContext);
    pageContext.setAttribute(PageContext.CONFIG, servletConfig);
    pageContext.setAttribute(PageContext.PAGE, ctx.getWikiElement());
    pageContext.setAttribute("pageContext", pageContext);
    pageContext.setAttribute("gspPageContext", pageContext);
    if (pageContext instanceof GspPageContext) {
      ChildPageContext gsptContext = (ChildPageContext) pageContext;
      gsptContext.setEvaluateTagAttributes(true);
    }
    for (Map.Entry<String, GWikiArtefakt< ? >> me : ctx.getParts().entrySet()) {
      pageContext.setAttribute(me.getKey(), me.getValue());
    }
    return pageContext;
  }

  public static Map<String, Object> createBinding(GWikiContext ctx)
  {
    Map<String, Object> context = new HashMap<String, Object>();
    if (ctx.getParts() != null) {
      for (Map.Entry<String, GWikiArtefakt< ? >> me : ctx.getParts().entrySet()) {
        context.put(me.getKey(), me.getValue());
      }
    }
    context.put("pageContext", ctx.getCreatePageContext());
    context.put("wikiContext", ctx);
    context.put("wikiPage", ctx.getWikiElement());
    return context;
  }

  public static Template compile(GWikiContext ctx, String text)
  {
    boolean store = false;
    GsptTemplateEngine engine = new GsptTemplateEngine();
    Map<String, Object> context = createBinding(ctx);

    String page = ctx.getWikiElement().getElementInfo().getId() + "Template.gspt";
    GsptPreprocessor processor = new GsptPreprocessor(page, context, servletApi24);
    // text = processor.preprocess(appStorage, text, pageContext);
    processor.addReplacer(new GWikiIncludeReplacer(ctx));

    // processor.addReplacer(new FormStartReplacer());
    // processor.addReplacer(new SimpleReplacer("</html:form>", "</form>"));
    processor.useStdReplacer(context);
    ReplacerContext replctx = ReplacerContext.createReplacer(page, context);
    text = processor.preprocess(replctx, text);
    if (store == true && saveGsp == true) {
      // TODO appStorage.storeFile(page.replace(".gspt", ".gsp"), text.getBytes());
    }
    // DaoManager.get().getLogging().debug(Category.Coding, "gspt compiled", new LogAttribute(AttributeType.Miscellaneous, text));
    // log.error("gspt compiled: " + text);
    Template template = null;
    try {
      template = engine.createTemplate(text);
      if (store == true && saveGroovy == true) {
        // TODO appStorage.storeFile(page.replace(".gspt", ".groovy"), engine.groovySource.getBytes());
      }

      return template;
    } catch (Exception ex) {
      if (store == true && saveGroovy == true && engine.groovySource != null) {
        // TODO appStorage.storeFile(page.replace(".gspt", ".groovy"), engine.groovySource.getBytes());
      }

      final List<Integer> errorLineNumbers = new ArrayList<Integer>();
      if (ex instanceof MultipleCompilationErrorsException) {
        List< ? > errorMessages = ((MultipleCompilationErrorsException) ex).getErrorCollector().getErrors();
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
        ((HttpServletResponse) ctx.getResponse()).sendError(HttpServletResponse.SC_NOT_FOUND);
      } catch (IOException ex) {
        throw new RuntimeIOException(ex);
      }
      return;
    }
    PageContext pageContext = ctx.getPageContext();
    if (pageContext == null) {
      pageContext = initPageContext(ctx);
      ctx.setPageContext(pageContext);
    }
    Map<String, Object> m = new HashMap<String, Object>();
    for (Map.Entry<String, GWikiArtefakt< ? >> me : ctx.getParts().entrySet()) {
      m.put(me.getKey(), me.getValue());
    }
    JspWriter orgOut = pageContext.getOut();

    m.put("pageContext", pageContext);
    m.put("application", pageContext.getRequest().getAttribute("application"));
    m.put("wikiPage", ctx.getWikiElement());
    m.put("wikiContext", ctx);
    pageContext.setAttribute("wikiPage", ctx.getWikiElement());
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
      throw new RuntimeException("IO failure execution Groovy", ex);
    } catch (Exception e) {
      throw new RuntimeException("Gspt; Exception in gspt. id: " + ctx.getWikiElement().getElementInfo().getId() + "; " + e.getMessage(), e);
    }
  }

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
