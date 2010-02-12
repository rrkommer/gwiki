package de.micromata.genome.gwiki.umgmt;

/**
 * Just takes a password and generates hashed value.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class MkPassHash
{
  public static void main(String[] args)
  {
    if (args.length == 0) {
      System.out.println("Please provide clear text password as argument");
      return;
    }
    System.out.println("\nHash:\n" + GWikiUserAuthorization.encrypt(args[0]));
  }
}
