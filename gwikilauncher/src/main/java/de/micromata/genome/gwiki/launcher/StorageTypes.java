package de.micromata.genome.gwiki.launcher;

public enum StorageTypes
{
  LOCAL_FILESYSTEM("Local file system", false, false, "", ""),

  LOCAL_DERBY("Embedded Derby", true, true, "org.apache.derby.jdbc.EmbeddedDriver", "jdbc:derby:gwiki-derby"),

  ORACLE("Oracle", true, true, "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@localhost:1521:gwiki"),

  POSTGRES("Postgres", true, true, "org.postgresql.Driver", "jdbc:postgresql://localhost:5432/gwiki"),

  ;
  private final String description;
  private final boolean jdbc;
  private final boolean requireUrls;
  private final String sampleUrl;
  private final String driver;

  private StorageTypes(String description, boolean jdbc, boolean requireUrls, String driver, String sampleUrl)
  {
    this.description = description;
    this.jdbc = jdbc;
    this.requireUrls = requireUrls;
    this.sampleUrl = sampleUrl;
    this.driver = driver;
  }

  public static StorageTypes byDescription(String desc)
  {
    for (StorageTypes st : values()) {
      if (st.getDescription().equals(desc) == true) {
        return st;
      }
    }
    return null;
  }

  public static StorageTypes byDriver(String driver)
  {
    for (StorageTypes st : values()) {
      if (st.getDriver().equals(driver) == true) {
        return st;
      }
    }
    return null;
  }

  public String getDescription()
  {
    return description;
  }

  public boolean isJdbc()
  {
    return jdbc;
  }

  public boolean isRequireUrls()
  {
    return requireUrls;
  }

  public String getSampleUrl()
  {
    return sampleUrl;
  }

  public String getDriver()
  {
    return driver;
  }

}
