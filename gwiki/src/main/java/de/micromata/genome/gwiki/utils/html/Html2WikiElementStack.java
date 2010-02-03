/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   29.12.2009
// Copyright Micromata 29.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.utils.html;

import org.apache.commons.collections15.ArrayStack;

public class Html2WikiElementStack
{
  private ArrayStack<Html2WikiElement> stack = new ArrayStack<Html2WikiElement>();

  public void push(Html2WikiElement el)
  {
    stack.push(el);
  }

  public Html2WikiElement pop()
  {
    return stack.pop();
  }

  public Html2WikiElement peek(int offset)
  {
    return stack.peek(offset);
  }
}
