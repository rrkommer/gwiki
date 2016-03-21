package de.micromata.genome.gwiki.page.impl.wiki;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroParamType;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MacroInfoParam {
  String name();

  MacroParamType type() default MacroParamType.String;

  String defaultValue() default "";

  boolean required() default false;

  String info() default "";

  boolean restricted() default false;

  /**
   * {macroName:parameter}
   * 
   * @return
   */
  boolean defaultParameter() default false;
}
