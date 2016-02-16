package de.micromata.genome.gwiki.launcher;

import java.util.Date;

import org.apache.log4j.Level;

/**
 * Pojo to store some logging data.
 * 
 * @author Daniel (d.ludwig@micromata.de)
 *
 */
public class LoggingItem
{
  /**
   * The translated message.
   */
  private String message;

  /**
   * Date when message was created.
   */
  private Date time = new Date();

  /**
   * Level of the message.
   */
  private Level type = Level.INFO;

  /**
   * Constructor.
   * @param message the message.
   */
  public LoggingItem(String message)
  {
    this.message = message;
  }

  /**
   * Constructor.
   * @param message the message.
   * @param type the level.
   */
  public LoggingItem(String message, Level type)
  {
    this(message);
    this.type = type;
  }

  /**
   * Getter.
   * @return the message.
   */
  public String getMessage()
  {
    return message;
  }

  /**
   * Setter.
   * @param message the message.
   */
  public void setMessage(String message)
  {
    this.message = message;
  }

  /**
   * Getter.
   * @return the time.
   */
  public Date getTime()
  {
    return time; // NOSONAR
  }

  /**
   * Setter.
   * @param time the time.
   */
  public void setTime(Date time)
  {
    this.time = time; // NOSONAR "we want to change the object"
  }

  /**
   * Getter.
   * @return the level.
   */
  public Level getType()
  {
    return type;
  }

  /**
   * Setter.
   * @param type the level.
   */
  public void setType(Level type)
  {
    this.type = type;
  }
}