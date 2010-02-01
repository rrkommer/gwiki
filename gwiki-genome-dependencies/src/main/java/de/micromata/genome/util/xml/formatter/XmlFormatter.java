/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   03.07.2009
// Copyright Micromata 03.07.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.xml.formatter;

import java.util.StringTokenizer;

/**
 * Formatiert einen XML String mit Einrückung
 * 
 * @author roger@micromata.de
 * 
 */
public class XmlFormatter
{
  public static enum XmlEl
  {
    ElOpen, ElOpenClosed, ElClosed, XmlDecl, Comment, Text
  }

  public static class XmlTokenizer
  {
    private String input;

    private String pushbackToken;

    private String lastToken;

    StringTokenizer stk;

    public XmlTokenizer(String input)
    {
      this.input = input;
      stk = new StringTokenizer(input, "<?!/>--", true);
    }

    private String parseText()
    {
      StringBuilder sb = new StringBuilder();
      String tk = null;
      while ((tk = nextToken()) != null) {
        if (tk.equals("<") == true) {
          pushBack(tk);
          return sb.toString();
        }
        sb.append(tk);
      }
      return sb.toString();
    }

    private String parseElement()
    {
      StringBuilder sb = new StringBuilder();
      String tk = null;
      while ((tk = nextToken()) != null) {
        if (tk.equals(">") == true) {
          sb.append(tk);
          return sb.toString();
        }
        sb.append(tk);
      }
      return sb.toString();
    }

    private String parseComment()
    {
      StringBuilder sb = new StringBuilder();
      String tk = null;
      while ((tk = nextToken()) != null) {
        if (tk.equals("-") == true) {
          tk = nextToken();
          if (tk.equals("-") == true) {
            tk = nextToken();
            if (tk.equals(">") == true) {
              sb.append("-->");
              return sb.toString();
            } else {
              sb.append("--");
              sb.append(tk);
            }
          } else {
            sb.append("-");
            sb.append(tk);
          }
        } else {
          sb.append(tk);
        }
      }
      return sb.toString();
    }

    private String nextToken()
    {
      if (pushbackToken != null) {
        String ret = pushbackToken;
        pushbackToken = null;
        return ret;
      }
      if (stk.hasMoreTokens() == false)
        return null;
      return stk.nextToken();
    }

    private void pushBack(String tk)
    {
      if (this.pushbackToken == null) {
        this.pushbackToken = tk;
      } else {
        this.pushbackToken = pushbackToken + tk;
      }
    }

    /**
     * return null, if non
     * 
     * @return
     */
    public XmlEl nextElem()
    {
      String tk = nextToken();
      if (tk == null)
        return null;
      if (tk.equals("<") == false) {
        pushBack(tk);
        lastToken = parseText();
        return XmlEl.Text;
      }
      tk = nextToken();
      if (tk.equals("!") == true) {
        pushBack("<!");
        lastToken = parseComment();
        return XmlEl.Comment;
      } else if (tk.equals("?") == true) {
        pushBack("<?");
        lastToken = parseElement();
        return XmlEl.XmlDecl;
      } else {
        pushBack("<");
        pushBack(tk);
        lastToken = parseElement();
        if (lastToken.startsWith("</") == true) {
          return XmlEl.ElClosed;
        } else if (lastToken.endsWith("/>") == true) {
          return XmlEl.ElOpenClosed;
        } else {
          return XmlEl.ElOpen;
        }
      }
    }

    public String getLastToken()
    {
      return lastToken;
    }

    public String getInput()
    {
      return input;
    }
  }

  public static String indent(String s, int count)
  {
    if (count == 0)
      return "";
    if (count == 1)
      return s;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < count; ++i) {
      sb.append(s);
    }
    return sb.toString();
  }

  /**
   * Format a XML string
   * 
   * if an error occours, returns the original xml string
   * 
   * @param xml
   * @param indentString String to use indent
   * @return
   */
  public static String formatXml(String xml, String indentString)
  {
    try {
      if (xml == null)
        return xml;

      return formatXmlInternal(xml, indentString);
    } catch (RuntimeException ex) {
      // now warn here
      return xml;
    }
  }

  public static String formatXmlInternal(String xml, String indentString)
  {
    StringBuilder sb = new StringBuilder();
    XmlTokenizer xt = new XmlTokenizer(xml);
    int curIndent = 0;
    XmlEl el;
    XmlEl lastEl = null;
    while ((el = xt.nextElem()) != null) {
      switch (el) {
        case Comment:
        case Text:
          sb.append(xt.getLastToken());
          break;
        case ElOpen:
          sb.append("\n").append(indent(indentString, curIndent)).append(xt.getLastToken());
          ++curIndent;
          break;
        case ElClosed:
          --curIndent;
          if (lastEl == XmlEl.Text || lastEl == XmlEl.Comment) {
            sb.append(xt.getLastToken());
          } else {
            sb.append("\n").append(indent(indentString, curIndent)).append(xt.getLastToken());
          }
          break;
        case XmlDecl:
        case ElOpenClosed:
          sb.append("\n").append(indent(indentString, curIndent)).append(xt.getLastToken());
          break;

      }
      lastEl = el;
    }
    return sb.toString();
  }
}
