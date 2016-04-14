//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

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

  String[] enumValues() default {};
}
