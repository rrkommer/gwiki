/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   14.12.2009
// Copyright Micromata 14.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a diff view.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class DiffSet
{
  public static enum DiffSetStatus
  {
    Compare, //
    NoLeft, //
    NoRight, //
    Incompatible, //
    Unsupported //
    ;
  }

  private DiffSetStatus status = DiffSetStatus.Compare;

  private List<DiffLine> lines = new ArrayList<DiffLine>();

  public DiffSet()
  {

  }

  public DiffSet(DiffSetStatus st)
  {
    status = st;
  }

  public void addLine(DiffLine df)
  {
    lines.add(df);
  }

  public List<DiffLine> getLines()
  {
    return lines;
  }

  public void setLines(List<DiffLine> lines)
  {
    this.lines = lines;
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    for (DiffLine dl : lines) {
      switch (dl.getDiffType()) {
        case Differ:
          sb.append("< ").append(dl.getLeftIndex()).append(" ").append(dl.getLeft());
          sb.append("> ").append(dl.getRightIndex()).append(" ").append(dl.getRight());
          break;
        case Equal:
          sb.append("< ").append(dl.getLeftIndex()).append(" ").append(dl.getLeft());
          sb.append("> ").append(dl.getRightIndex()).append(" ").append(dl.getRight());
          break;
        case LeftNew:
          sb.append("< ").append(dl.getLeftIndex()).append(" ").append(dl.getLeft());
          break;
        case RightNew:
          sb.append("> ").append(dl.getRightIndex()).append(" ").append(dl.getRight());
          break;
      }
    }
    return sb.toString();

  }

  public DiffSetStatus getStatus()
  {
    return status;
  }

  public void setStatus(DiffSetStatus status)
  {
    this.status = status;
  }

}
