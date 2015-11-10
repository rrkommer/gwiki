package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

import org.junit.Assert;
import org.junit.Test;

public class SplitTest
{
  private void check(String line, String[] expected)
  {
    Assert.assertArrayEquals(expected, CsvTable.split(line, '|'));
  }

  @Test
  public void testSplit()
  {
    check("", new String[] { ""});
    check("bla", new String[] { "bla"});
    check("a|b", new String[] { "a", "b"});
    check("|a|b", new String[] { "", "a", "b"});
    check("a|||b", new String[] { "a", "", "", "b"});

    String[] rec = CsvTable.split("1|440 hertz|Wolferl 200||||||||ADD|||1991||||||||||1319||0|Wolferl_200|1|1899-12-30 00:01:14||", '|');
    String t = rec[26];
  }
}
