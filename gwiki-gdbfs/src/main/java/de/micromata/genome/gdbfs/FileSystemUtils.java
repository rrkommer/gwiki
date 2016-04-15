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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.matcher.EveryMatcher;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * Utils for dealing with Files.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FileSystemUtils
{

  /**
   * The Enum CopyFlags.
   */
  public static enum CopyFlags
  {
    /**
     * only write files, which does not exists at target.
     */
    MergeNew(0x0001), //
    /**
     * Overwrite existant files.
     */
    OverwriteFiles(0x0002), //
    /**
     * Remove old files in the directory.
     */
    OverwriteDirectories(0x0004),
    /**
     * The flag.
     */
    //
    ;private int flag;

    /**
     * Instantiates a new copy flags.
     *
     * @param flag the flag
     */
    private CopyFlags(int flag)
    {
      this.flag = flag;
    }

    public int getFlag()
    {
      return flag;
    }

    /**
     * Checks if is sets the.
     *
     * @param flags the flags
     * @return true, if is sets the
     */
    public boolean isSet(int flags)
    {
      return (flag & flags) == flag;
    }

    /**
     * Combine.
     *
     * @param flags the flags
     * @return the int
     */
    public int combine(CopyFlags... flags)
    {
      int ret = 0;
      for (CopyFlags f : flags) {
        ret |= f.getFlag();
      }
      return ret;
    }
  }

  /**
   * Copy.
   *
   * @param source the source
   * @param target the target
   */
  public static void copy(final FsObject source, final FileSystem target)
  {
    copy(source, target, new EveryMatcher<String>());
  }

  /**
   * copy a file to - possible - other filesystem.
   *
   * @param source the source
   * @param target the target
   * @param matcher the matcher
   */
  public static void copy(final FsObject source, final FileSystem target, final Matcher<String> matcher)
  {
    source.getFileSystem().runInTransaction(null, 100000, false, new CallableX<Void, RuntimeException>()
    {

      @Override
      public Void call() throws RuntimeException
      {
        return target.runInTransaction(null, 10000, false, new CallableX<Void, RuntimeException>()
        {

          @Override
          public Void call() throws RuntimeException
          {
            copySync(source, target, matcher);
            return null;
          }
        });

      }
    });
  }

  /**
   * Copy sync.
   *
   * @param source the source
   * @param target the target
   * @param matcher the matcher
   */
  protected static void copySync(final FsObject source, final FileSystem target, final Matcher<String> matcher)
  {
    boolean matches = matcher.match(source.getName());
    if (source.isDirectory() == true) {
      if (matches == true) {
        if (target.existsForWrite(source.getName()) == false && target.mkdirs(source.getName()) == false) {
          throw new RuntimeException("Cannot create directory " + source.getName() + " on " + target);
        }
      }
      FsDirectoryObject dir = (FsDirectoryObject) source;
      List<FsObject> childs = dir.getChilds(null);
      for (FsObject c : childs) {
        copySync(c, target, matcher);
      }
    } else {
      FsFileObject file = (FsFileObject) source;
      if (file.isFile() == true && file.getName().equals("/.fslock") == true) {
        return;
      }
      if (matches == false) {
        return;
      }
      FsDirectoryObject fo = source.getParent();
      if (target.exists(fo.getName()) == false) {
        if (target.mkdirs(fo.getName()) == false) {
          throw new RuntimeException("Cannot create directory " + fo.getName() + " on " + target);
        }
      }

      if (file.isTextFile() == true) {
        String data = file.readString();
        target.writeTextFile(file.getName(), data, false);
      } else {
        byte[] data = file.readData();
        target.writeBinaryFile(file.getName(), data, false);
      }
    }
  }

  /**
   * Copy to zip.
   *
   * @param source the source
   * @param zout the zout
   */
  public static void copyToZip(FsObject source, ZipOutputStream zout)
  {
    String name = source.getName();
    if (source.isDirectory() == true) {
      name += "/";
    }
    ZipEntry ze = new ZipEntry(name);
    try {
      if (source.isFile() == true) {
        zout.putNextEntry(ze);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        source.getFileSystem().readBinaryFile(source.getName(), bout);
        zout.write(bout.toByteArray());
        zout.closeEntry();
      } else if (source.isDirectory() == true) {
        zout.putNextEntry(ze);
        zout.closeEntry();
      }
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  /**
   * Copy files into a zip file.
   *
   * @param source the source
   * @param matcher the matcher
   * @param zipOut will closed this stream after written.
   */
  public static void copyToZip(FsObject source, Matcher<String> matcher, OutputStream zipOut)
  {
    ZipOutputStream zOut = new ZipOutputStream(zipOut);
    if (source.isDirectory() == true) {
      FsDirectoryObject dir = (FsDirectoryObject) source;
      List<FsObject> files = dir.getFileSystem().listFiles(source.getName(), matcher, null, true);
      for (FsObject f : files) {
        copyToZip(f, zOut);
      }
    }
    // TODO look in reporting, if zip files will be closed.
    IOUtils.closeQuietly(zOut);
    IOUtils.closeQuietly(zipOut);
  }

  /**
   * Merge dir names.
   *
   * @param parent the parent
   * @param child the child
   * @return the string
   */
  public static String mergeDirNames(String parent, String child)
  {
    return FileNameUtils.join(parent, child);
  }

  /**
   * Normalize zip name.
   *
   * @param name the name
   * @return the string
   */
  public static String normalizeZipName(String name)
  {
    String s = StringUtils.replace(name, "\\", "/");
    if (s.startsWith("/") == true) {
      return s.substring(1);
    }
    return s;
  }

  /**
   * Copy from zip.
   *
   * @param ze the ze
   * @param zin the zin
   * @param target the target
   */
  public static void copyFromZip(ZipEntry ze, ZipInputStream zin, FsObject target)
  {
    String name = ze.getName();
    name = normalizeZipName(name);
    ze.getTime();
    if (name.endsWith("/") == true) {
      String fqName = mergeDirNames(target.getName(), name);
      fqName = fqName.substring(0, fqName.length() - 1);
      if (target.getFileSystem().existsForWrite(fqName) == true) {
        return;
      }
      target.getFileSystem().mkdirs(fqName);
    } else {
      String fqName = mergeDirNames(target.getName(), name);
      String pd = AbstractFileSystem.getParentDirString(fqName);
      if (target.getFileSystem().exists(pd) == false) {
        target.getFileSystem().mkdirs(pd);
      }
      target.getFileSystem().writeBinaryFile(fqName, zin, true);
    }
  }

  /**
   * Copy from zip.
   *
   * @param zipIn the zip in
   * @param target the target
   */
  public static void copyFromZip(InputStream zipIn, FsObject target)
  {
    ZipInputStream zin = new ZipInputStream(zipIn);
    try {
      while (true) {
        ZipEntry ze = zin.getNextEntry();
        if (ze == null) {
          break;
        }
        copyFromZip(ze, zin, target);
        zin.closeEntry();
      }
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }
}
