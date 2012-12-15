package de.micromata.genome.util.matcher;

import org.junit.Test;

public class BooleanListMatcherTest
{
  @Test
  public void testFoo()
  {
    String pattern = "+pub/plugins/gwiki\\-admintools*";
    new BooleanListRulesFactory<String>().createMatcher(pattern);
  }
}
