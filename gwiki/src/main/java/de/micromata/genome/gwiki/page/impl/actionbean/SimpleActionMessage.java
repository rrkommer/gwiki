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

package de.micromata.genome.gwiki.page.impl.actionbean;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;

public class SimpleActionMessage implements ActionMessage
{

  private static final long serialVersionUID = 8578743673967038098L;

  private String message;

  /**
   * The set of replacement parameters that will be used to create the message from the message template. Note that position 0 is reserved
   * for the field name and position 1 is reserved for the field value.
   */
  private Object[] replacementParameters;

  /**
   * Constructs a message with the supplied message string and zero or more parameters to be merged into the message. When constructing a
   * SimpleMessage a non-null message string must be supplied (though subclasses may return null if they do not rely upon it).
   * 
   * @param message the String message to display to the user, optionally with placeholders for replacement parameters
   * @param parameters
   */
  public SimpleActionMessage(String message, Object... parameters)
  {
    this.replacementParameters = parameters;
    this.message = message;
  }

  /**
   * Helper constructor to allow subclasses to provide and manipulate replacement parameters without having to supply a message String.
   * 
   * @param parameters zero or more paremeters for replacement into the message
   */
  protected SimpleActionMessage(Object... parameters)
  {
    this.replacementParameters = parameters;
  }

  /**
   * Uses the String message passed in as the message template and combines it with any replacement parameters provided to construct a
   * message for display to the user. Although SimpleMessage does not localize it's message string, any formatters invoked as a result of
   * using replacement parameters will be in the correct locale.
   * 
   * @param locale the locale of the current request
   * @return String the message stored under the messageKey supplied
   */
  public String getMessage(Locale locale)
  {
    if (replacementParameters == null || replacementParameters.length == 0) {
      return message;
    }
    // Now get the message itself
    String messageTemplate = getMessageTemplate(locale);
    MessageFormat format = new MessageFormat(messageTemplate, locale);
    return format.format(this.replacementParameters, new StringBuffer(), null).toString();
  }

  /**
   * Simply returns the message passed in at Construction time. Designed to be overridded by subclasses to lookup messages from resource
   * bundles.
   * 
   * @param locale the Locale of the message template desired
   * @return the message (potentially with TextFormat replacement tokens).
   */
  protected String getMessageTemplate(Locale locale)
  {
    return this.message;
  }

  /**
   * Returns the exact message that was supplied in the constructor. This should not be called to render user output, but only when direct
   * access to the String is needed for some reason.
   * 
   * @return the exact message String passed in to the constructor
   */
  protected String getMessage()
  {
    return this.message;
  }

  /** Allows subclasses to access the replacement parameters for this message. */
  protected Object[] getReplacementParameters()
  {
    return this.replacementParameters;
  }

  /**
   * Checks equality by ensuring that the current instance and the 'other' instance are instances of the same class (though not necessarily
   * SimpleMessage!) and that the message String and replacement parameters provided are the same.
   * 
   * @param o another object that is a SimpleMessage or subclass thereof
   * @return true if the two objects will generate the same user message, false otherwise
   */
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final SimpleActionMessage that = (SimpleActionMessage) o;

    if (message != null ? !message.equals(that.message) : that.message != null) {
      return false;
    }
    if (!Arrays.equals(replacementParameters, that.replacementParameters)) {
      return false;
    }

    return true;
  }

  /** Generated hashcode method. */
  public int hashCode()
  {
    return (message != null ? message.hashCode() : 0);
  }
}
