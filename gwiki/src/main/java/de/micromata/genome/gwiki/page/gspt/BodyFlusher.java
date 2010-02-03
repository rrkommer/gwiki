package de.micromata.genome.gwiki.page.gspt;

import java.io.IOException;

/**
 * Flush current buffer, but not flush target.
 * 
 * @author roger
 * 
 */
public interface BodyFlusher
{
  public void flushBody() throws IOException;
}
