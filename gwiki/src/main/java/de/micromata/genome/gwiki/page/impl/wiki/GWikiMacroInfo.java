package de.micromata.genome.gwiki.page.impl.wiki;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.util.types.Pair;

/**
 * Information about a macro.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public interface GWikiMacroInfo
{
  public static enum MacroParamType
  {
    String, PageId,
  }

  public static class MacroParamInfo
  {
    private MacroParamType type;
    private String name;
    private boolean required;
    private String defaultValue;
    private String info;

    public MacroParamInfo()
    {

    }

    public MacroParamInfo(MacroInfoParam anot)
    {
      this.type = anot.type();
      this.name = anot.name();
      this.required = anot.required();
      // TODO restricted
      this.defaultValue = anot.defaultValue();
      this.info = anot.info();
    }

    public MacroParamType getType()
    {
      return type;
    }

    public void setType(MacroParamType type)
    {
      this.type = type;
    }

    public String getName()
    {
      return name;
    }

    public void setName(String name)
    {
      this.name = name;
    }

    public boolean isRequired()
    {
      return required;
    }

    public void setRequired(boolean required)
    {
      this.required = required;
    }

    public String getDefaultValue()
    {
      return defaultValue;
    }

    public void setDefaultValue(String defaultValue)
    {
      this.defaultValue = defaultValue;
    }

    public String getInfo()
    {
      return info;
    }

    public void setInfo(String info)
    {
      this.info = info;
    }

  }

  public static class MacroInfoBean extends GWikiMacroInfoBase
  {
    boolean hasBody;
    boolean evalBody;
    boolean rteMacro;
    String info;
    List<MacroParamInfo> paramsInfos = new ArrayList<>();

    @Override
    public boolean hasBody()
    {
      return hasBody;
    }

    @Override
    public boolean evalBody()
    {
      return evalBody;
    }

    @Override
    public boolean isRteMacro()
    {
      return rteMacro;
    }

    @Override
    public String getInfo()
    {
      return info;
    }

    @Override
    public List<MacroParamInfo> getParamInfos()
    {
      return paramsInfos;
    }

    public void setHasBody(boolean hasBody)
    {
      this.hasBody = hasBody;
    }

    public void setEvalBody(boolean evalBody)
    {
      this.evalBody = evalBody;
    }

    public void setRteMacro(boolean rteMacro)
    {
      this.rteMacro = rteMacro;
    }

    public void setInfo(String info)
    {
      this.info = info;
    }

  }

  boolean hasBody();

  boolean evalBody();

  /**
   * Can be transformed for rich text edit and back.
   * 
   * @return
   */
  boolean isRteMacro();

  /**
   * Information about the macro.
   *
   * @return the info
   */
  String getInfo();

  List<MacroParamInfo> getParamInfos();

  Pair<String, String> getRteTemplate(String macroHead);
}
