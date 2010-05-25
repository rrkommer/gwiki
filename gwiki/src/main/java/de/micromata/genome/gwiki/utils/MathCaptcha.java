////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010 Micromata GmbH
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

import java.io.Serializable;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class MathCaptcha implements Serializable
{

  private static final long serialVersionUID = 5068620576947400403L;

  private int firstVal;

  private int secondVal;

  private int result;

  private String operation = "+";

  public MathCaptcha()
  {
    firstVal = (int) (Math.random() * 10.0);
    secondVal = (int) (Math.random() * 100.0);
    result = firstVal + secondVal;
  }

  public boolean checkResult(int result)
  {
    return this.result == result;
  }

  public int getFirstVal()
  {
    return firstVal;
  }

  public void setFirstVal(int firstVal)
  {
    this.firstVal = firstVal;
  }

  public int getSecondVal()
  {
    return secondVal;
  }

  public void setSecondVal(int secondVal)
  {
    this.secondVal = secondVal;
  }

  public int getResult()
  {
    return result;
  }

  public void setResult(int result)
  {
    this.result = result;
  }

  public String getOperation()
  {
    return operation;
  }

  public void setOperation(String operation)
  {
    this.operation = operation;
  }
}
