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

package de.micromata.genome.gdbfs;

import java.io.IOException;

/**
 * Exception thrown by FileSystem.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FsIOException extends FsException
{

  private static final long serialVersionUID = -3256791863540206821L;

  public FsIOException()
  {
    super();
  }

  public FsIOException(IOException cause)
  {
    super(cause);
  }

  public FsIOException(String message, IOException cause)
  {
    super(message, cause);
  }

  public FsIOException(String message)
  {
    super(message);
  }

}
