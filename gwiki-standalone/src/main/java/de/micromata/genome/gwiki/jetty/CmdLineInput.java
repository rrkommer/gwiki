package de.micromata.genome.gwiki.jetty;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

public class CmdLineInput
{
  public static String readLine()
  {
    StringBuilder sb = new StringBuilder();
    try {
      do {
        int c = System.in.read();
        if (c == '\n') {
          String s = sb.toString();
          if (s.length() > 0 && s.charAt(s.length() - 1) == '\r') {
            s = s.substring(0, s.length() - 1);
          }
          return s;
        }
        sb.append((char) c);
      } while (true);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static void message(String message)
  {
    System.out.println(message);
  }

  public static boolean ask(String message)
  {
    System.out.println(message);
    do {
      System.out.print("(Yes/No): ");
      String inp = StringUtils.trim(readLine());
      inp = inp.toLowerCase();
      if (inp.equals("y") == true || inp.equals("yes") == true) {
        return true;
      }
      if (inp.equals("n") == true || inp.equals("no") == true) {
        return false;
      }
    } while (true);
  }

  public static boolean ask(String message, boolean defaultYes)
  {
    System.out.println(message);
    do {
      if (defaultYes == true) {
        System.out.print("([Yes]/No): ");
      } else {
        System.out.print("(Yes/[No]): ");
      }
      String inp = StringUtils.trim(readLine());
      inp = inp.toLowerCase();
      if (StringUtils.isEmpty(inp) == true) {
        return defaultYes;
      }
      if (inp.equalsIgnoreCase("y") == true || inp.equalsIgnoreCase("yes") == true) {
        return true;
      }
      if (inp.equalsIgnoreCase("n") == true || inp.equalsIgnoreCase("no") == true) {
        return false;
      }
    } while (true);
  }

  public static String getInput(String prompt)
  {
    return getInput(prompt, null);
  }

  public static String getInput(String prompt, String defaultValue)
  {
    return getInput(prompt, defaultValue, false);
  }

  public static String getInput(String prompt, String defaultValue, boolean allowEmpty)
  {
    do {
      if (defaultValue == null) {
        message(prompt + ": ");
      } else {
        message(prompt + "[" + defaultValue + "]: ");
      }
      String inp = readLine();
      inp = StringUtils.trim(inp);
      if (defaultValue != null) {
        if (inp.length() == 0) {
          return defaultValue;
        }
      }
      if (allowEmpty == true || inp.length() > 0) {
        return inp;
      }
    } while (true);
  }

}
