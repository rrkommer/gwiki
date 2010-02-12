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

////////////////////////////////////////////////////////////////////////////


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

////////////////////////////////////////////////////////////////////////////


package de.micromata.genome.test.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.mock.MockHttpServletRequest;
import net.sourceforge.stripes.mock.MockHttpSession;

/**
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SimHttpServletRequest extends MockHttpServletRequest
{
  private ServletInputStream servletIs;

  private BufferedReader reader;

  public SimHttpServletRequest()
  {
    super("/", "/x");
  }

  @Override
  public HttpSession getSession()
  {
    return getSession(true);
  }

  @Override
  public HttpSession getSession(boolean createNew)
  {
    HttpSession session = super.getSession();
    if ((session == null) && (createNew == true)) {
      session = new MockHttpSession(null);
      setSession(session);
    }
    return session;
  }

  public void setBody(final Reader reader)
  {
    this.reader = new BufferedReader(reader);
    setInputStream(new ReaderInputStream(reader));
  }

  public void setBody(final InputStream is)
  {
    this.reader = new BufferedReader(new InputStreamReader(is));
    setInputStream(new ReaderInputStream(reader));
  }

  protected void setInputStream(final InputStream servletIs)
  {
    this.servletIs = new ServletInputStream() {

      public int available() throws IOException
      {
        return servletIs.available();
      }

      public void close() throws IOException
      {
        servletIs.close();
      }

      public boolean equals(Object obj)
      {
        return servletIs.equals(obj);
      }

      public int hashCode()
      {
        return servletIs.hashCode();
      }

      public void mark(int readlimit)
      {
        servletIs.mark(readlimit);
      }

      public boolean markSupported()
      {
        return servletIs.markSupported();
      }

      public int read(byte[] b, int off, int len) throws IOException
      {
        return servletIs.read(b, off, len);
      }

      public int read(byte[] b) throws IOException
      {
        return servletIs.read(b);
      }

      public void reset() throws IOException
      {
        servletIs.reset();
      }

      public long skip(long n) throws IOException
      {
        return servletIs.skip(n);
      }

      public String toString()
      {
        return servletIs.toString();
      }

      @Override
      public int read() throws IOException
      {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public ServletInputStream getInputStream() throws IOException
  {
    return servletIs;
  }

  @Override
  public BufferedReader getReader() throws IOException
  {
    return reader;
  }

}
