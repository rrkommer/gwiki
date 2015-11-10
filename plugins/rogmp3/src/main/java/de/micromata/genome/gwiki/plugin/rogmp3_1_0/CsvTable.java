package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.types.Pair;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class CsvTable
{
  List<String[]> table = new ArrayList<String[]>();

  private char fieldSeperator = '|';

  private String encoding = "UTF-8"; // "ISO-8859-1";

  private Map<Integer, TreeMap<String, List<String[]>>> indices = new TreeMap<Integer, TreeMap<String, List<String[]>>>();

  public void load(File file)
  {
    try {
      load(new FileInputStream(file));
    } catch (FileNotFoundException e) {
      throw new RuntimeException("CsvTable; File cannot found: " + file.getAbsolutePath() + "; " + e.getMessage(), e);
    }
  }

  public void store(File file)
  {
    StringBuilder sb = new StringBuilder();
    for (String[] line : table) {
      sb.append(StringUtils.join(line, '|')).append("\n");
    }
    try {
      FileOutputStream fout = new FileOutputStream(file);
      OutputStreamWriter sout = new OutputStreamWriter(fout, encoding);
      sout.write(sb.toString());
      IOUtils.closeQuietly(sout);
      IOUtils.closeQuietly(fout);
    } catch (IOException ex) {
      throw new RuntimeException("CsvTable; File write : " + file.getAbsolutePath() + "; " + ex.getMessage(), ex);
    }
  }

  public List<String> loadLines(InputStream is)
  {
    try {
      return IOUtils.readLines(is, encoding);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static String[] split(String line, char sepChar)
  {
    int count = 1;
    for (int i = 0; i < line.length(); ++i) {
      if (line.charAt(i) == sepChar) {
        ++count;
      }
    }
    String[] ret = new String[count];
    int lastPos = 0;
    int curRetIdx = 0;
    for (int i = 0; i < line.length(); ++i) {
      if (line.charAt(i) == sepChar) {
        ret[curRetIdx] = line.substring(lastPos, i);
        ++curRetIdx;
        lastPos = i + 1;
        continue;
      }
    }
    if (curRetIdx < ret.length) {
      ret[ret.length - 1] = line.substring(lastPos);

    }
    return ret;
  }

  public void load(InputStream is)
  {
    List<String> lines = loadLines(is);
    IOUtils.closeQuietly(is);
    for (String l : lines) {
      if (StringUtils.isBlank(l) == true) {
        continue;
      }
      table.add(split(l, fieldSeperator));
    }
  }

  public void sortByIndex(List<String[]> rec, final int... idx)
  {
    Collections.sort(rec, new Comparator<String[]>() {

      @Override
      public int compare(String[] o1, String[] o2)
      {
        for (int i = 0; i < idx.length; ++i) {
          int c = o1[idx[i]].compareTo(o2[idx[i]]);
          if (c != 0) {
            return c;
          }
        }
        return 0;
      }
    });
  }

  public void add(String[] row)
  {
    table.add(row);
    for (Map.Entry<Integer, TreeMap<String, List<String[]>>> me : indices.entrySet()) {
      if (row.length > me.getKey()) {
        String keyval = row[me.getKey()];
        List<String[]> sl = me.getValue().get(keyval);
        if (sl == null) {
          sl = new ArrayList<String[]>();
          sl.add(row);
          me.getValue().put(keyval, sl);
        } else {
          sl.add(row);
        }
      }
    }
  }

  public List<String[]> getSortByIndex(final int... idx)
  {
    List<String[]> ret = new ArrayList<String[]>(table);
    sortByIndex(ret, idx);
    return ret;
  }

  public void createIndex(int column)
  {
    TreeMap<String, List<String[]>> index = new TreeMap<String, List<String[]>>();
    for (String[] rec : table) {
      if (rec.length <= column) {
        continue;
      }
      String n = rec[column];
      List<String[]> el = index.get(n);
      if (el == null) {
        el = new ArrayList<String[]>();
        index.put(n, el);
      }
      el.add(rec);
    }
    indices.put(column, index);
  }

  // public List<String[]> find(int idx, String name)
  // {
  // TreeMap<String, List<String[]>> index = indices.get(idx);
  // if (index != null) {
  // return index.get(name);
  // }
  //
  // }

  public List<String[]> getUniqueSorted(int idx)
  {
    Map<String, String[]> ret = new TreeMap<String, String[]>();
    for (String[] rec : table) {
      if (rec.length > idx) {
        ret.put(rec[idx], rec);
      }
    }
    return new ArrayList<String[]>(ret.values());
  }

  public String[] findFirst(int idx, String name)
  {
    TreeMap<String, List<String[]>> index = indices.get(idx);
    if (index != null) {
      List<String[]> n = index.get(name);
      if (n == null) {
        return null;
      }
      return n.get(0);
    }
    for (String[] rec : table) {
      if (rec.length > idx) {
        String n = rec[idx];
        if (name.equals(n) == true) {
          return rec;
        }
      }
    }
    return null;
  }

  public List<String[]> findEquals(int idx, String name)
  {
    TreeMap<String, List<String[]>> index = indices.get(idx);
    if (index != null) {
      List<String[]> ret = index.get(name);
      if (ret != null) {
        return ret;
      }
      return new ArrayList<String[]>();
    }
    List<String[]> ret = new ArrayList<String[]>();
    for (String[] rec : table) {
      if (rec.length > idx) {
        String n = rec[idx];
        if (name.equals(n) == true) {
          ret.add(rec);
        }
      }
    }
    return ret;
  }

  public List<String[]> findMultiEquals(Pair<Integer, String>... keys)
  {
    if (keys.length > 0) {
      TreeMap<String, List<String[]>> idx = indices.get(keys[0].getFirst());
      if (idx != null) {
        Pair<Integer, String>[] sk = Arrays.copyOfRange(keys, 1, keys.length);
        List<String[]> res = idx.get(keys[0].getValue());
        if (res == null) {
          return Collections.emptyList();
        }
        return findMultiEquals(res, sk);
      }
    }
    return findMultiEquals(table, keys);
  }

  private List<String[]> findMultiEquals(List<String[]> subtable, Pair<Integer, String>... keys)
  {
    List<String[]> ret = new ArrayList<String[]>();
    nextRow: for (String[] rec : subtable) {
      for (Pair<Integer, String> key : keys) {
        Integer idx = key.getFirst();
        if (rec.length <= idx) {
          continue nextRow;
        }
        String n = rec[idx];
        if (key.getSecond().equals(n) == false) {
          continue nextRow;
        }
      }
      ret.add(rec);
    }
    return ret;
  }

  public List<String[]> findContaining(int idx, String name)
  {
    List<String[]> ret = new ArrayList<String[]>();
    for (String[] rec : table) {
      if (rec.length > idx) {
        String n = rec[idx];
        if (n.contains(name) == true) {
          ret.add(rec);
        }
      }
    }
    return ret;
  }
}
