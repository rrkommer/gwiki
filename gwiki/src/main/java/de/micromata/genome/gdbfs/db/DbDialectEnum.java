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

package de.micromata.genome.gdbfs.db;

public enum DbDialectEnum implements DbDialect
{
  /**
   * Probably also work with Oracle 9 - 11
   */
  Oracle10(Flags.combine(Flags.SupportSequencer)), //
  ;
  private int flags;

  private DbDialectEnum(int flags)
  {
    this.flags = flags;

  }

  public boolean supports(Flags flag)
  {
    return (this.flags & flag.getFlags()) == flag.getFlags();
  }
}
