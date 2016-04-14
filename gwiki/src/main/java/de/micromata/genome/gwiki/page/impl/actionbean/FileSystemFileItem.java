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

package de.micromata.genome.gwiki.page.impl.actionbean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.output.ByteArrayOutputStream;

import de.micromata.genome.gdbfs.FileSystem;

/**
 * @author roger
 * 
 */
public class FileSystemFileItem implements FileItem
{

  private static final long serialVersionUID = -6403053267567279869L;

  /**
   * Counter used in unique identifier generation.
   */
  private static int counter = 0;

  private FileSystem fileSystem;

  private String fieldName;

  private String fileName;

  private String contentType;

  private boolean formField;

  private String internalName;

  private byte[] data;

  private long size = -1;

  public FileSystemFileItem(FileSystem fileSystem, String fieldName, String contentType, boolean formField, String fileName)
  {
    this.fileSystem = fileSystem;
    this.fieldName = fieldName;
    this.formField = formField;
    this.fileName = fileName;
    this.contentType = contentType;
    internalName = getUniqueId() + ".tmp";
  }

  private static String getUniqueId()
  {
    final int limit = 100000000;
    int current;
    synchronized (DiskFileItem.class) {
      current = counter++;
    }
    String id = Integer.toString(current);

    // If you manage to get more than 100 million of ids, you'll
    // start getting ids longer than 8 characters.
    if (current < limit) {
      id = ("00000000" + id).substring(id.length());
    }
    return id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.fileupload.FileItem#getInputStream()
   */
  @Override
  public InputStream getInputStream() throws IOException
  {
    return new ByteArrayInputStream(get());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.fileupload.FileItem#get()
   */
  @Override
  public byte[] get()
  {
    if (data != null) {
      return data;
    }
    byte[] data = fileSystem.readBinaryFile(internalName);
    return data;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.fileupload.FileItem#getOutputStream()
   */
  @Override
  public OutputStream getOutputStream() throws IOException
  {
    return new ByteArrayOutputStream() {

      @Override
      public void close() throws IOException
      {
        super.close();
        byte[] ldata = this.toByteArray();
        if (ldata != null) {
          size = ldata.length;
          if (fileSystem == null) {
            data = ldata;
          } else {
            fileSystem.writeBinaryFile(internalName, ldata, true);
          }
        }
      }

    };
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.fileupload.FileItem#getSize()
   */
  @Override
  public long getSize()
  {
    return size;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.fileupload.FileItem#write(java.io.File)
   */
  @Override
  public void write(File file) throws Exception
  {
    FileOutputStream fout = null;
    try {
      fout = new FileOutputStream(file);
      fout.write(get());
    } finally {
      if (fout != null) {
        fout.close();
      }
    }
  }

  protected void finalize()
  {
    delete();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.fileupload.FileItem#delete()
   */
  @Override
  public void delete()
  {
    fileSystem.delete(internalName);
    data = null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.fileupload.FileItem#getContentType()
   */
  @Override
  public String getContentType()
  {
    return contentType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.fileupload.FileItem#getName()
   */
  @Override
  public String getName()
  {
    return fileName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.fileupload.FileItem#isInMemory()
   */
  @Override
  public boolean isInMemory()
  {
    return fileSystem == null || data != null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.fileupload.FileItem#getString()
   */
  @Override
  public String getString()
  {
    return new String(get());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.fileupload.FileItem#getString(java.lang.String)
   */
  @Override
  public String getString(String encoding) throws UnsupportedEncodingException
  {
    return new String(get(), encoding);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.fileupload.FileItem#getFieldName()
   */
  @Override
  public String getFieldName()
  {
    return fieldName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.fileupload.FileItem#setFieldName(java.lang.String)
   */
  @Override
  public void setFieldName(String name)
  {
    fieldName = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.fileupload.FileItem#isFormField()
   */
  @Override
  public boolean isFormField()
  {
    return formField;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.fileupload.FileItem#setFormField(boolean)
   */
  @Override
  public void setFormField(boolean state)
  {
    formField = state;

  }

  public byte[] getData()
  {
    return data;
  }

  public void setData(byte[] data)
  {
    this.data = data;
  }

}
