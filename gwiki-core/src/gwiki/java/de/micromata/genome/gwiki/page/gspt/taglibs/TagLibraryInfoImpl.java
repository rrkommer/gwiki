/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   16.12.2006
// Copyright Micromata 16.12.2006
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt.taglibs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.micromata.genome.gwiki.model.GWikiLog;

public class TagLibraryInfoImpl extends TagLibraryInfo
{
  protected PageContext pageContext;

  public TagLibraryInfoImpl(PageContext pageContext, String prefix, String uri)
  {
    super(prefix, uri);
    this.pageContext = pageContext;
    loadTagLibary(uri);
  }

  public static class TagTmpAttributeInfo
  {
    String name;

    String type;

    boolean required;

    boolean rtexprvalue;

    public void setName(String name)
    {
      this.name = name;
    }

    public void setRequired(boolean required)
    {
      this.required = required;
    }

    public void setRequired(String required)
    {
      this.required = StringUtils.equals("true", required) == true;
    }

    public void setRtexprvalue(boolean rtexprvalue)
    {
      this.rtexprvalue = rtexprvalue;
    }

    public void setRtexprvalue(String rtexprvalue)
    {
      this.rtexprvalue = StringUtils.equals("true", rtexprvalue) == true;
      ;
    }

    public void setType(String type)
    {
      this.type = type;
    }

  }

  public static class TagTmpInfo
  {
    String tagName;

    String tagClassName;

    String bodycontent;

    String infoString;

    List<TagTmpAttributeInfo> attributes = new ArrayList<TagTmpAttributeInfo>();

    public void setBodycontent(String bodycontent)
    {
      this.bodycontent = bodycontent;
    }

    public void setInfoString(String infoString)
    {
      this.infoString = infoString;
    }

    public void setTagClassName(String tagClassName)
    {
      this.tagClassName = tagClassName;
    }

    public void setTagName(String tagName)
    {
      this.tagName = tagName;
    }

    public void addAttributeInfo(TagTmpAttributeInfo attr)
    {
      attributes.add(attr);
    }

    public TagAttributeInfo[] getAttributeArray()
    {
      TagAttributeInfo[] ta = new TagAttributeInfo[attributes.size()];
      for (int i = 0; i < ta.length; ++i) {
        TagTmpAttributeInfo tt = attributes.get(i);
        ta[i] = new TagAttributeInfo(tt.name, tt.required, tt.type, tt.rtexprvalue);
      }
      return ta;
    }
  }

  List<TagTmpInfo> tmpTags = new ArrayList<TagTmpInfo>();

  public void addTag(TagTmpInfo tagInfo)
  {
    tmpTags.add(tagInfo);
  }

  void rework()
  {
    super.tags = new TagInfo[tmpTags.size()];
    for (int i = 0; i < super.tags.length; ++i) {
      TagTmpInfo tt = tmpTags.get(i);
      super.tags[i] = new TagInfo(tt.tagName, tt.tagClassName, tt.bodycontent, tt.infoString, this, null, tt.getAttributeArray());
    }
  }

  protected void loadTagLibary(String uri)
  {
    Digester dig = new Digester();
    dig.setClassLoader(Thread.currentThread().getContextClassLoader());
    dig.setValidating(false);
    final EntityResolver parentResolver = dig.getEntityResolver();
    dig.setEntityResolver(new EntityResolver() {

      public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
      {

        InputStream is = null;
        if (StringUtils.equals(systemId, "http://java.sun.com/j2ee/dtds/web-jsptaglibrary_1_1.dtd") == true) {
          is = TagLibraryInfoImpl.class.getResourceAsStream("de/micromata/genome/gwiki/page/gspt/taglibs/web-jsptaglibrary_1_1.dtd");
        } else if (StringUtils.equals(systemId, "http://java.sun.com/dtd/web-jsptaglibrary_1_2.dtd") == true) {
          is = TagLibraryInfoImpl.class.getResourceAsStream("de/micromata/genome/gwiki/page/gspt/taglibs/web-jsptaglibrary_1_2.dtd");
        }
        if (is == null) {
          if (parentResolver == null) {
            GWikiLog.error("Cannot resolve entity: " + systemId);
            return null;
          }
          return parentResolver.resolveEntity(publicId, systemId);
        }
        InputSource source = new InputSource(is);
        source.setPublicId(publicId);
        source.setSystemId(systemId);
        return source;
      }
    });
    dig.addCallMethod("taglib/tlib-version", "setTlibversion", 0);
    dig.addCallMethod("taglib/tlibversion", "setTlibversion", 0);
    dig.addCallMethod("taglib/jsp-version", "setJspversion", 0);
    dig.addCallMethod("taglib/jspversion", "setJspversion", 0);
    dig.addCallMethod("taglib/short-name", "setShortname", 0);
    dig.addCallMethod("taglib/shortname", "setShortname", 0);
    dig.addObjectCreate("taglib/tag", TagTmpInfo.class);
    dig.addCallMethod("taglib/tag/name", "setTagName", 0);
    dig.addCallMethod("taglib/tag/description", "setInfoString", 0);
    dig.addCallMethod("taglib/tag/tag-class", "setTagClassName", 0);
    dig.addCallMethod("taglib/tag/tagclass", "setTagClassName", 0);

    dig.addCallMethod("taglib/tag/body-content", "setBodycontent", 0);
    dig.addCallMethod("taglib/tag/bodycontent", "setBodycontent", 0);

    dig.addObjectCreate("taglib/tag/attribute", TagTmpAttributeInfo.class);
    dig.addCallMethod("taglib/tag/attribute/name", "setName", 0);
    dig.addCallMethod("taglib/tag/attribute/required", "setRequired", 0);
    dig.addCallMethod("taglib/tag/attribute/rtexprvalue", "setRtexprvalue", 0);
    dig.addSetNext("taglib/tag/attribute", "addAttributeInfo");
    dig.addSetNext("taglib/tag", "addTag");

    dig.push(this);
    try {
      InputStream is = loadImpl(uri);
      if (is == null) {
        throw new RuntimeException("could not load tld '" + uri + "'");
      }
      /*
       * ByteArrayOutputStream baos = new ByteArrayOutputStream(); IOUtils.copy(is, baos); String text =
       * Converter.stringFromBytes(baos.toByteArray()); dig.parse(new StringReader(text));
       */
      dig.parse(is);
      rework();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  protected InputStream loadImpl(String uri) throws IOException
  {
    return pageContext.getServletContext().getResourceAsStream(uri);
  }

  public TagLibraryInfo[] getTagLibraryInfos()
  {
    return new TagLibraryInfo[0];
  }

  public void setInfo(String info)
  {
    this.info = info;
  }

  public void setJspversion(String jspversion)
  {
    this.jspversion = jspversion;
  }

  public void setShortname(String shortname)
  {
    this.shortname = shortname;
  }

  public void setTlibversion(String tlibversion)
  {
    this.tlibversion = tlibversion;
  }

  public void setUrn(String urn)
  {
    this.urn = urn;
  }

}
