/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   19.11.2006
// Copyright Micromata 19.11.2006
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.Script;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagAdapter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.taglibs.standard.tag.el.core.OutTag;

import de.micromata.genome.gwiki.page.gspt.jdkrepl.PrintWriterPatched;
import de.micromata.genome.util.bean.SoftCastPropertyUtilsBean;
import de.micromata.genome.util.types.Pair;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class TagSupport
{
  private static final Logger log = Logger.getLogger(TagSupport.class);

  private static JspTagEvaluator jspTagEvaluator = new JspTagEvaluator();

  private static void setAttributes(JspTag tag, List<Object> attributes, ChildPageContext ctx) throws Exception
  {
    SoftCastPropertyUtilsBean beanInstance = ctx.getSoftCastPropertyUtilsBean();

    for (int i = 0; i < attributes.size() - 1; i = i + 2) {
      try {
        beanInstance.setProperty(tag, (String) attributes.get(i), convert(attributes.get(i + 1)));
      } catch (IllegalArgumentException e) {
        throw e;
      }
    }
  }

  private static Object convert(Object value)
  {
    if (value instanceof GString) {
      return ((GString) value).toString();
    }
    return value;
  }

  private static List<Object> evalAttributes(JspTag tag, List<Object> attributes, ChildPageContext ctx) throws JspException
  {
    if (ctx.isEvaluateTagAttributes() == false)
      return attributes;
    List<Object> nattr = new ArrayList<Object>(attributes.size());
    for (int i = 0; i < attributes.size() - 1; i = i + 2) {
      String key = (String) attributes.get(i);
      nattr.add(key);
      Object value = attributes.get(i + 1);
      if ((value instanceof String) == false) {
        nattr.add(value);
        continue;
      }
      String sv = (String) value;
      if (StringUtils.indexOf(sv, "${") == -1) {
        nattr.add(value);
        continue;
      }
      Object r = jspTagEvaluator.evaluate(key, sv, Object.class, tag, ctx);
      nattr.add(r);
    }
    return nattr;
  }

  //
  // private static List<Object> evalAttributes(Tag tag, List<Object> attributes, ChildPageContext ctx) throws JspException
  // {
  // if (ctx.isEvaluateTagAttributes() == false)
  // return attributes;
  // List<Object> nattr = new ArrayList<Object>(attributes.size());
  // for (int i = 0; i < attributes.size() - 1; i = i + 2) {
  // String key = (String) attributes.get(i);
  // nattr.add(key);
  // Object value = attributes.get(i + 1);
  // if ((value instanceof String) == false) {
  // nattr.add(value);
  // continue;
  // }
  // String sv = (String) value;
  // if (StringUtils.indexOf(sv, "${") == -1) {
  // nattr.add(value);
  // continue;
  // }
  // Object r = ExpressionEvaluatorManager.evaluate(key, sv, Object.class, tag, ctx);
  // nattr.add(r);
  // }
  // return nattr;
  // }

  /**
   * @return if return false page will be skipped
   */
  public static boolean initSimpleTag(Tag tag, List<Object> attributes, ChildPageContext ctx) throws Exception
  {
    if (log.isDebugEnabled())
      log.debug("Init simple tag: " + tag.getClass().getName());
    Tag ptag = ctx.getCurrentTag();
    PageContext pctx = ctx;// .getBrickContext().getPageContext();
    if (ctx.isUseParentTagContext() == true)
      pctx = ctx.getParentPageContext();
    tag.setPageContext(pctx);
    tag.setParent(ptag);
    attributes = evalAttributes(tag, attributes, ctx);
    setAttributes(tag, attributes, ctx);
    ctx.setCurrentTag(tag);
    int r = tag.doStartTag();
    // ctx.getTagStack().push(new Pair<Tag, Integer>(tag, r));
    r = tag.doEndTag();
    // ctx.getTagStack().pop();
    ctx.setCurrentTag(ptag);
    return r != javax.servlet.jsp.tagext.Tag.SKIP_PAGE;
  }

  public static void initSimpleSimpleTag(SimpleTag tag, List<Object> attributes, final ChildPageContext ctx, final Closure body)
      throws Exception
  {
    if (log.isDebugEnabled())
      log.debug("Init simple tag: " + tag.getClass().getName());
    Tag ptag = ctx.getCurrentTag();
    final JspContext pctx;// .getBrickContext().getPageContext();
    if (ctx.isUseParentTagContext() == true) {
      pctx = ctx.getParentPageContext();
    } else {
      pctx = ctx;
    }
    tag.setJspContext(pctx);
    tag.setParent(ptag);
    attributes = evalAttributes(tag, attributes, ctx);
    setAttributes(tag, attributes, ctx);
    final TagAdapter tagAdapter = new TagAdapter(tag);
    ctx.setCurrentTag(tagAdapter);
    tag.setJspBody(new JspFragment() {
      @Override
      public JspContext getJspContext()
      {
        return pctx;
      }

      @Override
      public void invoke(Writer out) throws JspException, IOException
      {
        ctx.getTagStack().push(new Pair<Tag, Integer>(tagAdapter, -1));
        try {
          JspWriter oldOut = null;
          try {
            if (pctx instanceof ChildPageContext) {
              oldOut = pctx.getOut();
              if (out != oldOut) {
                ((ChildPageContext) pctx).setInternalGroovyOut(createPrintWriter(out));
              } else {
                oldOut = null;
              }
            }
            body.call();
          } finally {
            if (oldOut != null) {
              pctx.getOut().flush();
              ((ChildPageContext) pctx).setInternalGroovyOut(createPrintWriter(oldOut));
            }
          }
        } finally {
          ctx.getTagStack().pop();
        }
      }
    });
    tag.doTag();
    ctx.setCurrentTag(ptag);
  }

  public static boolean initTag(Script script, Tag tag, List<Object> attributes, ChildPageContext ctx) throws Exception
  {
    boolean ret = initTag(tag, attributes, ctx);
    if (ret == true) {
      script.getBinding().setVariable("out", ctx.getInternalGroovyOut());
    }
    return ret;
  }

  /**
   * @return true if body should be included
   */
  public static boolean initTag(Tag tag, List<Object> attributes, ChildPageContext ctx) throws Exception
  {
    Tag ptag = ctx.getCurrentTag();
    if (log.isDebugEnabled())
      log.debug("Init tag: " + tag.getClass().getName());

    PageContext pctx = ctx;
    if (ctx.isUseParentTagContext() == true)
      pctx = ctx.getParentPageContext();
    tag.setPageContext(pctx);
    tag.setParent(ptag);
    attributes = evalAttributes(tag, attributes, ctx);
    setAttributes(tag, attributes, ctx);
    ctx.setCurrentTag(tag);
    int r = tag.doStartTag();
    ctx.getTagStack().push(new Pair<Tag, Integer>(tag, r));
    JspWriter oout = pctx.getOut();
    ctx.setInternalGroovyOut(createPrintWriter(oout));
    if (r != javax.servlet.jsp.tagext.Tag.SKIP_BODY && r != javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE && tag instanceof BodyTag) {

      BodyTag btag = (BodyTag) tag;

      JspWriter out = pctx.pushBody();
      PrintWriterPatched newPout = createPrintWriter(out, true);
      ctx.setInternalGroovyOut(newPout);

      btag.setBodyContent((javax.servlet.jsp.tagext.BodyContent) out);
      btag.doInitBody();
    }
    boolean ret = r != javax.servlet.jsp.tagext.Tag.SKIP_BODY;
    if (log.isDebugEnabled())
      log.debug("Init tag: " + tag.getClass().getName() + ": " + ret);
    return ret;
  }

  public static boolean closedBodyTag(Script script, Tag tag, List<Object> attributes, ChildPageContext ctx) throws Exception
  {
    if (initTag(script, tag, attributes, ctx)) {
      while (true) {
        if (continueAfterBody(script, ctx) == false)
          break;
      }
      afterBody(script, ctx);
    }
    return endTag(script, ctx);
  }

  public static String dummy(Object o)
  {
    return "";
  }

  public static boolean continueAfterBody(Script script, ChildPageContext ctx) throws Exception
  {
    return continueAfterBody(ctx);
  }

  public static boolean continueAfterBody(ChildPageContext ctx) throws Exception
  {
    if (ctx.getTagStack().size() == 0)
      return false;
    Pair<Tag, Integer> tt = ctx.getTagStack().lastElement();
    Tag t = tt.getFirst();
    boolean ret = false;
    if (t instanceof IterationTag) {
      IterationTag bt = (IterationTag) t;
      int r = bt.doAfterBody();
      ret = javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN == r;
    }
    if (log.isDebugEnabled() == true)
      log.debug("continueAfterBody: " + t.getClass().getName() + ": " + ret);

    return ret;
  }

  public static void afterBody(Script script, ChildPageContext ctx) throws Exception
  {
    afterBody(ctx);
  }

  public static PrintWriterPatched createPrintWriter(Writer out)
  {
    return new PrintWriterPatched(out, false, "\n");
  }

  public static PrintWriterPatched createPrintWriter(Writer out, boolean autoFlush)
  {
    return new PrintWriterPatched(out, autoFlush, "\n");
  }

  public static void afterBody(ChildPageContext ctx) throws Exception
  {
    Pair<Tag, Integer> tt = ctx.getTagStack().lastElement();
    Tag t = tt.getFirst();
    if (log.isDebugEnabled() == true)
      log.debug("afterBody: " + t.getClass().getName());
    if (tt.getSecond() != Tag.EVAL_BODY_INCLUDE && tt.getSecond() != javax.servlet.jsp.tagext.Tag.SKIP_BODY && t instanceof BodyTag) {
      JspWriter couts = ctx.popBody();

      // JspWriter cout = ctx.getPageContext().getOut();
      // TODO Rx minor new PrintWriter costs a lot because:
      // public PrintWriter(Writer out,
      // boolean autoFlush) {
      // super(out);
      // this.out = out;
      // this.autoFlush = autoFlush;
      // ooohhhh
      // lineSeparator = (String) java.security.AccessController.doPrivileged(
      // new sun.security.action.GetPropertyAction("line.separator"));
      // }
      PrintWriterPatched nout = createPrintWriter(couts);
      ctx.setInternalGroovyOut(nout);
      // ctx.getBinding().setProperty("out", nout);
    }

  }

  public static Writer getJspOut(ChildPageContext ctx)
  {
    return ctx.getOut();
  }

  public static boolean endTag(Script script, ChildPageContext ctx) throws Exception
  {
    script.getBinding().setVariable("out", ctx.getInternalGroovyOut());
    return endTag(ctx);
  }

  /**
   * @return true if continue
   */
  public static boolean endTag(ChildPageContext ctx) throws Exception
  {
    Pair<Tag, Integer> tt = ctx.getTagStack().pop();
    Tag t = tt.getFirst();
    int r = t.doEndTag();
    if (ctx.getTagStack().size() > 0)
      ctx.setCurrentTag(ctx.getTagStack().lastElement().getFirst());
    else
      ctx.setCurrentTag(null);
    boolean ret = r != javax.servlet.jsp.tagext.Tag.SKIP_PAGE;
    if (log.isDebugEnabled() == true)
      log.debug("endTag: " + t.getClass().getName() + ": " + ret);
    return ret;
  }

  public static void printEvalInlineElExpression(ChildPageContext ctx, String text) throws Exception
  {
    OutTag out = new OutTag();
    out.setPageContext(ctx);

    out.setEscapeXml("true");
    out.setValue(text);
    out.doStartTag();
    out.doEndTag();
  }
}
