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

package de.micromata.genome.gwiki.plugin.confluenceimporter_1_0.confluence;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import de.micromata.genome.gwiki.model.GWikiProps;

/**
 * A confluence element in the import file.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class ConfluenceElement extends Entity
{
  private String originalVersion;

  protected String createdBy;

  protected String createdAt;

  protected String modifiedAt;

  protected String modifiedBy;

  protected String version;

  public ConfluenceElement(Element el)
  {
    super(el);
    createdBy = selectText("property[@name=\"creatorName\"]/child::text()");
    createdAt = selectText("property[@name=\"creationDate\"]/child::text()");
    createdAt = convertDate(createdAt);
    modifiedBy = selectText("property[@name=\"lastModifierName\"]/child::text()");
    modifiedAt = selectText("property[@name=\"lastModificationDate\"]/child::text()");
    modifiedAt = convertDate(modifiedAt);
    originalVersion = selectText("property[@name=\"originalVersion\"]/id/child::text()");
    // version = = selectText("property[@name=\"version\"]/id/child::text()");
  }

  public boolean isArchive()
  {
    return StringUtils.isNotEmpty(originalVersion);
  }

  private static ThreadLocal<SimpleDateFormat> confDate = new ThreadLocal<SimpleDateFormat>() {

    @Override
    protected SimpleDateFormat initialValue()
    {

      // 2006-05-25 07:24:09.936
      return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    }
  };

  protected String convertDate(String s)
  {
    if (StringUtils.isEmpty(s) == true) {
      return s;
    }
    try {
      Date d = confDate.get().parse(s);
      return GWikiProps.internalTimestamp.get().format(d);
    } catch (ParseException ex) {
      // TODO log
      return "";
    }
  }

  public String getCreatedBy()
  {
    return createdBy;
  }

  public void setCreatedBy(String createdBy)
  {
    this.createdBy = createdBy;
  }

  public String getCreatedAt()
  {
    return createdAt;
  }

  public void setCreatedAt(String createdAt)
  {
    this.createdAt = createdAt;
  }

  public String getModifiedAt()
  {
    return modifiedAt;
  }

  public void setModifiedAt(String modifiedAt)
  {
    this.modifiedAt = modifiedAt;
  }

  public String getModifiedBy()
  {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy)
  {
    this.modifiedBy = modifiedBy;
  }

  public static ThreadLocal<SimpleDateFormat> getConfDate()
  {
    return confDate;
  }

  public static void setConfDate(ThreadLocal<SimpleDateFormat> confDate)
  {
    ConfluenceElement.confDate = confDate;
  }

  public String getOriginalVersion()
  {
    return originalVersion;
  }

  public void setOriginalVersion(String originalVersion)
  {
    this.originalVersion = originalVersion;
  }

  public String getVersion()
  {
    return version;
  }

  public void setVersion(String version)
  {
    this.version = version;
  }

}
