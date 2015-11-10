package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

import org.junit.Test;

public class GenITunesTest
{
  @Test
  public void testGen()
  {
    String dbPath = "D:\\ourMP3gwiki\\acccexp";
    String mproot = "D:\\ourMP3\\mp3root\\classic";
    String outF = "D:\\ourMP3gwiki\\rogMp3.xml";
    Mp3Db db = Mp3Db.get(dbPath, mproot);

    GenItunesFile git = new GenItunesFile(db, outF);
    git.renderItunes();
    System.out.println("MP3 size: " + git.getSumMp3Size() / (1024 * 1024) + " MB");
  }
}
