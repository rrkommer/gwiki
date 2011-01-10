package de.micromata.genome.gwiki.chronos.spi.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ClassPathReplaceResource extends ClassPathResource
{
  private Map<String, String> replaceMap = new HashMap<String, String>();

  public ClassPathReplaceResource(String path, Class<?> clazz)
  {
    super(path, clazz);
  }

  public ClassPathReplaceResource(String path, ClassLoader classLoader, Class<?> clazz)
  {
    super(path, classLoader, clazz);
  }

  public ClassPathReplaceResource(String path, ClassLoader classLoader)
  {
    super(path, classLoader);
  }

  public ClassPathReplaceResource(String path)
  {
    super(path);
  }

  @Override
  public InputStream getInputStream() throws IOException
  {
    return super.getInputStream();
  }

  @Override
  public Resource createRelative(String relativePath)
  {
    return super.createRelative(relativePath);
  }

  public Map<String, String> getReplaceMap()
  {
    return replaceMap;
  }

  public void setReplaceMap(Map<String, String> replaceMap)
  {
    this.replaceMap = replaceMap;
  }

}
