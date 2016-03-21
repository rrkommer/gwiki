package de.micromata.genome.gwiki.utils;

import org.apache.commons.lang.ObjectUtils;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * very simple builder for json.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class JsonBuilder
{
  //  public static abstract class JsonObject
  //  {
  //    public abstract void render(StringBuilder sb);
  //
  //    @Override
  //    public String toString()
  //    {
  //      StringBuilder sb = new StringBuilder();
  //      render(sb);
  //      return sb.toString();
  //    }
  //  }
  //
  //  public static class JsonPrimtive extends JsonObject
  //  {
  //    private Object object;
  //
  //    public JsonPrimtive()
  //    {
  //
  //    }
  //
  //    public JsonPrimtive(Object object)
  //    {
  //      this.object = object;
  //    }
  //
  //    @Override
  //    public void render(StringBuilder sb)
  //    {
  //      if (object instanceof Number) {
  //        sb.append(((Number) object).toString());
  //        return;
  //      } else if (object instanceof Boolean) {
  //        sb.append(Boolean.toString((Boolean) object));
  //      } else {
  //        String s = esc(ObjectUtils.toString(object));
  //        sb.append("'").append(s).append("'");
  //      }
  //    }
  //  }
  //
  //  public static class JsonArray extends JsonObject
  //  {
  //    List<JsonObject> elements = new ArrayList<>();
  //
  //    public void add(JsonObject obj)
  //    {
  //      elements.add(obj);
  //    }
  //
  //    @Override
  //    public void render(StringBuilder sb)
  //    {
  //      sb.append("[ ");
  //      boolean first = true;
  //      for (JsonObject el : elements) {
  //        if (first == true) {
  //          first = false;
  //        } else {
  //          sb.append(", ");
  //        }
  //        el.render(sb);
  //      }
  //      sb.append(" ]");
  //    }
  //
  //  }
  //
  //  public static class JsonMap extends JsonObject
  //  {
  //    Map<String, JsonObject> elements = new TreeMap<>();
  //
  //    public JsonMap()
  //    {
  //
  //    }
  //
  //    public void put(String key, JsonObject value)
  //    {
  //      elements.put(key, value);
  //    }
  //
  //    public void put(String key, boolean value)
  //    {
  //      elements.put(key, new JsonPrimtive(value));
  //    }
  //
  //    public void put(String key, String value)
  //    {
  //      elements.put(key, new JsonPrimtive(value));
  //    }
  //
  //    @Override
  //    public void render(StringBuilder sb)
  //    {
  //      sb.append("{ ");
  //      boolean first = true;
  //      for (Map.Entry<String, JsonObject> me : elements.entrySet()) {
  //        if (first == true) {
  //          first = false;
  //        } else {
  //          sb.append(", ");
  //        }
  //        sb.append("'").append(me.getKey()).append("': ");
  //        me.getValue().render(sb);
  //      }
  //      sb.append(" }");
  //    }
  //  }
  public static JsonArray array()
  {
    return new JsonArray();
  }

  public static JsonArray array(JsonValue... elements)
  {
    JsonArray ret = new JsonArray();
    for (JsonValue el : elements) {
      ret.add(el);
    }
    return ret;
  }

  public static JsonArray array(String... elements)
  {
    JsonArray ret = new JsonArray();
    for (String el : elements) {
      ret.add(el);
    }
    return ret;
  }

  public static JsonObject map()
  {
    return new JsonObject();
  }

  public static JsonObject map(String... keyValues)
  {
    JsonObject ret = new JsonObject();
    for (int i = 0; i < keyValues.length - 1; ++i) {
      ret.set(keyValues[i], Json.value(keyValues[i + 1]));
      ++i;
    }
    return ret;
  }

  public static JsonValue toValue(Object obj)
  {
    if (obj == null) {
      return Json.NULL;
    }
    if (obj instanceof JsonValue) {
      return (JsonValue) obj;
    }
    if (obj instanceof String) {
      return Json.value((String) obj);
    }
    if (obj instanceof Boolean) {
      return Json.value((Boolean) obj);
    }
    if (obj instanceof Integer) {
      return Json.value((Integer) obj);
    }
    if (obj instanceof Long) {
      return Json.value((Long) obj);
    }
    if (obj instanceof Float) {
      return Json.value((Float) obj);
    }
    if (obj instanceof Double) {
      return Json.value((Double) obj);
    }
    throw new RuntimeException("Unsupported type to convert to JsonValue: " + obj.getClass().getName());
  }

  public static JsonObject map(Object... keyValues)
  {
    JsonObject ret = new JsonObject();
    for (int i = 0; i < keyValues.length - 1; ++i) {
      String skey = ObjectUtils.toString(keyValues[i]);
      Object val = keyValues[i + 1];
      ret.set(skey, toValue(val));
      ++i;
    }
    return ret;
  }

  //  public static String esc(String t)
  //  {
  //    t = StringUtils.replace(t, "\\", "\\\\");
  //    t = StringUtils.replace(t, "'", "\\'");
  //    t = StringUtils.replace(t, "\"", "\\\"");
  //    t = StringUtils.replace(t, "\n", "\\n");
  //    return t;
  //  }
}
