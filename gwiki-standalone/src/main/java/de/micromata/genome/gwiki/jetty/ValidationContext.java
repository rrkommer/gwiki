package de.micromata.genome.gwiki.jetty;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class ValidationContext
{
  private List<ValidationMessage> messages = new ArrayList<>();

  public boolean error(String message)
  {
    messages.add(new ValidationMessage(Level.ERROR, "", message));
    return false;
  }

  public boolean error(String property, String message)
  {
    messages.add(new ValidationMessage(Level.ERROR, property, message));
    return false;
  }

  public boolean info(String property, String message)
  {
    messages.add(new ValidationMessage(Level.INFO, property, message));
    return true;
  }

  public boolean info(String message)
  {
    messages.add(new ValidationMessage(Level.INFO, "", message));
    return true;
  }

  public List<ValidationMessage> getMessages()
  {
    return messages;
  }

  public boolean hasMessages()
  {
    return messages.isEmpty() == false;
  }

  public boolean hasErrors()
  {
    return messages.stream().anyMatch(vm -> vm.getLevel().toInt() >= Level.ERROR_INT);
  }

}
