package de.micromata.genome.gwiki.chronos;

import de.micromata.genome.gwiki.chronos.logging.LogAttribute;
import de.micromata.genome.gwiki.chronos.logging.LogAttributeRuntimeException;

/**
 * Base class for Exception thrown by jobs
 * 
 * @author roger
 * 
 */
public class JobControlException extends LogAttributeRuntimeException
{

  private static final long serialVersionUID = -5339065645182169281L;
  /**
   * Wenn silent == true, wird vom jchronos die exception nicht mehr geloggt
   */
  private boolean silent = false;

  public JobControlException()
  {
    super();
  }

  public JobControlException(String message, boolean captureLogContext)
  {
    super(message, captureLogContext);
  }

  public JobControlException(String message, LogAttribute... attrs)
  {
    super(message, attrs);
  }

  public JobControlException(String message, Throwable cause, boolean captureLogContext, LogAttribute... attrs)
  {
    super(message, cause, captureLogContext, attrs);
  }

  public JobControlException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public JobControlException(String message)
  {
    super(message);
  }

  public JobControlException(Throwable cause)
  {
    super(cause);
  }

  public boolean isSilent()
  {
    return silent;
  }

  public void setSilent(boolean silent)
  {
    this.silent = silent;
  }

}
