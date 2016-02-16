package de.micromata.genome.gwiki.jetty;

import org.apache.log4j.Level;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class ValidationMessage
{
  private Level level = Level.ERROR;
  private String message;
  private String property;

  public ValidationMessage()
  {

  }

  public ValidationMessage(String message)
  {
    this.message = message;
  }

  public ValidationMessage(Level level, String property, String message)
  {
    this.level = level;
    this.message = message;
    this.property = property;
  }

  public String getMessage()
  {
    return message;
  }

  public void setMessage(String message)
  {
    this.message = message;
  }

  public Level getLevel()
  {
    return level;
  }

  public void setLevel(Level type)
  {
    this.level = type;
  }

  public String getProperty()
  {
    return property;
  }

  public void setProperty(String property)
  {
    this.property = property;
  }

}
