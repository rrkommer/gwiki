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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.TransactionStatus;

import de.micromata.genome.dao.db.DatabaseProviderUtils;
import de.micromata.genome.db.spring.DBTable;
import de.micromata.genome.db.spring.SimpleDBTable;
import de.micromata.genome.gdbfs.AbstractFileSystem;
import de.micromata.genome.gdbfs.FileSystemEventType;
import de.micromata.genome.gdbfs.FsDirectoryObject;
import de.micromata.genome.gdbfs.FsException;
import de.micromata.genome.gdbfs.FsFileExistsException;
import de.micromata.genome.gdbfs.FsFileLockException;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gdbfs.db.DbDialect.Flags;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.RuntimeIOException;
import de.micromata.genome.util.types.Converter;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * Database based implementation of file system.
 * 
 * Events are cluster aware.
 * 
 * Currently only tested with oracle 10 but should be easely ported to other db systems.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class DbFileSystemImpl extends AbstractFileSystem
{
  public static final String DEL_PREFIX = "del";

  private final static int COLSIZE = 3990;

  private String fileSystemName = "GNMVFSYS";

  /**
   * Has to be set from outside
   */
  private DbTarget dbTarget;

  private DBTable table = new SimpleDBTable("vw_base_gvfs", "base_gvfs", "sq_base_gvfs");

  private DBTable tableData = new SimpleDBTable("vw_base_gvfs_data", "base_gvfs_data", "sq_base_gvfs_data");

  private long lastEventCollected = System.currentTimeMillis();

  private long checkFileSystemIntervallMs = TimeInMillis.MINUTE;

  private long lastCheckSystemMs = 0;

  private ThreadLocal<Long> updateCounterInTrans = new ThreadLocal<Long>() {

    @Override
    protected Long initialValue()
    {
      return Long.valueOf(0);
    }

  };

  protected void incUpdateCounterInTrans()
  {
    updateCounterInTrans.set(updateCounterInTrans.get() + 1);
  }

  public String toString()
  {
    return table.getTableName() + ":" + fileSystemName;
  }

  private JdbcTemplate jdbc()
  {
    return dbTarget.getJtemplate();
  }

  private String normalizeName(String name)
  {
    if (name == null)
      return null;
    if (name.length() == 0)
      return "/";
    if (name.startsWith("/") == false) {
      name = "/" + name;
    }
    if (name.endsWith("/") == true && name.length() > 1) {
      name = name.substring(0, name.length() - 1);
    }
    return name;
  }

  public boolean exists(String name)
  {
    name = normalizeName(name);

    String sql = "select count(*) from " + table.getTableName() + " where FSNAME = ? and NAME = ?";
    return jdbc().queryForLong(sql, new Object[] { fileSystemName, name}) > 0;
  }

  public boolean rename(String oldName, String newName)
  {
    checkReadOnly();
    oldName = normalizeName(oldName);
    newName = normalizeName(newName);
    Long parentDir = getParentDirPk(newName);
    if (parentDir == null) {
      return false;
    }
    try {
      String sql = "update " + table.getTableName() + " set NAME = ?, PARENT = ? where FSNAME = ? and NAME = ?";
      int updated = jdbc().update(sql, new Object[] { newName, parentDir, fileSystemName, oldName});
      boolean res = updated == 1;
      if (res == true) {
        incUpdateCounterInTrans();
      }
      return res;
    } catch (DataAccessException ex) {
      return false;
    }
  }

  private void checkRootDir()
  {
    if (exists("/") == true)
      return;
    checkReadOnly();
    try {
      String sql = "insert into " + table.getTableName() + " (FSNAME, NAME, TYPE, DATAENC) VALUES(?, ?, ?, ?)";
      jdbc().update(sql, new Object[] { fileSystemName, "/", Character.toString(TYPE_DIR), Character.toString(DATAENCODING_BINARY)});
      incUpdateCounterInTrans();
    } catch (DataAccessException ex) {
      throw new FsException("Error mkdir: / ; " + ex.getMessage(), ex);
    }
  }

  private Long getParentDirPk(String name)
  {
    String parentName = getParentDirString(name);
    if (parentName.length() == 0) {
      checkRootDir();
      parentName = "/";
    }
    String sql = "select " + table.getPkColumnName() + " from " + table.getTableName() + " where FSNAME = ? and NAME = ? and TYPE = ?";
    try {
      return jdbc().queryForLong(sql, new Object[] { fileSystemName, parentName, Character.toString(TYPE_DIR)});
    } catch (IncorrectResultSizeDataAccessException ex) {
      return null;
    }
  }

  private Long getObjectPk(String name)
  {
    name = normalizeName(name);
    return getObjectPkUnnormalized(name);
  }

  private Long getObjectPkUnnormalized(String name)
  {
    String sql = "select " + table.getPkColumnName() + " from " + table.getTableName() + " where FSNAME = ? and NAME = ?";
    try {
      return jdbc().queryForLong(sql, new Object[] { fileSystemName, name});
    } catch (IncorrectResultSizeDataAccessException ex) {
      return null;
    }
  }

  public boolean mkdir(String name)
  {
    checkReadOnly();
    name = normalizeName(name);
    try {
      if (existsForWrite(name) == true) {
        return true;
      }
      Long pk = getParentDirPk(name);
      if (pk == null) {
        if (name.equals("/") == true) {
          return true;
        } else {
          return false;
        }
      } else if (name.equals("/") == true) {
        return true;
      }
      String sql = "insert into " + table.getTableName() + " (FSNAME, NAME, TYPE, DATAENC, PARENT) VALUES(?, ?, ?, ?, ?)";
      int updated = jdbc().update(sql,
          new Object[] { fileSystemName, name, Character.toString(TYPE_DIR), Character.toString(DATAENCODING_BINARY), pk});
      if (updated == 0)
        return false;
      incUpdateCounterInTrans();
    } catch (DataAccessException ex) {
      throw new FsException("Error mkdir file: " + name + "; " + ex.getMessage(), ex);
    }
    return true;
  }

  public boolean mkdirs(String name)
  {
    checkReadOnly();
    name = normalizeName(name);
    String parentName = getParentDirString(name);
    if (parentName.length() == 0)
      return mkdir(name);
    Long pk = getParentDirPk(name);
    if (pk == null) {
      if (mkdirs(parentName) == false)
        return false;
      pk = getParentDirPk(name);
      if (pk == null)
        return false;
    }
    return mkdir(name);
  }

  public FsObject getFileObject(String name)
  {
    return getFileObject(name, false);
  }

  public FsObject getFileObject(String name, boolean withData)
  {
    name = normalizeName(name);
    String sql = "select * from " + table.getTableName() + " where FSNAME = ? and NAME = ?";
    try {
      return (FsObject) jdbc().queryForObject(sql, new Object[] { fileSystemName, name}, new DbObjectRowMapper(this, withData));
    } catch (IncorrectResultSizeDataAccessException ex) {
      return null;
    }
  }

  protected Object decode(String dataEncoding, String data)
  {
    boolean isText = dataEncoding.charAt(0) == DATAENCODING_TEXT;
    if (isText == true) {
      return data;
    }
    return Base64.decodeBase64(data.getBytes());
  }

  public String readTextFile(String file)
  {
    file = normalizeName(file);
    Object odata = readFileContent(file);
    if (odata == null)
      return null;
    if (odata instanceof String) {
      return (String) odata;
    } else if (odata instanceof byte[]) {
      try {
        return new String((byte[]) odata, "UTF-8");
      } catch (UnsupportedEncodingException ex) {
        throw new RuntimeException(ex);
      }
    } else {
      throw new RuntimeException("Unexpected file content type: " + odata.getClass().getName());
    }
  }

  public void readBinaryFile(String file, OutputStream os)
  {
    file = normalizeName(file);
    Object odata = readFileContent(file);
    if (odata == null)
      return;
    byte[] data;
    if (odata instanceof byte[]) {
      data = (byte[]) odata;
    } else {
      try {
        data = ((String) odata).getBytes("UTF-8");
      } catch (UnsupportedEncodingException ex) {
        throw new RuntimeException(ex);
      }
    }
    try {
      IOUtils.copy(new ByteArrayInputStream(data), os);
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  @SuppressWarnings("unchecked")
  protected void readLongValue(Long pk, final StringBuilder sb)
  {
    String sql = "select DATACOL1 from "
        + tableData.getTableName()
        + " where "
        + table.getPkColumnName()
        + " = ?"
        + " order by DATAROW asc";
    List ret = jdbc().query(sql, new Object[] { pk}, new RowMapper() {
      public Object mapRow(ResultSet rs, int rowNum) throws SQLException
      {
        sb.append(rs.getString(1));
        return null;
      }
    });
  }

  public Object readFileContent(String file)
  {
    try {
      file = normalizeName(file);
      FsObject obj = getFileObject(file, true);
      if ((obj instanceof FsDbFileObject) == false) {
        return null;
      }
      StringBuilder sb = new StringBuilder();
      FsDbFileObject fdb = (FsDbFileObject) obj;
      sb.append(fdb.getDataCol0());

      if (fdb.isLongValue() == true) {
        readLongValue(fdb.getPk(), sb);
      }
      try {
        return decode(fdb.getDataEnc(), sb.toString());
      } catch (Exception ex) {
        throw new FsException("Failed to decode file: " + file + "; " + ex.getMessage(), ex);
      }
    } catch (DataAccessException ex) {
      throw new FsException("Error Reading file: " + file + "; " + ex.getMessage(), ex);
    }
  }

  public List<FsObject> readChilds(FsDirectoryObject dirObject)
  {
    throw new RuntimeException("readChilds not supported yet");
  }

  private List<String> splitLongValues(String content)
  {
    List<String> rows = new ArrayList<String>();
    for (int i = 0; content.length() > 0; ++i) {
      String t = Converter.trimUtf8(content, COLSIZE);
      content = StringUtils.substring(content, t.length());
      rows.add(t);
    }
    if (rows.isEmpty() == true) {
      rows.add("");
    }
    return rows;
  }

  protected String getUserName()
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    if (ctx != null && ctx.getWikiWeb() != null) {
      String name = ctx.getWikiWeb().getAuthorization().getCurrentUserName(ctx);
      if (name == null) {
        return "anon";
      }
      return name;
    } else {
      return "anon";
    }
    // return GenomeDaoManager.get().getUserDAO().getCurrentShortUserName();
  }

  private void insertLongData(String fileName, JdbcTemplate jdbc, final Long pk, final List<String> rows)
  {
    try {

      String tp = this.table.getPkColumnName();
      String sql = "insert into "
          + tableData.getTableName()
          + " ("
          + tableData.getPkColumnName()
          + ", "
          + tp
          + ", DATAROW, DATACOL1, MODIFIEDBY, CREATEDBY,UPDATECOUNTER,CREATEDAT,MODIFIEDAT) VALUES( "
          + DatabaseProviderUtils.getInPlaceSequenceSelect(tableData.getPkSequenceName())
          + ", ?, ?, ?, ?,?,0,"
          + DatabaseProviderUtils.getNowTimestamp()
          + ","
          + DatabaseProviderUtils.getNowTimestamp()
          + ")";
      final String user = getUserName();

      jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {

        public int getBatchSize()
        {
          return rows.size() - 1;
        }

        public void setValues(PreparedStatement ps, int row) throws SQLException
        {
          ps.setLong(1, pk);
          ps.setInt(2, row);
          ps.setString(3, rows.get(row + 1));
          ps.setString(4, user);
          ps.setString(5, user);
        }
      });
    } catch (DataAccessException ex) {
      throw new FsException("Error writing file: " + fileName + "; " + ex.getMessage(), ex);
    }
  }

  public long getLastModified(final String fileName)
  {
    final String name = normalizeName(fileName);
    String sql = "select MODIFIEDAT from " + getTable().getTableName() + " where FSNAME = ? and NAME = ?";
    JdbcTemplate j = jdbc();
    try {
      Date d = (Date) j.queryForObject(sql, new Object[] { fileSystemName, name}, Date.class);
      return d.getTime();
    } catch (IncorrectResultSizeDataAccessException ex) {
      return 0;
    } catch (DataAccessException ex) {
      throw new FsException("Error reading modification date: " + name + "; " + ex.getMessage(), ex);
    }
  }

  public long getNextPk(JdbcTemplate jdbc, DBTable dbTable)
  {
    final String sqlSeq = DatabaseProviderUtils.getSequenceSelect(dbTable.getPkSequenceName());
    long pk = jdbc.queryForLong(sqlSeq);
    return pk;
  }

  public void writeBinaryFile(String file, byte[] data, boolean overWrite)
  {
    String mimeType = getMimeType(file);
    checkReadOnly();
    file = normalizeName(file);
    int len = 0;
    if (data != null)
      len = data.length;
    String sdata = new String(Base64.encodeBase64(data));
    writeFileObject(file, Character.toString(DATAENCODING_BINARY), mimeType, len, sdata, overWrite);

  }

  public void writeBinaryFile(String file, InputStream is, boolean overWrite)
  {
    checkReadOnly();
    file = normalizeName(file);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try {
      IOUtils.copy(is, bos);
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
    byte[] data = bos.toByteArray();
    writeBinaryFile(file, data, overWrite);
  }

  public void writeTextFile(String file, String data, boolean overWrite)
  {
    checkReadOnly();
    file = normalizeName(file);
    String mimeType = getMimeType(file);
    String dataEnc = Character.toString(DATAENCODING_TEXT);
    int len = 0;
    if (data != null)
      len = data.length();
    writeFileObject(file, dataEnc, mimeType, len, data, overWrite);
  }

  protected void createParentDirFile(String name)
  {
    if (isAutoCreateDirectories() == false) {
      return;
    }
    String parentName = getParentDirString(name);
    mkdirs(parentName);
  }

  public void writeFileObject(String file, String dataEnc, String mimeType, int length, String data, boolean overWrite)
  {
    checkReadOnly();
    file = normalizeName(file);
    Long pk = getObjectPk(file);
    if (pk != null && overWrite == false) {
      throw new FsFileExistsException("File exists: " + file);
    }

    JdbcTemplate j = jdbc();
    List<String> values = splitLongValues(data);
    if (values.size() > 1) {
      dataEnc += "L";
    } else {
      dataEnc += "S";
    }

    if (pk == null) {
      Long ppk = getParentDirPk(file);

      if (ppk == null) {
        createParentDirFile(file);
        ppk = getParentDirPk(file);
        if (ppk == null) {
          throw new FsFileExistsException("Parent directory not found for: " + file);
        }
      }
      if (this.dbTarget.getDbDialect().supports(Flags.SupportSequencer) == true) {
        pk = getNextPk(j, table);

        String sql = "insert into "
            + table.getTableName()
            + " ("
            + table.getPkColumnName()
            + ", FSNAME, NAME, TYPE, DATAENC, MIMETYPE, PARENT, LENGTH, DATACOL0) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
          jdbc().update(sql,
              new Object[] { pk, fileSystemName, file, Character.toString(TYPE_FILE), dataEnc, mimeType, ppk, length, values.get(0)});
        } catch (DataIntegrityViolationException ex) {
          throw new FsFileExistsException("File already exists: " + file);

        } catch (DataAccessException ex) {
          throw new FsException("Error writing update file: " + file + "; " + ex.getMessage(), ex);
        }
      } else {
        throw new FsException("Database dialect not supported");
      }

    } else {
      String sqldata = "delete from " + tableData.getTableName() + " where " + table.getPkColumnName() + " = ?";
      jdbc().update(sqldata, new Object[] { pk});
      String sql = "update "
          + table.getTableName()
          + " set TYPE = ?, DATAENC = ?, MIMETYPE = ?, LENGTH = ?, DATACOL0 = ? where "
          + table.getPkColumnName()
          + " = ?";
      jdbc().update(sql, new Object[] { Character.toString(TYPE_FILE), dataEnc, mimeType, length, values.get(0), pk});
    }
    if (values.size() > 1) {
      insertLongData(file, j, pk, values);
    }
    incUpdateCounterInTrans();
  }

  private boolean hasChilds(String fileName, long pk)
  {
    try {
      String sql = "select count(*) from " + table.getTableName() + " where FSNAME = ? and PARENT = ?";
      return jdbc().queryForInt(sql, new Object[] { fileSystemName, pk}) > 0;
    } catch (DataAccessException ex) {
      throw new FsException("Error writing update file: " + fileName + "; " + ex.getMessage(), ex);
    }
  }

  public void destoryFile(String name)
  {
    name = normalizeName(name);
    destoryFileUnnormalized(name);
  }

  private void destoryFileUnnormalized(String name)
  {
    Long delNamePk = getObjectPkUnnormalized(name);
    if (delNamePk == null) {
      return;
    }

    String sql = "delete from " + tableData.getTableName() + " where " + table.getPkColumnName() + " = ?";
    int updated = jdbc().update(sql, new Object[] { delNamePk});
    sql = "delete from " + table.getTableName() + " where " + table.getPkColumnName() + " = ?";
    updated = jdbc().update(sql, new Object[] { delNamePk});
  }

  public boolean delete(String name)
  {
    checkReadOnly();
    name = normalizeName(name);
    Long pk = getObjectPk(name);
    if (pk == null) {
      return false;
    }
    if (hasChilds(name, pk) == true) {
      return false;
    }
    try {
      String delName = DEL_PREFIX + name;
      destoryFileUnnormalized(delName);
      String sql = "update " + table.getTableName() + " set NAME = ?, PARENT = null where " + table.getPkColumnName() + " = ?";
      int updated = jdbc().update(sql, new Object[] { delName, pk});

      // String sql = "delete from " + table.getTableName() + " where " + table.getPkColumnName() + " = ?";
      // int updated = j.update(sql, new Object[] { pk});
      boolean res = updated == 1;
      if (res == true) {
        incUpdateCounterInTrans();
      }
      return res;
    } catch (DataAccessException ex) {
      throw new FsException("Error deleting file: " + name + "; " + ex.getMessage(), ex);
    }
  }

  public void erase()
  {
    checkReadOnly();
    String sqldata = "delete from "
        + tableData.getTableName()
        + " where "
        + table.getPkColumnName()
        + " in (select "
        + table.getPkColumnName()
        + " from "
        + table.getTableName()
        + " where FSNAME = ?)";
    try {
      JdbcTemplate j = jdbc();
      int datarows = j.update(sqldata, new Object[] { fileSystemName});

      String sql = "delete from " + table.getTableName() + " where FSNAME = ?";
      int rows = j.update(sql, new Object[] { fileSystemName});
      if (rows > 0) {
        incUpdateCounterInTrans();
      }
    } catch (DataAccessException ex) {
      throw new FsException("Error deleting erasing filesystem; " + ex.getMessage(), ex);
    }
  }

  public List<FsObject> listFiles(final String tname, final Matcher<String> matcher, final Character searchType, final boolean recursive)
  {
    final List<FsObject> ret = new ArrayList<FsObject>();
    final String name = normalizeName(tname);
    Long thisPk = getObjectPk(name);
    if (thisPk == null) {
      return ret;
    }
    try {
      String sql = "select * from " + table.getTableName() + " where FSNAME = ? and NAME like ?";
      String sname;
      if (name == null || name.equals("/") == true) {
        sname = "/%";
      } else {
        sname = name + "/%";
      }
      Object[] args = new Object[] { fileSystemName, sname};
      if (recursive == false) {
        sql += " and PARENT = ?";
        args = new Object[] { fileSystemName, sname, thisPk};
      }
      jdbc().query(sql, args, new DbObjectRowMapper(this, false) {

        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException
        {
          FsObject obj = (FsObject) super.mapRow(rs, rowNum);

          if (searchType != null) {
            if (searchType == 'F' && obj.isFile() == false) {
              return null;
            } else if (searchType == 'D' && obj.isDirectory() == false) {
              return null;
            }
            if (matcher != null) {
              String mn = obj.getName().substring(tname.length());
              if (matcher.match(mn) == false) {
                return null;
              }
            }
          }
          ret.add(obj);
          return null;
        }
      });
    } catch (DataAccessException ex) {
      throw new FsException("Error reading filesystem; " + ex.getMessage(), ex);
    }
    return ret;
  }

  protected long lockFile(String lockFile, long timeout)
  {
    boolean doNotLock = true;
    if (lockFile == null)
      lockFile = "/";
    checkRootDir();
    int timeOutSec = (int) (timeout / 1000);

    String sql = "select " + table.getPkColumnName() + " from " + table.getTableName() + " where FSNAME = ? and NAME = ?";

    if (doNotLock == false) {
      sql += " FOR UPDATE WAIT " + timeOutSec;
    }
    JdbcTemplate j = jdbc();
    try {
      Long pk = j.queryForLong(sql, new Object[] { getFileSystemName(), lockFile});
      return pk;
    } catch (IncorrectResultSizeDataAccessException ex) {
      throw new FsFileLockException("File to be locked does not exits: " + lockFile + "; " + ex.getMessage(), ex);
    } catch (DataAccessException ex) {
      throw new FsFileLockException("Cannot lock file: " + lockFile + "; " + ex.getMessage(), ex);
    }
  }

  public long getModificationCounter()
  {
    try {
      JdbcTemplate j = jdbc();
      String sql = "select UPDATECOUNTER from " + table.getTableName() + " where FSNAME = ? and NAME = ?";
      return j.queryForLong(sql, new Object[] { getFileSystemName(), "/"});
    } catch (IncorrectResultSizeDataAccessException ex) {
      // db is empty
      return 0;
    }
  }

  protected void updateUpdateCounter(long pk)
  {
    String sql = "update " + table.getTableName() + " set " + table.getPkColumnName() + " = ? where " + table.getPkColumnName() + " = ?";
    JdbcTemplate j = jdbc();
    j.update(sql, new Object[] { pk, pk});
  }

  protected ThreadLocal<Long> recursiveCounter = new ThreadLocal<Long>() {
    @Override
    protected Long initialValue()
    {
      return Long.valueOf(0);
    }
  };

  @SuppressWarnings("unchecked")
  public <R> R runInTransaction(final String lockFile, final long timeOut, final boolean noModFs,
      final CallableX<R, RuntimeException> callback)
  {

    updateCounterInTrans.set(Long.valueOf(0));

    TransactionTemplateX tx = dbTarget.getTransactionTemplate("DbFileSystem." + getFileSystemName());
    long rec = recursiveCounter.get();
    try {
      recursiveCounter.set(rec + 1);
      if (rec > 0) {
        return callback.call();
      } else {
        return (R) tx.execute(new TransactionCallbackX<R>() {

          public R doInTransaction(TransactionStatus status)
          {
            long c = lockFile(lockFile, timeOut);
            R r = callback.call();
            if (updateCounterInTrans.get() > 0) {
              if (noModFs == false) {
                updateUpdateCounter(c);
              }
            }
            return r;
          }
        });
      }
    } finally {
      recursiveCounter.set(rec - 1);
    }
  }

  @SuppressWarnings("unchecked")
  protected synchronized void checkNotifications()
  {
    String sql = "select * from " + table.getTableName() + " where FSNAME = ? and MODIFIEDAT > ?";
    List<FsObject> ret = jdbc()
        .query(sql, new Object[] { fileSystemName, new Date(lastEventCollected)}, new DbObjectRowMapper(this, false));
    long lastMod = lastEventCollected;
    for (FsObject obj : ret) {
      long modTime = obj.getLastModified();
      FileSystemEventType fsEvent = FileSystemEventType.Modified;
      if (obj.getCreatedAt() == obj.getModifiedAt() && obj.getUpdateCounter() == 0) {
        fsEvent = FileSystemEventType.Created;
      }
      String name = obj.getName();
      if (name.startsWith(DEL_PREFIX) == true) {
        fsEvent = FileSystemEventType.Deleted;
        name = name.substring(DEL_PREFIX.length());
      }
      if (lastMod < obj.getLastModified()) {
        lastMod = modTime;
      }
      if (name.startsWith("/") == true) {
        name = name.substring(1);
      }
      addEvent(fsEvent, name, modTime);
    }
    lastEventCollected = lastMod;
    lastCheckSystemMs = System.currentTimeMillis();
  }

  @Override
  public void checkEvents(boolean force)
  {
    if (force == true || lastCheckSystemMs + checkFileSystemIntervallMs < System.currentTimeMillis()) {
      checkNotifications();
      super.checkEvents(force);
    }
  }

  public String getFileSystemName()
  {
    return fileSystemName;
  }

  public void setFileSystemName(String name)
  {
    this.fileSystemName = name;
  }

  public DbTarget getDbTarget()
  {
    return dbTarget;
  }

  public void setDbTarget(DbTarget dbTarget)
  {
    this.dbTarget = dbTarget;
  }

  public DBTable getTable()
  {
    return table;
  }

  public void setTable(DBTable table)
  {
    this.table = table;
  }

  public DBTable getTableData()
  {
    return tableData;
  }

  public void setTableData(DBTable tableData)
  {
    this.tableData = tableData;
  }

  public long getCheckFileSystemIntervallMs()
  {
    return checkFileSystemIntervallMs;
  }

  public void setCheckFileSystemIntervallMs(long checkFileSystemIntervallMs)
  {
    this.checkFileSystemIntervallMs = checkFileSystemIntervallMs;
  }

}
