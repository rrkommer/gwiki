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

/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   20.01.2008
// Copyright Micromata 20.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.manager;

import java.util.HashMap;
import java.util.Map;

import de.micromata.genome.gwiki.chronos.JobAbortException;
import de.micromata.genome.gwiki.chronos.JobRetryException;
import de.micromata.genome.gwiki.chronos.spi.AbstractFutureJob;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.GenomeLogCategory;
import de.micromata.genome.logging.LogExceptionAttribute;
import de.micromata.genome.logging.LoggedRuntimeException;
import de.micromata.genome.util.text.PipeValueList;

/**
 * Argument is a Map<String, String> map
 * 
 * @author roger@micromata.de
 * 
 */
public abstract class AbstractCommandLineJob extends AbstractFutureJob
{
  transient protected String stringArg;

  transient protected Map<String, String> args;

  transient protected String adminUserName;

  /**
   * Method to be implemented
   * 
   * @param args Parsed arguments
   * @return
   * @throws Exception
   */
  public abstract Object call(Map<String, String> args) throws Exception;

  @Override
  public Object call(Object argument) throws Exception
  {
    // StopWatch sw = AbstractGenomeJob.prepareJob(this);
    long waitTime = getWaitTime();
    try {
      if (argument == null) {
        args = new HashMap<String, String>();
      } else {
        args = getStandardArgs(argument);
        parseStandardValues(args);
      }
      return call(args);

    } catch (JobAbortException ex) {
      throw ex;
    } catch (JobRetryException ex) {
      throw ex;
    } catch (LoggedRuntimeException ex) {
      throw new JobRetryException();
    } catch (Throwable ex) {
      this.getTriggerJobDO();
      /**
       * @logging
       * @reason Ein Job ist abgebrochen
       * @action Abhaengig von der Exception
       */
      GLog.error(GenomeLogCategory.Scheduler,
          "AdminJob failed. JobName: " + getClass().getSimpleName() + ": " + ex.getMessage(),
          new LogExceptionAttribute(ex));
      throw new JobRetryException();
    } finally {
      // AbstractGenomeJob.finishJob(this, sw, waitTime);

    }

  }

  protected void parseStandardValues(Map<String, String> args)
  {

  }

  public static Map<String, String> getStandardArgs(Object obj)
  {
    if ((obj instanceof String) == false) {
      throw new RuntimeException("Job expects standard pipe seperated args");
    }
    String args = (String) obj;
    Map<String, String> margs = PipeValueList.decode(args);
    return margs;
  }

  // public static Date parseDateTime(String s)
  // {
  // if (s == null)
  // return null;
  // return Converter.parseIsoDateToDate(s);
  // }
  //
  // public static Integer parseInt(String s)
  // {
  // if (s == null)
  // return null;
  // return Integer.parseInt(s);
  // }
  //
  // public static BigDecimal parseBigDecimal(String s)
  // {
  // if (s == null || s.length() == 0)
  // return null;
  // Pair<Boolean, BigDecimal> r = Converter.convertBigDecimal(s);
  // if (r.getFirst() == false)
  // throw new RuntimeException("Cannot parse Bigdecimal: " + s);
  // return r.getSecond();
  // }
  //
  // public static Date parseDate(Map<String, String> args, String name, boolean required)
  // {
  // try {
  // Date start = parseDateTime(args.get(name));
  // if (start == null && required)
  // throw new RuntimeException(name + " muss angegeben werden");
  // return start;
  // } catch (Exception ex) {
  // throw new RuntimeException(name + " kann nicht geparst werden", ex);
  // }
  // }
  //
  // public static Integer parseInteger(Map<String, String> args, String name, boolean required)
  // {
  // try {
  // Integer v = parseInt(args.get(name));
  // if (v == null && required)
  // throw new RuntimeException(name + " muss angegeben werden");
  // return v;
  // } catch (Exception ex) {
  // throw new RuntimeException(name + " kann nicht geparst werden", ex);
  // }
  // }
  //
  // public static boolean parseBoolean(Map<String, String> args, String name, boolean required)
  // {
  // String v = args.get(name);
  //
  // if (required == true && v == null) {
  // throw new RuntimeException(name + " muss angegeben werden");
  // }
  //
  // if (v == null) {
  // return false;
  // }
  //
  // return v.equals("true");
  // }
  //
  // public static BigDecimal parseBigDecimal(Map<String, String> args, String name, boolean required)
  // {
  // try {
  // BigDecimal v = parseBigDecimal(args.get(name));
  // if (v == null && required)
  // throw new RuntimeException(name + " muss angegeben werden");
  // return v;
  // } catch (Exception ex) {
  // throw new RuntimeException(name + " kann nicht geparst werden", ex);
  // }
  // }
  //
  // public static String parseNotEmptyString(Map<String, String> args, String name)
  // {
  // String v = args.get(name);
  // if (StringUtils.isBlank(v) == true)
  // throw new RuntimeException("Fuer " + name + " muss ein Wert angegeben werden");
  // return v;
  // }
}
