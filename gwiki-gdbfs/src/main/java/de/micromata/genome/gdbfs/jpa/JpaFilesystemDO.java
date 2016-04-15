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

package de.micromata.genome.gdbfs.jpa;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.micromata.genome.jpa.StdRecordDO;

/**
 * The Class JpaFilesystemDO.
 */
@Entity
@Table(name = "TB_GWIKI_JPAFSYS", indexes = {
    @Index(name = "IX_GWIKI_JPAFSYS_NAME", columnList = "fsName, name", unique = true),
    @Index(name = "IX_GWIKI_JPAFSYS_PARENT", columnList = "parent")
})
public class JpaFilesystemDO extends StdRecordDO<Long>
{

  /**
   * The fs name.
   */
  private String fsName;

  /**
   * The name.
   */
  private String name;

  /**
   * The parent.
   */
  private Long parent;

  /**
   * The file type.
   */
  private char fileType;

  /**
   * The mime type.
   */
  private String mimeType;

  /**
   * The data.
   */
  private byte[] data;

  /**
   * The length.
   */
  private int length;

  /**
   * The attributes.
   */
  protected String attributes;

  /**
   * Instantiates a new jpa filesystem do.
   */
  public JpaFilesystemDO()
  {
    super();

  }

  /**
   * Instantiates a new jpa filesystem do.
   *
   * @param pk the pk
   * @param name the name
   * @param fileType the file type
   * @param mimeType the mime type
   * @param length the length
   * @param attributes the attributes
   * @param modifiedAt the modified at
   * @param modifiedBy the modified by
   * @param createdAt the created at
   * @param createdBy the created by
   * @param updateCounter the update counter
   */
  public JpaFilesystemDO(Long pk, String name, char fileType, String mimeType, int length, String attributes,
      Date modifiedAt, String modifiedBy, Date createdAt, String createdBy, Integer updateCounter)
  {
    this.pk = pk;
    this.name = name;
    this.fileType = fileType;
    this.mimeType = mimeType;
    this.length = length;
    this.attributes = attributes;
    this.modifiedAt = modifiedAt;
    this.setModifiedBy(modifiedBy);
    this.setCreatedAt(createdAt);
    this.setCreatedBy(createdBy);
    this.setUpdateCounter(updateCounter);
  }

  @Column(name = "FILETYPE")
  public char getFileType()
  {
    return fileType;
  }

  public void setFileType(char fileTye)
  {
    this.fileType = fileTye;
  }

  @Id
  @GeneratedValue
  @Override
  public Long getPk()
  {
    return pk;
  }

  @Transient
  public boolean isDirectory()
  {
    return fileType == 'D';
  }

  @Column(name = "FSNAME", length = 32, nullable = false)
  public String getFsName()
  {
    return fsName;
  }

  public void setFsName(String fsName)
  {
    this.fsName = fsName;
  }

  @Column(name = "name", length = 1024, nullable = false)
  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  @Column(name = "PARENT")
  public Long getParent()
  {
    return parent;
  }

  public void setParent(Long parent)
  {
    this.parent = parent;
  }

  @Lob
  @Column(name = "DATA")
  public byte[] getData()
  {
    return data;
  }

  public void setData(byte[] data)
  {
    this.data = data;
  }

  @Column(name = "MIMETYPE", length = 50)
  public String getMimeType()
  {
    return mimeType;
  }

  public void setMimeType(String mimeType)
  {
    this.mimeType = mimeType;
  }

  @Column(name = "length")
  public int getLength()
  {
    return length;
  }

  public void setLength(int length)
  {
    this.length = length;
  }

  @Column(name = "attributes", length = 1024)
  public String getAttributes()
  {
    return attributes;
  }

  public void setAttributes(String attributes)
  {
    this.attributes = attributes;
  }
}
