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

package de.micromata.genome.gwiki.umgmt;

/**
 * Just takes a password and generates hashed value.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class MkPassHash
{
  public static void main(String[] args)
  {
    if (args.length == 0) {
      System.out.println("Please provide clear text password as argument");
      return;
    }
    System.out.println("\nHash:\n" + GWikiUserAuthorization.encrypt(args[0]));
  }
}
