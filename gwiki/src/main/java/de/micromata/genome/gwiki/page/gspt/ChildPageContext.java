////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Stack;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.Tag;

import de.micromata.genome.gwiki.page.gspt.jdkrepl.PrintWriterPatched;
import de.micromata.genome.util.bean.SoftCastPropertyUtilsBean;
import de.micromata.genome.util.types.Pair;

/**
 * PageContext with extensions for GWARs
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@SuppressWarnings("deprecation")
public abstract class ChildPageContext extends PageContext
{
  protected PageContext parentPageContext;

  private Stack<Pair<Tag, Integer>> tagStack = new Stack<Pair<Tag, Integer>>();

  private Tag currentTag = null;

  private boolean useParentTagContext = false;

  private String fileName;

  private int lineNo = 0;

  private PrintWriterPatched internalGroovyOut;

  private boolean evaluateTagAttributes = true;

  private SoftCastPropertyUtilsBean softCastPropertyUtilsBean;

  public ChildPageContext(PageContext parentPageContext)
  {
    this.parentPageContext = parentPageContext;
    this.softCastPropertyUtilsBean = SoftCastPropertyUtilsBean.getInstance();
  }

  public PrintWriterPatched getInternalGroovyOut()
  {
    return internalGroovyOut;
  }

  public void setInternalGroovyOut(PrintWriterPatched internalGroovyOut)
  {
    this.internalGroovyOut = internalGroovyOut;
  }

  public Stack<Pair<Tag, Integer>> getTagStack()
  {
    return tagStack;
  }

  public void setTagStack(Stack<Pair<Tag, Integer>> tagStack)
  {
    this.tagStack = tagStack;
  }

  public Tag getCurrentTag()
  {
    return currentTag;
  }

  public void setCurrentTag(Tag currentTag)
  {
    this.currentTag = currentTag;
  }

  public boolean isUseParentTagContext()
  {
    return useParentTagContext;
  }

  public void setUseParentTagContext(boolean useParentTagContext)
  {
    this.useParentTagContext = useParentTagContext;
  }

  public String getFileName()
  {
    return fileName;
  }

  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }

  public int getLineNo()
  {
    return lineNo;
  }

  public void setLineNo(int lineNo)
  {
    this.lineNo = lineNo;
  }

  @Override
  public JspWriter getOut()
  {
    return parentPageContext.getOut();
  }

  @Override
  public void initialize(Servlet arg0, ServletRequest arg1, ServletResponse arg2, String arg3, boolean arg4, int arg5, boolean arg6)
      throws IOException, IllegalStateException, IllegalArgumentException
  {
    parentPageContext.initialize(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
  }

  @Override
  public void release()
  {
    // TODO still problem. in JspWiki included buffers will flushed to late
    if (internalGroovyOut != null) {
      // try {
      internalGroovyOut.flush();
      // } catch (IOException ex) {
      // ex.printStackTrace(); // TODO
      // }
    }
    parentPageContext.release();
  }

  @Override
  public BodyContent pushBody()
  {
    return parentPageContext.pushBody();

  }

  @Override
  public JspWriter popBody()
  {
    return parentPageContext.popBody();
  }

  @Override
  public JspWriter pushBody(java.io.Writer writer)
  {
    return parentPageContext.pushBody(writer);
  }

  @Override
  public ExpressionEvaluator getExpressionEvaluator()
  {
    return parentPageContext.getExpressionEvaluator();
  }

  @Override
  public VariableResolver getVariableResolver()
  {
    return parentPageContext.getVariableResolver();
  }

  @SuppressWarnings({ "unchecked", "rawtypes"})
  @Override
  public Enumeration getAttributeNamesInScope(int arg0)
  {
    // TODO R3Vx (minor) ji likely to be wrong. Wenn Variable auf null gesetzt wird, diese aber auch in Parent ist,
    // wird ggf. die von Parent weitergeliefert
    return parentPageContext.getAttributeNamesInScope(arg0);
  }

  @Override
  public HttpSession getSession()
  {
    return parentPageContext.getSession();
  }

  @Override
  public void handlePageException(Exception arg0) throws ServletException, IOException
  {
    parentPageContext.handlePageException(arg0);

  }

  @Override
  public void handlePageException(Throwable arg0) throws ServletException, IOException
  {
    parentPageContext.handlePageException(arg0);
  }

  @Override
  public void include(String arg0) throws ServletException, IOException
  {
    include(arg0, true);
  }

  @Override
  public void include(String arg0, boolean flush) throws ServletException, IOException
  {
    if (flush == true) {
      if (internalGroovyOut != null) {
        internalGroovyOut.flush();
      }
      JspWriter swriter = getOut();
      if (swriter instanceof BodyFlusher) {
        ((BodyFlusher) swriter).flushBody();
      } else {
        // System.out.println("no bodyflusher");
      }
    }
    parentPageContext.include(arg0, flush);
  }

  @Override
  public ServletConfig getServletConfig()
  {
    return parentPageContext.getServletConfig();
  }

  @Override
  public ServletResponse getResponse()
  {
    return parentPageContext.getResponse();
  }

  @Override
  public void forward(String arg0) throws ServletException, IOException
  {
    parentPageContext.forward(arg0);

  }

  @Override
  public Exception getException()
  {
    return parentPageContext.getException();
  }

  @Override
  public Object getPage()
  {
    return parentPageContext.getPage();
  }

  @Override
  public ServletContext getServletContext()
  {
    return parentPageContext.getServletContext();
  }

  @Override
  public ServletRequest getRequest()
  {
    return parentPageContext.getRequest();
  }

  public PageContext getParentPageContext()
  {
    return parentPageContext;
  }

  // needed by 2.1
  // javax.el.ELContext getELContext()
  // {
  // return parentPageContext.getELContext();
  // }
  public boolean isEvaluateTagAttributes()
  {
    return evaluateTagAttributes;
  }

  public void setEvaluateTagAttributes(boolean evaluateTagAttributes)
  {
    this.evaluateTagAttributes = evaluateTagAttributes;
  }

  public SoftCastPropertyUtilsBean getSoftCastPropertyUtilsBean()
  {
    return softCastPropertyUtilsBean;
  }

  public void setSoftCastPropertyUtilsBean(SoftCastPropertyUtilsBean softCastPropertyUtilsBean)
  {
    this.softCastPropertyUtilsBean = softCastPropertyUtilsBean;
  }
}
