package de.micromata.genome.gwiki.fssvn;

import java.util.List;

import junit.framework.TestCase;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;

public class MyFirstTest extends TestCase
{
  public void testIt()
  {
    String url = "https://svn.micromata.de/svn/testsvn/trunk";
    try {

      Sardine sardine = SardineFactory.begin("testsvn", "testsvn", SslUtils.createEasySSLSocketFactory());
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
