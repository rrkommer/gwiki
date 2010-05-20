package de.micromata.genome.gwiki.jetty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.auth.GWikiSimpleUserAuthorization;

/**
 * Utility class to create initial configuration.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiInitialSetup
{
  private String gwikiPropFileName = "gwiki.properties";

  public GWikiInitialSetup()
  {
    String fn = System.getProperty("gwiki.properties.file");
    if (StringUtils.isNotEmpty(fn) == true) {
      gwikiPropFileName = fn;
    }
  }

  protected String readLine()
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

  protected void message(String message)
  {
    System.out.println(message);
  }

  protected boolean ask(String message)
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

  protected String getInput(String prompt)
  {
    return getInput(prompt, null);
  }

  protected String getInput(String prompt, String defaultValue)
  {
    do {
      if (defaultValue == null) {
        message(prompt + ": ");
      } else {
        message(prompt + "[" + defaultValue + "]: ");
      }
      String inp = readLine();
      if (defaultValue != null) {
        if (inp.length() == 0) {
          return defaultValue;
        }
      }
      if (inp.length() > 0) {
        return inp;
      }
    } while (true);
  }

  protected void setSysProp(String key, Properties props, String defaultValue)
  {
    if (StringUtils.isEmpty(System.getProperty(key)) == false) {
      return;
    }
    System.setProperty(key, StringUtils.defaultString(props.getProperty(key), defaultValue));
  }

  /**
   * 
   * @return true on first startup.
   */
  public boolean readCheckBasicSettings()
  {
    boolean firstStart = checkBasicSettings();
    Properties props = new Properties();
    File propFile = new File(gwikiPropFileName);
    try {
      props.load(new FileInputStream(new File(gwikiPropFileName)));
    } catch (IOException ex) {
      throw new RuntimeException("Failed to load properties file: " + propFile.getAbsolutePath() + ": " + ex.getMessage(), ex);
    }
    setSysProp("gwiki.jetty.port", props, "8081");
    setSysProp("gwiki.jetty.contextpath", props, "/");
    setSysProp("gwiki.wikifilepath", props, "./gwiki");
    setSysProp("gwiki.sys.user", props, "");
    setSysProp("gwiki.sys.passwordhash", props, "");
    setSysProp("gwiki.enable.webdav", props, "false");
    setSysProp("gwiki.dev.path", props, "./gwiki");
    setSysProp("de.micromata.genome.gwiki.contextfile", props, "GWikiContext.xml");
    for (String k : (Set<String>) (Set) props.keySet()) {
      setSysProp(k, props, "");
    }
    return firstStart;

  }

  protected void checkEmailServer(Properties props)
  {
    if (ask("GWiki sends email to notify page changes and in case user resets the password.\n"
        + "Do you want to configure an Email server?") == false) {
      return;
    }
    String server = getInput("Mail servers hostname");
    props.put("mail.smtp.host", server);
    String port = getInput("Mail servers port", "25");
    props.put("mail.smtp.host", port);
    if (ask("Need your email server authentification?") == true) {
      props.put("mail.smtp.auth", "true");
      message("Input now email server account data. The password will be stored as clear text in the gwiki.properties");
      String user = getInput("email server user");
      props.put("mail.smtp.user", user);
      String pass = getInput("email server user");
      props.put("mail.smtp.password", pass);
    } else {
      props.put("mail.smtp.auth", "false");
    }

  }

  protected boolean checkBasicSettings()
  {
    File propFile = new File(gwikiPropFileName);
    if (propFile.exists() == true) {
      return false;
    }
    message("Welcome to GWiki.\n"
        + "You are running GWiki the first time.\nPlease answer following questions.\n"
        + "If the prompt has [defaultValue] you can also accept the defaultValue by hitting enter.\n\n");
    Properties props = new Properties();
    String port;
    do {
      port = getInput("HTTP Port", "8081");
      try {
        Integer.parseInt(port);
      } catch (NumberFormatException ex) {
        message("Port should be a number");
        continue;
      }
      props.put("gwiki.jetty.port", port);
      break;
    } while (true);
    String contextPath;
    do {
      contextPath = getInput("Context path", "/");
      if (contextPath.length() == 0 //
          || (contextPath.length() > 0 && contextPath.startsWith("/") == false)
          || (contextPath.length() > 1 && contextPath.endsWith("/") == true)) {
        message("Context path must start with / and if context path not a single / context path must not end with /");
        continue;
      }
      props.put("gwiki.jetty.contextpath", contextPath);
      break;
    } while (true);
    do {
      String pubUrl = getInput("Public Url", "http://localhost:" + port + contextPath);
      props.put("gwiki.public.url", pubUrl);
      break;
    } while (true);
    do {
      String pubemail = getInput("Public Sender Email", "gwiki-noreply@labs.micromata.de");
      props.put("gwiki.public.email", pubemail);
      break;
    } while (true);
    String defaultPath = new File("./gwiki").getAbsolutePath();
    do {
      message("Where to store gwiki files? Please select an empty directory.");
      String wikiPath = getInput("GWiki file", defaultPath);
      File d = new File(wikiPath);
      if (d.exists() == true) {
        if (d.isDirectory() == false) {
          message(d.getAbsolutePath() + " is not a directory.");
          continue;
        }
      } else {
        if (d.mkdirs() == false) {
          message("Cannot create directory: " + d.getAbsolutePath());
          continue;
        }
      }
      props.put("gwiki.wikifilepath", d.getAbsolutePath());
      break;
    } while (true);

    if (ask("Generate System user?") == true) {
      props.put("gwiki.sys.user", getInput("user name", "gwikisys"));
      String pass;
      do {
        pass = getInput("user password");
        if (pass.length() < 5) {
          message("password should have at least 5 characters");
          continue;
        }
        break;
      } while (true);
      props.put("gwiki.sys.passwordhash", GWikiSimpleUserAuthorization.encrypt(pass));
    }
    String enableWebDAV = "false";
    if (ask("Enable User for WebDAV Access?") == true) {
      enableWebDAV = "true";
    }
    props.put("gwiki.enable.webdav", enableWebDAV);

    String contextFile = "GWikiContext.xml";
    do {
      contextFile = getInput("Location of GWikiContext.xml file", "GWikiContext.xml");
      File f = new File(contextFile);
      if (f.exists() == false || f.isFile() == false) {
        message("File cannot be found or is not a file: " + f.getAbsolutePath());
        continue;
      }
      break;
    } while (true);

    checkEmailServer(props);
    message("Configuration finished.\n");
    props.put("de.micromata.genome.gwiki.contextfile", contextFile);
    try {
      props.store(new FileOutputStream(propFile), "Generated by Gwiki");
    } catch (IOException ex) {
      throw new RuntimeException("Failed to write Properties file: " + ex.getMessage(), ex);
    }
    message("The settings are stored in " + propFile.getAbsolutePath() + "\nYou can change the settings using a text editor.");
    message("GWiki Server is now starting");
    return true;

  }
}
