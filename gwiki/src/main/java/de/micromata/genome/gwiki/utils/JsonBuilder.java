package de.micromata.genome.gwiki.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.ObjectUtils;

/**
 * very simple builder for json.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class JsonBuilder
{
  public static abstract class JsonObject
  {
    public abstract void render(StringBuilder sb);

    @Override
    public String toString()
    {
      StringBuilder sb = new StringBuilder();
      render(sb);
      return sb.toString();
    }
  }

  public static class JsonPrimtive extends JsonObject
  {
    private Object object;

    public JsonPrimtive()
    {

    }

    public JsonPrimtive(Object object)
    {
      this.object = object;
    }

    @Override
    public void render(StringBuilder sb)
    {
      if (object instanceof Number) {
        sb.append(((Number) object).toString());
        return;
      }
      String s = esc(ObjectUtils.toString(object));
      sb.append("'").append(s).append("'");
    }

  }

  public static class JsonArray extends JsonObject
  {
    List<JsonObject> elements = new ArrayList<>();

    public void add(JsonObject obj)
    {
      elements.add(obj);
    }

    @Override
    public void render(StringBuilder sb)
    {
      sb.append("[ ");
      boolean first = true;
      for (JsonObject el : elements) {
        if (first == true) {
          first = false;
        } else {
          sb.append(", ");
        }
        el.render(sb);
      }
      sb.append(" ]");
    }

  }

  public static class JsonMap extends JsonObject
  {
    Map<String, JsonObject> elements = new TreeMap<>();

    public JsonMap()
    {

    }

    public void put(String key, JsonObject value)
    {
      elements.put(key, value);
    }

    @Override
    public void render(StringBuilder sb)
    {
      sb.append("{ ");
      boolean first = true;
      for (Map.Entry<String, JsonObject> me : elements.entrySet()) {
        if (first == true) {
          first = false;
        } else {
          sb.append(", ");
        }
        sb.append("'").append(me.getKey()).append("': ");
        me.getValue().render(sb);
      }
      sb.append(" }");
    }
  }

  public static JsonArray array(JsonObject... elements)
  {
    JsonArray ret = new JsonArray();
    ret.elements.addAll(Arrays.asList(elements));
    return ret;
  }

  public static JsonMap map()
  {
    return new JsonMap();
  }

  public static JsonMap map(String... keyValues)
  {
    JsonMap ret = new JsonMap();
    for (int i = 0; i < keyValues.length - 1; ++i) {
      ret.elements.put(keyValues[i], new JsonPrimtive(keyValues[i + 1]));
      ++i;
    }
    return ret;
  }

  public static JsonMap map(Object... keyValues)
  {
    JsonMap ret = new JsonMap();
    for (int i = 0; i < keyValues.length - 1; ++i) {
      String skey = ObjectUtils.toString(keyValues[i]);
      Object val = keyValues[i + 1];
      JsonObject jval;
      if (val instanceof JsonObject) {
        jval = (JsonObject) val;
      } else {
        jval = new JsonPrimtive(val);
      }
      ret.elements.put(skey, jval);
      ++i;
    }
    return ret;
  }

  public static String esc(String t)
  {
    t = StringUtils.replace(t, "\\", "\\\\");
    t = StringUtils.replace(t, "'", "\\'");
    t = StringUtils.replace(t, "\"", "\\\"");
    return t;
  }
}
