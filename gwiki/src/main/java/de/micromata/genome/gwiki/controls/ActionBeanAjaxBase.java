package de.micromata.genome.gwiki.controls;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.eclipsesource.json.JsonValue;

import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * Actin bean for ajax services.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class ActionBeanAjaxBase extends ActionBeanBase
{
  /**
   * Response wanted in json
   */
  private boolean json;

  protected Object sendResponse(int rc, String message)
  {
    sendResponse(toMap("rc", Integer.toString(rc), "rm", message));
    return noForward();
  }

  protected String encodeAsUrl(Map<String, String> map)
  {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> me : map.entrySet()) {
      if (sb.length() > 0) {
        sb.append("&");
      }
      try {
        sb.append(URLEncoder.encode(me.getKey(), "UTF-8"));
        sb.append("=");
        sb.append(URLEncoder.encode(me.getValue() != null ? me.getValue() : "", "UTF-8"));
      } catch (UnsupportedEncodingException ex) {
        throw new RuntimeException(ex);
      }
    }
    return sb.toString();
  }

  protected String encodeAsJson(Map<String, String> map)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("{ ");
    boolean first = true;
    for (Map.Entry<String, String> me : map.entrySet()) {
      if (first == true) {
        first = false;
      } else {
        sb.append(", ");
      }
      sb.append('\'').append(me.getKey()).append("': '").append(me.getValue()).append("'");
    }
    sb.append("}");
    return sb.toString();
  }

  protected Map<String, String> toMap(String... keyValues)
  {
    Map<String, String> ret = new HashMap<String, String>();
    for (int i = 0; i < keyValues.length; ++i) {
      if (i + 1 >= keyValues.length) {
        return ret;
      }
      ret.put(keyValues[i], keyValues[i + 1]);
      ++i;
    }
    return ret;
  }

  protected Object sendUrlResponse(Map<String, String> resp)
  {
    String sr = encodeAsUrl(resp);
    return sendStringResponse(sr);
  }

  protected Object sendJsonResponse(String... keyValues)
  {
    return sendJsonResponse(toMap(keyValues));
  }

  protected Object sendJsonResponse(Map<String, String> resp)
  {
    String sr = encodeAsJson(resp);
    wikiContext.getResponse().setContentType("application/json");
    return sendStringResponse(sr);
  }

  protected Object sendResponse(Map<String, String> resp)
  {
    String sr;
    if (json == true) {
      sr = encodeAsJson(resp);
      wikiContext.getResponse().setContentType("application/json");
    } else {
      sr = encodeAsUrl(resp);
    }
    return sendStringResponse(sr);
  }

  protected Object sendResponse(JsonValue obj)
  {
    wikiContext.getResponse().setContentType("application/json");
    return sendStringResponse(obj.toString());
  }

  protected Object sendStringResponse(String sr)
  {
    try {
      wikiContext.getResponseOutputStream().write(sr.getBytes("UTF-8"));
      wikiContext.getResponseOutputStream().flush();
      return noForward();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public boolean isJson()
  {
    return json;
  }

  public void setJson(boolean json)
  {
    this.json = json;
  }
}
