package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class CsvTableTest
{
  @Test
  public void testLoad()
  {
    String fname = "D:\\ourMP3gwiki\\acccexp\\Titel.csv";
    if (new File(fname).exists() == false) {
      return;
    }
    CsvTable table = new CsvTable();
    table.load(new File(fname));
    table.createIndex(1);
    List<String[]> found = table.findEquals(1, "Tubin, Eduard");
    System.out.println("found: " + found);
  }
}
