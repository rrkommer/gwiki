/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   23.10.2009
// Copyright Micromata 23.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspTag;

import org.apache.taglibs.standard.lang.jstl.Constants;
import org.apache.taglibs.standard.lang.jstl.ELEvaluator;
import org.apache.taglibs.standard.lang.jstl.ELException;
import org.apache.taglibs.standard.lang.jstl.JSTLVariableResolver;

/**
 * Only reason for this class, because instead of org.apache.taglibs.standard.lang.support.ExpressionEvaluator it accepts not only Tag but
 * JspTag. Copy of org.apache.taglibs.standard.lang.jstl.Evaluator
 * 
 * @author roger@micromata.de
 * 
 */
public class JspTagEvaluator
{
  // -------------------------------------
  // Properties
  // -------------------------------------

  // -------------------------------------
  // Member variables
  // -------------------------------------

  /** The singleton instance of the evaluator **/
  static ELEvaluator sEvaluator = new ELEvaluator(new JSTLVariableResolver());

  // -------------------------------------
  // ExpressionEvaluator methods
  // -------------------------------------
  /**
   * 
   * Translation time validation of an attribute value. This method will return a null String if the attribute value is valid; otherwise an
   * error message.
   **/
  public String validate(String pAttributeName, String pAttributeValue)
  {
    try {
      sEvaluator.parseExpressionString(pAttributeValue);
      return null;
    } catch (ELException exc) {
      return MessageFormat.format(Constants.ATTRIBUTE_PARSE_EXCEPTION, new Object[] { "" + pAttributeName, "" + pAttributeValue,
          exc.getMessage()});
    }
  }

  // -------------------------------------
  /**
   * 
   * Evaluates the expression at request time
   **/
  public Object evaluate(String pAttributeName, String pAttributeValue, Class< ? > pExpectedType, JspTag pTag, PageContext pPageContext,
      Map functions, String defaultPrefix) throws JspException
  {
    try {
      return sEvaluator.evaluate(pAttributeValue, pPageContext, pExpectedType, functions, defaultPrefix);
    } catch (ELException exc) {
      throw new JspException(MessageFormat.format(Constants.ATTRIBUTE_EVALUATION_EXCEPTION, new Object[] { "" + pAttributeName,
          "" + pAttributeValue, exc.getMessage(), exc.getRootCause()}), exc.getRootCause());
    }
  }

  /** Conduit to old-style call for convenience. */
  public Object evaluate(String pAttributeName, String pAttributeValue, Class pExpectedType, JspTag pTag, PageContext pPageContext)
      throws JspException
  {
    return evaluate(pAttributeName, pAttributeValue, pExpectedType, pTag, pPageContext, null, null);
  }

  // -------------------------------------
  // Testing methods
  // -------------------------------------
  /**
   * 
   * Parses the given attribute value, then converts it back to a String in its canonical form.
   **/
  public static String parseAndRender(String pAttributeValue) throws JspException
  {
    try {
      return sEvaluator.parseAndRender(pAttributeValue);
    } catch (ELException exc) {
      throw new JspException(MessageFormat.format(Constants.ATTRIBUTE_PARSE_EXCEPTION, new Object[] { "test", "" + pAttributeValue,
          exc.getMessage()}));
    }
  }

  // -------------------------------------

}
