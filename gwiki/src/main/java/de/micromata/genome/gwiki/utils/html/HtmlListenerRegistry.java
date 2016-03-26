package de.micromata.genome.gwiki.utils.html;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;

import de.micromata.genome.gwiki.utils.html.HtmlFilterListener.HtmlEvent;

public class HtmlListenerRegistry
{
  List<List<HtmlFilterListener>> listenerStack = new ArrayList<>();
  public Html2WikiFilter html2WikiFilter;

  public HtmlListenerRegistry(Html2WikiFilter html2WikiFilter)
  {
    this.html2WikiFilter = html2WikiFilter;
    listenerStack.add(new ArrayList<>());
  }

  public void addListener(HtmlFilterListener listener)
  {
    List<HtmlFilterListener> ls = listenerStack.get(0);
    for (int i = 0; i < ls.size(); ++i) {
      HtmlFilterListener cl = ls.get(i);
      if (cl.listenerPrio() > listener.listenerPrio()) {
        ls.add(i, listener);
        return;
      }
    }
    ls.add(listener);
  }

  public void pushListener(HtmlFilterListener listener)
  {
    ArrayList<HtmlFilterListener> nstack = new ArrayList<>();
    nstack.add(listener);
    listenerStack.add(nstack);
  }

  public void popListener(HtmlFilterListener listener)
  {
    List<HtmlFilterListener> ls = listenerStack.get(listenerStack.size() - 1);
    // TODO check if listenerId
    listenerStack.remove(listenerStack.size() - 1);
  }

  private boolean iterate(HtmlEvent event, BiFunction<HtmlFilterListener, HtmlEvent, Boolean> function)
  {
    for (int i = listenerStack.size() - 1; i >= 0; --i) {
      List<HtmlFilterListener> ls = listenerStack.get(i);
      for (int j = 0; j < ls.size(); ++j) {
        HtmlFilterListener listener = ls.get(j);
        boolean r = function.apply(listener, event);
        if (r == false) {
          return false;
        }
      }
    }
    return true;
  }

  public boolean startElement(QName element, XMLAttributes attributes, Augmentations augs)
  {
    HtmlEvent event = new HtmlEvent(this, element, attributes);
    return iterate(event, (listener, cevent) -> listener.startElement(cevent));

  }

  public boolean emptyElement(QName element, XMLAttributes attributes, Augmentations augs)
  {
    HtmlEvent event = new HtmlEvent(this, element, attributes);
    return iterate(event, (listener, cevent) -> listener.emptyElement(cevent));
  }

  public boolean endElement(QName element, Augmentations augs)
  {
    HtmlEvent event = new HtmlEvent(this, element);
    return iterate(event, (listener, cevent) -> listener.endElement(cevent));
  }

  public boolean characters(String t, StringBuilder collectedText)
  {
    HtmlEvent event = new HtmlEvent(this, t, collectedText);
    return iterate(event, (listener, cevent) -> listener.characters(cevent));
  }
}
