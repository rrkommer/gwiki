package de.micromata.genome.util.xml.xmlbuilder;

/**
 * function logic
 * 
 * @author roger
 * 
 */
public class Logic
{
  /**
   * 
   * @param condition
   * @param node
   * @return null if condiation is false
   */
  public static <T> T If(boolean condition, T node)
  {
    return IfElse(condition, node, null);
  }

  public static <T> T IfElse(boolean condition, T node, T elseNode)
  {
    return condition ? node : elseNode;
  }
}
