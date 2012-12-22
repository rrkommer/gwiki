package de.micromata.genome.gwiki.fssvn;

import java.util.List;

import org.junit.Test;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;

public class MyFirstTest
{
  @Test
  public void dummy()
  {

  }

  // @Test
  public void myTest()
  {
    String url = "https://svn.micromata.de/svn/testsvn/trunk";
    try {

      Sardine sardine = SardineFactory.begin("", "", SslUtils.createEasySSLSocketFactory());
      List<DavResource> resources = sardine.getResources(url);
      for (DavResource res : resources) {

        System.out.println(res.getName());
        System.out.println("  " + res);

        System.out.println("  " + res.getCustomProps().toString());
        res.isDirectory();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
