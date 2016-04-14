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

package de.micromata.genome.gwiki.tools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import de.micromata.genome.util.runtime.RuntimeIOException;

public class PatchJavaHeader
{
  public static String header = "////////////////////////////////////////////////////////////////////////////\n"
      + "//\n"
      + "// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer\n"
      + "//\n"
      + "// Licensed under the Apache License, Version 2.0 (the \"License\");\n"
      + "// you may not use this file except in compliance with the License.\n"
      + "// You may obtain a copy of the License at\n"
      + "//\n"
      + "// http://www.apache.org/licenses/LICENSE-2.0\n"
      + "//\n"
      + "// Unless required by applicable law or agreed to in writing, software\n"
      + "// distributed under the License is distributed on an \"AS IS\" BASIS,\n"
      + "// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
      + "// See the License for the specific language governing permissions and\n"
      + "// limitations under the License.\n"
      + "//\n"
      + "////////////////////////////////////////////////////////////////////////////\n\n";

  public static byte[] headerBytes = header.getBytes();

  public static int skipLine(byte[] data, int offset)
  {
    for (int i = offset; i < data.length; ++i) {
      if (data[i] == '\n') {
        return i;
      }
    }
    throw new RuntimeException("No endline found");
  }

  public static int skipComments(byte[] data)
  {
    for (int i = 0; i < data.length; ++i) {
      if (data[i] == '/' && data[i + 1] == '/') {
        i = skipLine(data, i);
        continue;
      }
      return i;
    }
    return 0;
  }

  public static void patchFile(File file)
  {
    try {
      byte[] data = FileUtils.readFileToByteArray(file);
      int ofs = skipComments(data);
      byte[] newData = new byte[data.length - ofs + headerBytes.length];
      System.arraycopy(headerBytes, 0, newData, 0, headerBytes.length);
      System.arraycopy(data, ofs, newData, headerBytes.length, data.length - ofs);
      // String nf = new String(newData);
      // System.out.println("\n\n\n" + nf);
      FileUtils.writeByteArrayToFile(file, newData);
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  public static void main(String[] args)
  {
    String baseDir = args[0];
    Collection<File> col = FileUtils.listFiles(new File(baseDir), new String[] { "java"}, true);
    for (File f : col) {
      System.out.println("file: " + f.getAbsolutePath());
      patchFile(f);
    }

  }
}
