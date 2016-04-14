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

package de.micromata.genome.gwiki.auth;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.Validate;

import de.micromata.genome.logging.GenomeLogCategory;
import de.micromata.genome.logging.LogLevel;
import de.micromata.genome.logging.LoggedRuntimeException;
import de.micromata.genome.util.types.Converter;

/**
 * Utility to handle passwords.
 * 
 * @author roger
 * 
 */
public class PasswordUtils
{
  public static final int SALTLENGTH = 16;

  /**
   * Creates a salted password
   * 
   * @param clearPassword
   * @return
   */
  public static String createSaltedPassword(String clearPassword)
  {
    Validate.notNull(clearPassword);
    Random r = new SecureRandom();
    String salt = RandomStringUtils.random(SALTLENGTH, 32, 127, true, true, null, r);
    return encodePassword(salt, clearPassword);
  }

  /**
   * Check if password is valid. First checks if salted password and then make fallback to old unsalted implementation.
   * 
   * @param clearPassword
   * @param encodedPassword
   * @return
   */
  public static boolean checkPassword(String clearPassword, String encodedPassword)
  {
    if (checkSaltedPassword(clearPassword, encodedPassword) == true) {
      return true;
    }
    return checkUnsaltedPassword(clearPassword, encodedPassword);
  }

  /**
   * Standart asymetrischen password verschlüsseln mit SHA-256 für Genome
   * 
   * @param plaintext Nie <code>null</code>
   * @return password hash
   */

  public static String unsaltedSecureHash(String plaintext)
  {
    Validate.notNull(plaintext, "plaintext not set");
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(plaintext.getBytes(CharEncoding.UTF_8));
      byte raw[] = md.digest();
      String hash = Converter.encodeBase64(raw);
      return hash;
    } catch (Exception ex) {
      /**
       * @logging
       * @reason Beim asymetrischen verschlüsseln ist ein Fehler aufgetreten.
       * @action Überprüfen der Java-Installation
       */
      throw new LoggedRuntimeException(ex, LogLevel.Fatal, GenomeLogCategory.System, "Error while executing hashing encryption: "
          + ex.getMessage());
    }
  }

  public static String encodePassword(String salt, String clearPassword)
  {

    String encPwd = salt + Converter.encodeBase64(DigestUtils.sha1(salt + clearPassword));
    return salt + encPwd;
  }

  public static boolean checkUnsaltedPassword(String clearPassword, String encodedPassword)
  {
    if (clearPassword == null || encodedPassword == null) {
      return false;
    }
    return encodedPassword.equals(unsaltedSecureHash(clearPassword)) == true;
  }

  public static boolean checkSaltedPassword(String clearPassword, String encodedPassword)
  {
    if (clearPassword == null || encodedPassword == null) {
      return false;
    }
    if (encodedPassword.length() < SALTLENGTH + 1) {
      return false;
    }
    String salt = encodedPassword.substring(0, SALTLENGTH);
    return encodedPassword.equals(encodePassword(salt, clearPassword));
  }

}
