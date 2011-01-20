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

import java.io.Serializable;

public class GenomeJobArgument implements Serializable
{

  private static final long serialVersionUID = 7443578555262482723L;

  private Object argument;

  public Object getArgument()
  {
    return argument;
  }

  public void setArgument(Object argument)
  {
    this.argument = argument;
  }

}
