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

package de.micromata.genome.gwiki.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

/**
 * Standalone response output stream for batch processing and unit tests.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class StandaloneServletOutputStream extends ServletOutputStream
{
  private ByteArrayOutputStream out = new ByteArrayOutputStream();

  /** Pass through method calls ByteArrayOutputStream.write(int b). */
  public void write(int b) throws IOException
  {
    out.write(b);
  }

  /** Returns the array of bytes that have been written to the output stream. */
  public byte[] getBytes()
  {
    return out.toByteArray();
  }

  /** Returns, as a character string, the output that was written to the output stream. */
  public String getString()
  {
    return out.toString();
  }
}
