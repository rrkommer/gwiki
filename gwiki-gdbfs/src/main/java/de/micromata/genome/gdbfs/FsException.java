//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gdbfs;

/**
 * Exception thrown by FileSystem.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FsException extends RuntimeException

{

  private static final long serialVersionUID = -3256791863540206821L;

  public FsException()
  {
    super();
  }

  public FsException(Throwable cause)
  {
    super(cause);
  }

  public FsException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public FsException(String message)
  {
    super(message);
  }

}
