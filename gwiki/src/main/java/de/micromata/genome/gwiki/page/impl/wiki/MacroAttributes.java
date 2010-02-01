/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   22.10.2009
// Copyright Micromata 22.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;

/**
 * parse and Held command args
 * 
 * @author roger@micromata.de
 * 
 */
public class MacroAttributes implements Serializable
{
  public static final String DEFAULT_VALUE_KEY = "defaultValue";

  private static final long serialVersionUID = -2591357184191470111L;

  private String cmd;

  private GWikiProps args = new GWikiProps();

  private String body;

  private GWikiFragmentChildContainer childFragment;

  private String requestPrefix;

  public MacroAttributes()
  {

  }

  public MacroAttributes(String text)
  {
    parse(text);
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    toString(sb);
    return sb.toString();
  }

  public String escapeValue(String value)
  {
    if (StringUtils.indexOfAny(value, new char[] { '|', '}', '{', '='}) == -1) {
      return value;
    }
    return '"' + StringEscapeUtils.escapeJavaScript(value) + '"';
  }

  public void toString(StringBuilder sb)
  {
    sb.append("{").append(getCmd());
    if (getArgs().isEmpty() == false) {
      String defSt = getDefaultValue();
      sb.append(":");
      boolean preBar = false;
      if (defSt != null) {
        sb.append(escapeValue(defSt));
        preBar = true;
      }
      for (Map.Entry<String, String> me : getArgs().getMap().entrySet()) {
        if (MacroAttributes.DEFAULT_VALUE_KEY.equals(me.getKey()) == true) {
          continue;
        }
        if (preBar == true) {
          sb.append("|");
        }
        sb.append(me.getKey()).append("=").append(escapeValue(me.getValue()));
        preBar = true;
      }
    }
    sb.append("}");
  }

  public void parse(String text)
  {
    // format cmd:key=value|key=value
    int idx = text.indexOf(':');
    if (idx == -1) {
      cmd = text;
      return;
    }
    cmd = text.substring(0, idx);
    String argstr = text.substring(idx + 1);
    args = new GWikiProps(MacroAttributesUtils.decode(argstr));
    return;
  }

  public static String escape(String k)
  {
    k = StringUtils.replace(k, "\\", "\\\\");
    k = StringUtils.replace(k, "|", "\\|");
    k = StringUtils.replace(k, "=", "\\=");
    return k;

  }

  public static String encode(Map<String, String> map)
  {
    if (map == null)
      return "";
    StringBuilder sb = new StringBuilder();
    boolean isFirst = true;
    for (Map.Entry<String, String> me : map.entrySet()) {
      if (isFirst == false)
        sb.append("|");
      isFirst = false;
      String k = me.getKey();
      k = escape(k);
      sb.append(k).append("=");
      String v = me.getValue();
      v = escape(v);
      sb.append(v);
    }
    return sb.toString();
  }

  public String getDefaultValue()
  {
    return getArgs().getStringValue(DEFAULT_VALUE_KEY);
  }

  public String getCmd()
  {
    return cmd;
  }

  public void setCmd(String cmd)
  {
    this.cmd = cmd;
  }

  public GWikiProps getArgs()
  {
    return args;
  }

  public void setArgs(GWikiProps args)
  {
    this.args = args;
  }

  public String getBody()
  {
    return body;
  }

  public void setBody(String body)
  {
    this.body = body;
  }

  public GWikiFragmentChildContainer getChildFragment()
  {
    return childFragment;
  }

  public void setChildFragment(GWikiFragmentChildContainer childFragment)
  {
    this.childFragment = childFragment;
  }

  public String getRequestPrefix()
  {
    return requestPrefix;
  }

  public void setRequestPrefix(String requestPrefix)
  {
    this.requestPrefix = requestPrefix;
  }

}
