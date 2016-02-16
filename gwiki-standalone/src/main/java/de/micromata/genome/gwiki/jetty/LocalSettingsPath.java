package de.micromata.genome.gwiki.jetty;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LocalSettingsPath {

  String key();

  String defaultValue() default "";
}
