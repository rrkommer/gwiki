package de.micromata.genome.gwiki.utils.html;

import org.apache.commons.lang.StringUtils;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;

import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;

public interface HtmlFilterListener
{
  public static class HtmlEvent
  {
    HtmlListenerRegistry registry;
    public QName element;
    public String el;
    public XMLAttributes attributes;
    public String text;
    public StringBuilder collectedText;

    public HtmlEvent(HtmlListenerRegistry registry, QName element)
    {
      this(registry, element, null);
    }

    public HtmlEvent(HtmlListenerRegistry registry, QName element, XMLAttributes attributes)
    {
      this.registry = registry;
      this.element = element;
      this.el = element.rawname.toLowerCase();
      this.attributes = attributes;
    }

    public HtmlEvent(HtmlListenerRegistry registry, String text, StringBuilder collectedText)
    {
      this.registry = registry;
      this.text = text;
      this.collectedText = collectedText;
    }

    public static void attrsToString(XMLAttributes attributes, StringBuilder sb)
    {
      if (attributes == null) {
        return;
      }
      for (int i = 0; i < attributes.getLength(); ++i) {
        sb.append(" ").append(attributes.getLocalName(i)).append("='").append(attributes.getValue(i)).append("'");
      }
    }

    @Override
    public String toString()
    {
      StringBuilder sb = new StringBuilder();
      if (el != null) {
        if (attributes != null) {
          sb.append("<").append(el);
          attrsToString(attributes, sb);
          sb.append(">");
        } else {
          sb.append("</").append(el);
          sb.append("/>");
        }
      }
      if (text != null) {
        sb.append(text);
      }
      return sb.toString();
    }

    public String getClassAttr()
    {
      return getAttrVal("class");
    }

    public String getAttrVal(String key)
    {
      return StringUtils.defaultString(attributes.getValue(key));
    }

    public boolean containsInStyleClass(String text)
    {
      String val = attributes.getValue("class");
      return StringUtils.contains(val, text);
    }

    public void flushText()
    {
      registry.html2WikiFilter.flushText();
    }

    public void resetText()
    {
      registry.html2WikiFilter.collectedText.setLength(0);
    }

    public String getCollectedText()
    {
      return registry.html2WikiFilter.collectedText.toString();
    }

    public Html2WikiFilter getHtmlFilter()
    {
      return registry.html2WikiFilter;
    }

    public GWikiWikiParserContext getParseContext()
    {
      return getHtmlFilter().parseContext;
    }
  }

  default int listenerPrio()
  {
    return 100;
  }

  default String listenerId()
  {
    return getClass().getName();
  }

  default boolean startElement(HtmlEvent event)
  {
    return true;
  }

  default boolean emptyElement(HtmlEvent event)
  {
    return true;
  }

  default boolean endElement(HtmlEvent cevent)
  {
    return true;
  }

  default boolean characters(HtmlEvent cevent)
  {
    return true;
  }
}
