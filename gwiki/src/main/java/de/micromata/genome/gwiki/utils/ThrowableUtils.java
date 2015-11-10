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

package de.micromata.genome.gwiki.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Utils for handling throwables.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class ThrowableUtils
{
  public static String getExceptionStacktraceForHtml(Throwable ex)
  {
    if (ex == null) {
      return StringEscapeUtils.escapeHtml("<no exception set>");
    }
    StringWriter sout = new StringWriter();
    PrintWriter pout = new PrintWriter(sout);
    ex.printStackTrace(pout);
    return StringEscapeUtils.escapeHtml(sout.getBuffer().toString());
  }
}
