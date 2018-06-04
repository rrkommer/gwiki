//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.jetty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.auth.GWikiSysUserAuthorization;
import de.micromata.genome.gwiki.auth.PasswordUtils;
import de.micromata.genome.util.collections.SortedProperties;
import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.genome.util.runtime.RuntimeIOException;
import de.micromata.genome.util.text.PlaceHolderReplacer;
import de.micromata.genome.util.types.Pair;

/**
 * Utility class to create initial configuration.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiInitialSetup extends CmdLineInput
{

  private String gwikiPropFileName = "gwiki.properties";

  private Map<String, String> templateParams = new HashMap<>();
  private boolean createNewContextFile = true;
  // TODO RK does not really works.
  private String configLocation = "";
  Map<String, String> props = new HashMap<>();
  Map<String, String> initialProps = new HashMap<>();

  public GWikiInitialSetup()
  {
    String fn = System.getProperty("gwiki.properties.file");
    if (StringUtils.isNotEmpty(fn) == true) {
      gwikiPropFileName = fn;
    }
  }

  protected File getConfigLocation(String fileName)
  {
    if (StringUtils.isEmpty(configLocation) == true || configLocation.equals(".") == true) {
      return new File(new File(".").getAbsoluteFile(), fileName);
    }
    return new File(new File(configLocation).getAbsoluteFile(), fileName);
  }

  /**
   * 
   * @return true on first startup.
   */
  public boolean readCheckBasicSettings()
  {
    boolean firstStart = checkBasicSettings();
    return firstStart;

  }

  protected void checkEmailServer()
  {
    if (ask("GWiki sends email to notify page changes and in case user resets the password.\n"
        + "Do you want to configure an Email server?", false) == false) {
      return;
    }

    //    props.put("mail.smtp.ssl.enable", "false");
    //    props.put("mail.smtp.starttls.enable", "false");
    //    props.put("mail.smtp.user", "");
    //    props.put("mail.smtp.password", "");
    //    if (ask("GWiki sends email to notify page changes and in case user resets the password.\n"
    //        + "Do you want to configure an Email server?", false) == false) {
    //      props.put("mail.smtp.auth", "false");
    //      props.put("mail.smtp.host", "localhost");
    //      props.put("mail.smtp.port", "25");
    //      return;
    //    }
    props.put("mail.session.gwiki.name", "GWikiMailSession");
    String server = getInput("Mail servers hostname");
    props.put("mail.session.gwiki.smtp.host", server);
    String port = getInput("Mail servers port", "25");
    props.put("mail.session.gwiki.smtp.port", port);
    if (ask("Need your email server authentification?") == true) {
      props.put("mail.session.gwiki.smtp.auth", "true");
      message("Input now email server account data. The password will be stored as clear text in the gwiki.properties");
      String user = getInput("email server user");
      props.put("mail.session.gwiki.smtp.user", user);
      String pass = getInput("email server password");
      props.put("mail.session.gwiki.smtp.password", pass);
    } else {
      props.put("mail.session.gwiki.smtp.auth", "false");
      props.put("mail.session.gwiki.smtp.user", "");
      props.put("mail.session.gwiki.smtp.password", "");
    }
    if (ask("Enbale StartTLSin for EMail SMPT")) {
      props.put("mail.session.gwiki.smtp.starttls.enable", "true");
    }
    if (ask("Enbale SSL in for EMail SMPT")) {
      props.put("mail.session.gwiki.smtp.ssl.enable", "true");
    }

    props.put("jndi.bind.gwikimailsession.target", "java:comp/env/genome/mail/Session");
    props.put("jndi.bind.gwikimailsession.type", "MailSession");
    props.put("jndi.bind.gwikimailsession.source", "GWikiMailSession");

  }

  public File getGwikiProperties()
  {
    File propFile = getConfigLocation(gwikiPropFileName);
    return propFile;
  }

  protected boolean checkBasicSettings()
  {
    File propFile = getGwikiProperties();
    if (propFile.exists() == true) {
      return false;
    }
    return createNewProperties();
  }

  protected boolean createNewProperties()
  {

    message("Welcome to GWiki.\n"
        + "You are running GWiki the first time.\nPlease answer following questions.\n"
        + "If the prompt has [defaultValue] you can also accept the defaultValue by hitting enter.\n\n");

    getConfigLocation(gwikiPropFileName);
    getHttpServer();
    getFileSystem();

    if (ask("Generate System user?", true) == true) {
      props.put(GWikiSysUserAuthorization.LS_GWIKI_SYS_USER, getInput("user name", "gwikisys"));
      String pass;
      do {
        pass = StringUtils.trim(getInput("user password"));
        if (pass.length() < 5) {
          message("password should have at least 5 characters");
          continue;
        }
        break;
      } while (true);
      String salted = PasswordUtils.createSaltedPassword(pass);
      props.put(GWikiSysUserAuthorization.LS_GWIKI_SYS_PASSWORDHASH, salted);
    }
    String enableWebDAV = "false";
    if (ask("Enable User for WebDAV Access?", false) == true) {
      enableWebDAV = "true";
    }
    props.put("gwiki.enable.webdav", enableWebDAV);

    checkEmailServer();

    createContextFile();
    storeConfig();
    message("Configuration finished.\n");
    return true;
  }

  protected void storeConfig()
  {
    getConfigLocation();
    File propFile = getConfigLocation(gwikiPropFileName);
    String contextFile = getGWikiContextFile();
    props.put("gwiki.contextfile", contextFile);

    Properties properties = new SortedProperties();
    for (Map.Entry<String, String> me : props.entrySet()) {
      properties.put(me.getKey(), me.getValue());
    }
    try (FileOutputStream fis = new FileOutputStream(propFile)) {
      properties.store(fis, "Generated by Gwiki");
    } catch (IOException ex) {
      throw new RuntimeException("Failed to write Properties file: " + ex.getMessage(), ex);
    }
    message("The settings are stored in " + propFile.getAbsolutePath()
        + "\nYou can change the settings using a text editor.");
    message("GWiki Server is now starting");
    LocalSettings ls = LocalSettings.get();
    ls.getMap().putAll(props);
    ls.getMap().putAll(this.initialProps);

  }

  private void getConfigLocation()
  {
    configLocation = new File("").getAbsolutePath();
    if (true) {
      return;
    }
    do {
      String path = getInput("Directory to store Gwiki configuration", ".");
      File file = new File(path);
      if (file.isDirectory() == false) {
        message("directory does not exists: " + file.getAbsolutePath());
        continue;
      }
      configLocation = file.getAbsolutePath();
      break;
    } while (true);

  }

  private void getHttpServer()
  {
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
  }

  private void createContextFile()
  {
    if (createNewContextFile == false) {
      return;
    }
    try (InputStream is = getClass().getClassLoader().getResourceAsStream("GWikiContextTemplate.xml")) {
      String content = IOUtils.toString(is, Charset.forName("UTF-8"));
      String contxml = PlaceHolderReplacer.resolveReplace(content, "%{", "}", (repl) -> templateParams.get(repl));
      File file = getConfigLocation("GWikiContext.xml");
      FileUtils.write(file, contxml, Charset.forName("UTF-8"));
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  private String getGWikiContextFile()
  {
    if (ask("Use existing GWikiContext.xml file", false) == false) {
      createNewContextFile = true;
      return getConfigLocation("GWikiContext.xml").getAbsolutePath();
    }
    createNewContextFile = false;
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
    return contextFile;
  }

  private void getFileSystem()
  {
    if (checkDbSettings() == true) {
      props.put("gwiki.wikifilepath", "");
      return;
    }
    getLocalFileSystem();

  }

  private void getLocalFileSystem()
  {
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
      templateParams.put("PRIMARYFILESYSTEM",
          "             <bean class=\"de.micromata.genome.gdbfs.StdFileSystem\">\r\n" +
              "                <constructor-arg>\r\n" +
              "                  <value>${gwiki.wikifilepath}</value>\r\n" +
              "                </constructor-arg>\r\n" +
              "              </bean>");
      break;
    } while (true);
  }

  protected StandaloneDatabases getDataBase()
  {
    do {
      message("Select a Database type: ");
      for (StandaloneDatabases db : StandaloneDatabases.values()) {
        message(" (" + db.getNum() + ") " + db.getDescription());
      }
      message(" E(x)it");

      String input = getInput("1, 2, 3 or x: ");
      if (StringUtils.isBlank(input) == true) {
        continue;
      }
      if (StringUtils.equalsIgnoreCase(input, "x") == true) {
        return null;
      }
      StandaloneDatabases db = StandaloneDatabases.getDatabaseByInput(input);
      if (db != null) {
        return db;
      }
    } while (true);
  }

  /**
   * 
   * @param db
   * @return first initial, url.
   */
  public Pair<String, String> getJdbcUrl(StandaloneDatabases db)
  {
    Supplier<Pair<String, String>> supl = db.getCmdlineUrlSuplider();
    if (supl != null) {
      return supl.get();
    }
    message(
        "Get the jdbc url (Sample: " + db.getSampleUrl() + ")");
    String inp = getInput("Get the jdbc url (Sample: " + db.getSampleUrl() + "): ");
    return Pair.make(inp, inp);
  }

  protected boolean checkDbSettings()
  {
    do {
      if (ask("Do you want to store pages in Database (otherwise in file system", false) == false) {
        return false;
      }
      StandaloneDatabases db = getDataBase();
      if (db == null) {
        continue;
      }
      Pair<String, String> pair = getJdbcUrl(db);
      String url = pair.getFirst();
      String dbuser = getInput("database user", "", true);
      String dbpass = getInput("database password", "", true);
      if (checkDbUrl(db, url, dbuser, dbpass) == true) {
        props.put("db.ds.gwikdb.name", "gwikidb");
        props.put("db.ds.gwikdb.drivername", db.getDriver());
        this.initialProps.put("db.ds.gwikdb.url", url);
        props.put("db.ds.gwikdb.url", pair.getSecond());
        props.put("db.ds.gwikdb.username", dbuser);
        props.put("db.ds.gwikdb.password", dbpass);
        props.put("genomeds", "gwikidb");

        props.put("jndi.bind.standard.target", "java:comp/env/genome/jdbc/dsWeb");
        props.put("jndi.bind.standard.type", "DataSource");
        props.put("jndi.bind.standard.source", "gwikidb");

        templateParams.put("PRIMARYFILESYSTEM",
            "             <bean class=\"de.micromata.genome.gdbfs.jpa.JpaFileSystemImpl\">\r\n" +
                "              </bean>");

        initialProps.put("hibernate.hbm2ddl.auto", "update");
        return true;
      }
      continue;
    } while (false);

    return true;
  }

  private boolean checkDbUrl(StandaloneDatabases db, String url, String user, String pass)
  {
    try {
      Class.forName(db.getDriver());
      try (Connection con = DriverManager.getConnection(url, user, pass)) {
        try (Statement stmt = con.createStatement()) {
          message("Created DB Connection....");
        }
      }
      return true;
    } catch (ClassNotFoundException e) {
      message("Cannot find db driver");
      return false;
    } catch (SQLException e) {
      message("Cannot create connection: " + e.getMessage());
      return false;
    }
  }
}
