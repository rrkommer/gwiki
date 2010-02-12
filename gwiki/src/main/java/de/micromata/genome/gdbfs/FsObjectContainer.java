/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   04.11.2009
// Copyright Micromata 04.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs;

import java.io.Serializable;

/**
 * Container which holds a file and its data.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FsObjectContainer implements Serializable
{

  private static final long serialVersionUID = -6952916904083188777L;

  private FsObject file;

  private byte[] byteData;

  public FsObjectContainer()
  {

  }

  public FsObjectContainer(FsObject file)
  {
    this.file = file;
  }

  public FsObject getFile()
  {
    return file;
  }

  public void setFile(FsObject file)
  {
    this.file = file;
  }

  public byte[] getByteData()
  {
    return byteData;
  }

  public void setByteData(byte[] byteData)
  {
    this.byteData = byteData;
  }

}
