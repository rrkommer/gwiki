/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   14.11.2009
// Copyright Micromata 14.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.controls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.util.types.Converter;

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
      if (sb.length() != 0) {
        sb.append(" and ");
      }
      sb.append("prop:" + f + " ~ " + sval);
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
    QueryResult qr = wikiContext.getWikiWeb().getContentSearcher().search(wikiContext, query);
    return qr;
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
      Collection<GWikiElementInfo> webInfos = wikiContext.getWikiWeb().getPageInfos().values();
      List<SearchResult> sr = new ArrayList<SearchResult>(webInfos.size());
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
      return noForward();
    }
    QueryResult qr = filter(query);
    writeXmlResult(qr);
    return noForward();

  }

  public String renderField(String fieldName, GWikiElementInfo elementInfo)
  {
    String val = elementInfo.getProps().getStringValue(fieldName);
    if (GWikiPropKeys.CREATEDAT.equals(fieldName) == true || GWikiPropKeys.MODIFIEDAT.equals(fieldName) == true) {
      val = wikiContext.getUserDateString(GWikiProps.parseTimeStamp(val));
    }
    return val;
  }

  public void writeXmlErrorResponse(String message)
  {
    GWikiContext sb = wikiContext;
    sb.append(message);
    sb.flush();
  }

  public void writeXmlResult(QueryResult qr)
  {
    List<String> fl = Converter.parseStringTokens(fields, "|", false);
    wikiContext.getResponse().setContentType("text/xml;charset=utf-8");
    GWikiContext sb = wikiContext;
    int total = qr.getTotalFoundItems();
    sb.append("<?xml version='1.0' encoding='utf-8'?>");
    sb.append("<rows><total>" + ((total / rows) + (total % rows != 0 ? 1 : 0)) + "</total>");
    sb.append("<records>" + rows + "</records>");
    sb.append("<page>" + page + "</page>");
    int id = 0;
    int startRow = (page - 1) * rows;
    int endRow = (page) * rows;
    for (SearchResult sr : qr.getResults()) {
      if (id < startRow) {
        ++id;
        continue;
      }
      if (id >= endRow) {
        break;
      }
      sb.append("<row id='" + id + "'>");
      sb.append("<cell>" + id + "</cell>");

      for (String f : fl) {
        sb.append("<cell>" + StringEscapeUtils.escapeXml(renderField(f, sr.getElementInfo())) + "</cell>");
      }

      sb.append("</row>");
      ++id;
    }
    sb.append("</rows>");
    sb.flush();
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
