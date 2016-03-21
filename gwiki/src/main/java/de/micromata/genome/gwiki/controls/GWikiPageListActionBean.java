////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

package de.micromata.genome.gwiki.controls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.util.types.Converter;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

/**
 * Base implementation of listing elements/pages.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPageListActionBean extends ActionBeanBase
{
  /**
   * predefined filter expression. does not contains order by.
   * 
   * public, but no getter/setter.
   */
  public String fixedFilterExpression;

  private String filterExpression;

  private String fields;

  private String returnFormat = "xml";

  private int startRow;

  private int maxRows;

  private int page;

  private int rows;
  protected QueryResult queryResult;

  public String getjqGridSearchExpression()
  {
    if (StringUtils.equals(getReqParam("_search"), "true") == false) {
      return null;
    }
    //
    // String skey = getReqParam("sidx");
    // if (StringUtils.isBlank(skey) == true) {
    // return null;
    // }
    List<String> fl = Converter.parseStringTokens(fields, "|", false);
    StringBuilder sb = new StringBuilder();
    for (String f : fl) {
      String sval = getReqParam(f);
      if (StringUtils.isBlank(sval) == true) {
        continue;
      }
      List<String> args = Converter.parseStringTokens(sval, " ", false);
      for (String a : args) {
        if (sb.length() != 0) {
          sb.append(" and ");
        }
        sb.append("prop:" + f + " ~ " + a);
      }
    }
    if (sb.length() == 0) {
      return null;
    }
    return sb.toString();
  }

  protected String getjqGridOrderByExpression()
  {
    String sortBy = getReqParam("sidx");
    String sort = getReqParam("sord");
    if (StringUtils.isEmpty(sortBy) == true) {
      return null;
    }
    String order = StringUtils.equals(sort, "desc") ? "DESC" : "ASC";
    return "prop:" + sortBy + " " + order;
  }

  public String getInternalSearchExpression()
  {
    filterExpression = StringUtils.trim(filterExpression);
    fixedFilterExpression = StringUtils.trim(fixedFilterExpression);
    if (StringUtils.isEmpty(filterExpression) == true) {
      return fixedFilterExpression;
    }
    if (StringUtils.isEmpty(fixedFilterExpression) == true) {
      return filterExpression;
    }
    return fixedFilterExpression + " and " + filterExpression;
  }

  public String getSearchExpression()
  {
    String fe = getInternalSearchExpression();
    String jqsex = getjqGridSearchExpression();
    String order = getjqGridOrderByExpression();
    if (jqsex == null) {
      if (StringUtils.isEmpty(order) == true) {
        return fe;
      }
      if (StringUtils.isBlank(fe) == true) {
        return "order by " + order;
      }

      return fe + " order by " + order;
    }
    if (StringUtils.isBlank(fe) == true) {
      if (StringUtils.isBlank(order) == true) {
        return jqsex;
      }
      return jqsex + " order by " + order;
    }

    if (fe.contains("order by ") == true) {
      if (fe.startsWith("order by") == true) {
        return "(" + jqsex + ") and " + fe;
      } else {
        return "(" + jqsex + ") " + fe;
      }
    }
    String searchPart;
    if (StringUtils.isEmpty(fe) == true) {
      searchPart = jqsex;
    } else if (StringUtils.isEmpty(jqsex) == true) {
      searchPart = fe;
    } else {
      searchPart = "(" + fe + ") and (" + jqsex + ")";
    }
    if (StringUtils.isEmpty(order) == true) {
      return searchPart;
    } else {
      return searchPart + order;
    }
  }

  @SuppressWarnings("rawtypes")
  protected String getReqDump()
  {
    Map params = wikiContext.getRequest().getParameterMap();
    StringBuilder sb = new StringBuilder();
    for (Object o : params.keySet()) {
      String k = (String) o;
      sb.append(k).append("=");
      String[] vals = wikiContext.getRequest().getParameterValues(k);

      for (int i = 0; i < vals.length; ++i) {
        sb.append(vals[i]).append("|");
      }
      sb.append("\n");
    }
    String pd = sb.toString();
    return pd;
  }

  protected QueryResult filter(SearchQuery query)
  {
    query.setFindUnindexed(true);
    queryResult = wikiContext.getWikiWeb().getContentSearcher().search(wikiContext, query);
    return queryResult;
  }

  protected boolean filterBeforeQuery(GWikiElementInfo ei)
  {
    return true;
  }

  protected SearchQuery buildQuery()
  {
    String searchExpr = "";
    try {
      // String reqDump = getReqDump();
      Iterable<GWikiElementInfo> webInfos = wikiContext.getWikiWeb().getElementInfos();
      List<SearchResult> sr = new ArrayList<SearchResult>(wikiContext.getWikiWeb().getElementInfoCount());
      for (GWikiElementInfo wi : webInfos) {
        if (filterBeforeQuery(wi) == true) {
          sr.add(new SearchResult(wi));
        }
      }
      searchExpr = getSearchExpression();
      SearchQuery query = new SearchQuery(StringUtils.defaultString(searchExpr), false, sr);
      query.setMaxCount(100000);
      return query;
    } catch (Exception ex) {
      wikiContext.append(ex.getMessage() + " for " + searchExpr);
      return null;
    }
  }

  public Object onFilter()
  {
    SearchQuery query = buildQuery();
    if (query == null) {
      return null;
    }
    queryResult = filter(query);
    return null;

  }

  public String renderField(String fieldName, GWikiElementInfo elementInfo)
  {
    if (StringUtils.equals(fieldName, "PAGEID") == true) {
      return elementInfo.getId();
    }
    if (StringUtils.equals(fieldName, GWikiPropKeys.TITLE) == true) {
      return elementInfo.getTitle();
    }
    String val = elementInfo.getProps().getStringValue(fieldName);

    if (GWikiPropKeys.CREATEDAT.equals(fieldName) == true || GWikiPropKeys.MODIFIEDAT.equals(fieldName) == true) {
      val = wikiContext.getUserDateString(GWikiProps.parseTimeStamp(val));
    }
    return StringUtils.defaultString(val);
  }

  public String writeTableResult()
  {
    return writeTableResult(queryResult);
  }

  public String writeTableResult(QueryResult qr)
  {
    if (qr == null) {
      return "";
    }
    XmlElement table = Html.table();
    List<String> fieldList = Converter.parseStringTokens(fields, "|", false);
    XmlElement tr = Html.tr(Xml.attrs("class", "gwikiTable"));
    for (String fname : fieldList) {
      tr.add(Html.th(Xml.attrs("class", "gwikith"), Xml.text(fname)));
    }
    table.add(tr);
    for (SearchResult sr : qr.getResults()) {
      tr = Html.tr(Xml.attrs());
      for (String fname : fieldList) {
        String text = renderField(fname, sr.getElementInfo());
        tr.add(Html.td(Xml.attrs("class", "gwikitd"), Xml.code(text)));
      }
      table.add(tr);
    }
    String ret = table.toString();
    return ret;
  }

  public String getFilterExpression()
  {
    return filterExpression;
  }

  public void setFilterExpression(String filterExpression)
  {
    this.filterExpression = filterExpression;
  }

  public String getFields()
  {
    return fields;
  }

  public void setFields(String fields)
  {
    this.fields = fields;
  }

  public String getReturnFormat()
  {
    return returnFormat;
  }

  public void setReturnFormat(String returnFormat)
  {
    this.returnFormat = returnFormat;
  }

  public int getStartRow()
  {
    return startRow;
  }

  public void setStartRow(int startRow)
  {
    this.startRow = startRow;
  }

  public int getMaxRows()
  {
    return maxRows;
  }

  public void setMaxRows(int maxRows)
  {
    this.maxRows = maxRows;
  }

  public int getPage()
  {
    return page;
  }

  public void setPage(int page)
  {
    this.page = page;
  }

  public int getRows()
  {
    return rows;
  }

  public void setRows(int rows)
  {
    this.rows = rows;
  }

}
