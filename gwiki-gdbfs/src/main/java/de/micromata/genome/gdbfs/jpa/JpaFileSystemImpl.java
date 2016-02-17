package de.micromata.genome.gdbfs.jpa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import de.micromata.genome.gdbfs.AbstractFileSystem;
import de.micromata.genome.gdbfs.FileSystemEventType;
import de.micromata.genome.gdbfs.FsDirectoryObject;
import de.micromata.genome.gdbfs.FsFileExistsException;
import de.micromata.genome.gdbfs.FsFileObject;
import de.micromata.genome.gdbfs.FsInvalidNameException;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.jpa.Clauses;
import de.micromata.genome.jpa.Clauses.LogicClause;
import de.micromata.genome.jpa.CriteriaUpdate;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.RuntimeIOException;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class JpaFileSystemImpl extends AbstractFileSystem
{
  private static final char FILE = 'F';

  private static final char DIRECTORY = 'D';

  private GwikiEmgrFactory emfac = GwikiEmgrFactory.get();

  private String fileSystemName = "JPAFSYS";
  private static final String entName = JpaFilesystemDO.class.getName();
  private long lastEventCollected = System.currentTimeMillis();

  private long checkFileSystemIntervallMs = TimeInMillis.MINUTE;

  private long lastCheckSystemMs = 0;
  private List<JpaFilesystemDO> deletedFiles = new ArrayList<>();

  public JpaFileSystemImpl()
  {

  }

  public JpaFileSystemImpl(String fileSystemName)
  {
    this.fileSystemName = fileSystemName;
  }

  @Override
  public String getFileSystemName()
  {

    return fileSystemName;
  }

  private String normalizeName(String name)
  {
    if (name == null) {
      return null;
    }
    if (name.length() == 0) {
      return "/";
    }
    if (name.startsWith("/") == false) {
      name = "/" + name;
    }
    if (name.endsWith("/") == true && name.length() > 1) {
      name = name.substring(0, name.length() - 1);
    }
    return name;
  }

  @Override
  public boolean mkdirs(String name)
  {
    return mkdirsInternal(name) != null;
  }

  private Long mkdirsInternal(String name)
  {
    checkReadOnly();
    name = normalizeName(name);
    String parentName = getParentDirString(name);
    if (parentName.length() == 0) {
      return mkdirInternal(name);
    }
    Long pk = findPk(parentName);
    if (pk == null) {
      pk = mkdirsInternal(parentName);
      if (pk == null) {
        return null;
      }
    }
    return mkdir(name, pk);
  }

  private Long findPk(String normName)
  {
    if (StringUtils.isEmpty(normName) == true) {
      normName = "/";
    }
    String fn = normName;
    List<Long> resl = emfac.notx()
        .go((emgr) -> emgr.selectAttached(Long.class,
            "select e.pk from " + entName + " e where e.fsName = :fsName and e.name = :name",
            "fsName", getFileSystemName(), "name", fn));
    if (resl.isEmpty() == true) {
      return null;
    }
    return resl.get(0);
  }

  public static String getParentDirString(String name)
  {
    int lidx = name.lastIndexOf('/');
    if (lidx == -1) {
      return "";
    }
    return name.substring(0, lidx);
  }

  private void checkRootDir()
  {
    if (exists("/") == true) {
      return;
    }
    checkReadOnly();
    emfac.tx().go((emgr) -> {
      JpaFilesystemDO f = new JpaFilesystemDO();
      f.setName("/");
      f.setFileType(DIRECTORY);
      f.setFsName(getFileSystemName());
      f.setParent(null);
      emgr.insertDetached(f);
      return null;
    });
  }

  @Override
  public boolean mkdir(String name)
  {
    return mkdirInternal(name) != null;
  }

  public Long mkdirInternal(String name)
  {
    checkReadOnly();
    checkRootDir();
    String normname = normalizeName(name);
    Long pk = findPk(normname);
    if (pk != null) {
      return pk;
    }
    String parentName = getParentDirString(normname);
    Long ppk = findPk(parentName);
    if (ppk == null) {
      return null;
    }
    return emfac.tx().go((emgr) -> {
      JpaFilesystemDO f = new JpaFilesystemDO();
      f.setName(normname);
      f.setFileType('D');
      f.setFsName(getFileSystemName());
      f.setParent(ppk);
      emgr.insertDetached(f);
      return f.getPk();
    });
  }

  private Long mkdir(String name, Long parentPk)
  {
    JpaFilesystemDO obj = findDbObjectShort(name);
    if (obj != null) {
      if (obj.isDirectory() == false) {
        throw new FsFileExistsException("Cannot create directory because file exists with same name: " + name);
      }
      return obj.getPk();
    }
    return emfac.tx().go((emgr) -> {
      JpaFilesystemDO dir = new JpaFilesystemDO();
      dir.setName(name);
      dir.setParent(parentPk);
      dir.setFsName(getFileSystemName());
      dir.setFileType('D');
      emgr.insertDetached(dir);
      return dir.getPk();
    });
  }

  @Override
  public boolean exists(String name)
  {
    String normname = normalizeName(name);
    return findPk(normname) != null;
  }

  // TODO RK what if rename with directory?
  @Override
  public boolean rename(String oldName, String newName)
  {

    return emfac.tx().go((emgr) -> {
      String normOldName = normalizeName(oldName);
      String normNewName = normalizeName(newName);

      String nparent = getParentDirString(normNewName);

      Long nparentPk = mkdirsInternal(nparent);
      if (nparentPk == null) {
        throw new FsFileExistsException("Cannot parent dir: " + nparent);
      }

      CriteriaUpdate<JpaFilesystemDO> cu = CriteriaUpdate.createUpdate(JpaFilesystemDO.class);
      cu.set("name", normNewName)
          .set("parent", nparentPk)
          .addWhere(Clauses.and(
              Clauses.equal("fsName", getFileSystemName()),
              Clauses.equal("name", normOldName)));
      int ret = emgr.update(cu);
      return ret > 0;
    });

  }

  @Override
  public synchronized boolean delete(String name)
  {
    checkReadOnly();
    name = normalizeName(name);
    Long pk = findPk(name);
    if (pk == null) {
      return false;
    }
    if (hasChilds(pk) == true) {
      return false;
    }
    JpaFilesystemDO delent = emfac.tx().go((emgr) -> {
      JpaFilesystemDO ent = emgr.selectByPkAttached(JpaFilesystemDO.class, pk);

      emgr.deleteAttached(ent);
      return ent;
    });
    deletedFiles.add(delent);
    return true;
  }

  private boolean hasChilds(String name)
  {
    String normname = normalizeName(name);
    return emfac.notx().go((emgr) -> {
      Long pk = findPk(normname);
      if (pk == null) {
        return false;
      }
      return hasChilds(pk);
    });
  }

  private boolean hasChilds(Long pk)
  {
    return emfac.notx()
        .go((emgr) -> {
          return emgr.selectSingleAttached(Long.class,
              "select count(*) from " + entName + " e where e.parent = :parent", "parent", pk) > 0;
        });

  }

  @Override
  public FsObject getFileObject(String name)
  {
    JpaFilesystemDO dbo = findDbObjectShort(name);
    if (dbo == null) {
      return null;
    }
    return convert(dbo);
  }

  private String getNewShortHql()
  {
    return "select new " + entName
        + "(e.pk, e.name, e.fileType, e.mimeType, e.length, e.attributes, e.modifiedAt, e.modifiedBy, e.createdAt, e.createdBy, e.updateCounter) "
        +
        " from " + entName + " e ";
  }

  private JpaFilesystemDO findDbObjectShort(String name)
  {
    String norname = normalizeName(name);
    List<JpaFilesystemDO> dbo = emfac.notx().go((emgr) -> {
      return emgr.selectAttached(JpaFilesystemDO.class,
          getNewShortHql() + " where e.fsName = :fsName and e.name = :name",
          "fsName", getFileSystemName(), "name", norname);
    });
    if (dbo.isEmpty() == true) {
      return null;
    }
    return dbo.get(0);
  }

  private JpaFilesystemDO findDbObjectFull(String name)
  {
    String norname = normalizeName(name);
    List<JpaFilesystemDO> dbo = emfac.notx().go((emgr) -> {
      return emgr.selectAttached(JpaFilesystemDO.class,
          "select e from " + entName + " e where e.fsName = :fsName and e.name = :name",
          "fsName", getFileSystemName(), "name", norname);
    });
    if (dbo.isEmpty() == true) {
      return null;
    }
    return dbo.get(0);
  }

  private FsObject convert(JpaFilesystemDO fsdo)
  {
    FsObject ret;
    if (fsdo.isDirectory() == false) {
      ret = new FsFileObject(this, fsdo.getName(), fsdo.getMimeType(), fsdo.getModifiedAt().getTime());
    } else {
      ret = new FsDirectoryObject(this, fsdo.getName(), fsdo.getModifiedAt().getTime());
    }
    ret.setCreatedAt(fsdo.getCreatedAt());
    ret.setCreatedBy(fsdo.getCreatedBy());
    ret.setModifiedBy(fsdo.getModifiedBy());
    ret.setUpdateCounter(fsdo.getUpdateCounter());
    ret.setLength(fsdo.getLength());
    ret.setPk(fsdo.getPk());
    return ret;
  }

  @Override
  public long getLastModified(String name)
  {
    String normName = normalizeName(name);
    return emfac.notx().go((emgr) -> {
      List<Date> datel = emgr.selectAttached(Date.class,
          "select e.modifiedAt from " + entName + " e where e.fsName = :fsName and e.name = :name", "fsName",
          getFileSystemName(), "name", normName);
      if (datel.isEmpty() == true) {
        return 0L;
      }
      return datel.get(0).getTime();
    });
  }

  private Long checkCreateParentDirs(String normName)
  {
    String parentDir = getParentDirString(normName);
    Long ret = mkdirsInternal(parentDir);

    if (ret == null) {
      throw new FsFileExistsException("Cannot create parent directory: " + normName);
    }
    return ret;
  }

  @Override
  public void writeBinaryFile(String file, InputStream is, boolean overWrite)
  {
    checkReadOnly();
    checkRootDir();
    String normName = normalizeName(file);
    Long parentPk = checkCreateParentDirs(normName);

    JpaFilesystemDO fob = findDbObjectFull(normName);
    if (overWrite == false && fob != null) {
      throw new FsFileExistsException("File exists: " + normName);
    }
    if (fob != null && fob.isDirectory() == true) {
      throw new FsFileExistsException("Cannot write data into directory: " + normName);
    }
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try {
      IOUtils.copy(is, bos);
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
    byte[] data = bos.toByteArray();
    String mimeType = getMimeType(file);
    if (fob == null) {
      fob = new JpaFilesystemDO();
      fob.setName(normName);
      fob.setFileType(FILE);
    }
    fob.setParent(parentPk);
    fob.setFsName(getFileSystemName());
    fob.setLength(data.length);
    fob.setData(data);
    fob.setMimeType(mimeType);
    JpaFilesystemDO fobn = fob;
    emfac.tx().go((emgr) -> {
      if (fobn.getPk() != null) {
        emgr.merge(fobn);
      } else {
        emgr.insert(fobn);
      }
      return null;
    });
  }

  @Override
  public void readBinaryFile(String file, OutputStream os)
  {
    String normName = normalizeName(file);
    JpaFilesystemDO fob = findDbObjectFull(normName);
    if (fob == null) {
      throw new FsInvalidNameException("Cannot read file. File is not content of file system: " + normName);
    }
    if (fob.isDirectory() == true) {
      throw new FsInvalidNameException("Cannot read file. File is directory: " + normName);
    }

    try {
      IOUtils.copy(new ByteArrayInputStream(fob.getData()), os);
    } catch (IOException e) {
      throw new RuntimeIOException("Cannot read file: " + e.getMessage(), e);
    }
  }

  @Override
  public void erase()
  {
    emfac.tx().go((emgr) -> {
      int erasedCount = emgr.createUntypedQuery("delete from " + entName + " e where e.fsName = :fsName",
          "fsName", getFileSystemName()).executeUpdate();
      return null;
    });

  }

  @Override
  public List<FsObject> listFiles(String name, Matcher<String> matcher, Character searchType, boolean recursive)
  {
    final List<FsObject> ret = new ArrayList<FsObject>();
    final String nornName = normalizeName(name);
    Long thisPk = findPk(nornName);
    if (thisPk == null) {
      return ret;
    }
    LogicClause clause = Clauses.and(Clauses.equal("fsName", getFileSystemName()));
    if (name == null || name.equals("/") == true) {
      // nix
    } else {
      clause.add(Clauses.like("name", nornName + "/%"));
    }
    if (searchType != null) {
      clause.add(Clauses.equal("fileType", searchType));
    }
    if (recursive == false) {
      clause.add(Clauses.equal("parent", thisPk));
    }
    StringBuilder sb = new StringBuilder();
    Map<String, Object> args = new HashMap<>();

    clause.renderClause(sb, "e", args);
    emfac.notx().go((emgr) -> {

      List<JpaFilesystemDO> list = emgr.selectAttached(JpaFilesystemDO.class,
          getNewShortHql() + " where " + sb.toString(), args);
      for (JpaFilesystemDO fsd : list) {
        if (matcher != null) {
          String mn = fsd.getName().substring(name.length());
          if (matcher.match(mn) == false) {
            continue;
          }
        }
        ret.add(convert(fsd));
      }
      return null;

    });
    return ret;
  }

  @Override
  public long getModificationCounter()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void checkEvents(boolean force)
  {
    if (force == true || lastCheckSystemMs + checkFileSystemIntervallMs < System.currentTimeMillis()) {
      checkNotifications();
      super.checkEvents(force);
    }
  }

  protected synchronized void checkNotifications()
  {
    Date lmod = new Date(lastEventCollected);
    lastEventCollected = System.currentTimeMillis();
    List<JpaFilesystemDO> res = emfac.notx().go((emgr) -> {
      return emgr.selectAttached(JpaFilesystemDO.class,
          getNewShortHql() + " where e.fsName = :fsName and e.modifiedAt > :modifiedAt",
          "fsName", getFileSystemName(), "modifiedAt", lmod);

    });
    long lastMod = lastEventCollected;
    for (JpaFilesystemDO fo : deletedFiles) {
      FsObject obj = convert(fo);
      FileSystemEventType fsEvent = FileSystemEventType.Deleted;
      long modTime = obj.getLastModified();
      String name = fo.getName();
      if (name.startsWith("/") == true) {
        name = name.substring(1);
      }
      addEvent(fsEvent, name, modTime);
    }
    deletedFiles.clear();
    for (JpaFilesystemDO fo : res) {
      FsObject obj = convert(fo);
      long modTime = obj.getLastModified();
      FileSystemEventType fsEvent = FileSystemEventType.Modified;
      if (obj.getCreatedAt().compareTo(obj.getModifiedAt()) == 0 && obj.getUpdateCounter() == 0) {
        fsEvent = FileSystemEventType.Created;
      }
      String name = obj.getName();
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
  public <R> R runInTransaction(String lockFile, long timeOut, boolean noModFs, CallableX<R, RuntimeException> callback)
  {
    return emfac.tx().timeOut(timeOut).go((emgr) -> {
      return callback.call();
    });
  }

}
