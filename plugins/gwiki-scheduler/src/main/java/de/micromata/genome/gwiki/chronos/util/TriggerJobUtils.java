/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   24.01.2008
// Copyright Micromata 24.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.util;

import de.micromata.genome.gwiki.chronos.Trigger;

/**
 * Collection of utils to create Trigger.
 * 
 * @author roger@micromata.de
 * 
 */
public class TriggerJobUtils
{
  public static Trigger createTriggerDefinition(final String definition)
  {
    if (definition.startsWith("+")) {
      return new DelayTrigger(definition);
    } else if (definition.startsWith("!")) {
      return new FixedTrigger(definition);
    } else if (definition.startsWith("p")) {
      return new PeriodicalTrigger(definition);
    } else {
      return new CronTrigger(definition);
    }
  }
}
