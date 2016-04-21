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

package de.micromata.genome.gwiki.page.impl.wiki.rte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Comment;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.micromata.genome.gwiki.page.impl.wiki.rte.els.RteBrDomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.rte.els.RteCodeDomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.rte.els.RteCodeFormatDomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.rte.els.RteHeadingStyleDomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.rte.els.RteImageDomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.rte.els.RteLiDomentElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.rte.els.RteLinkDomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.rte.els.RteListDomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.rte.els.RteMacroDomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.rte.els.RtePDomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.rte.els.RteSimpleStyleDomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.rte.els.RteSmileyDomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.rte.els.RteSpanDomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.rte.els.RteTableDomElementListener;

public class RteDomVisitor implements DomVisitor
{
  private static Logger LOG = Logger.getLogger(RteDomVisitor.class);
  Map<String, List<DomElementListener>> listener = new HashMap<>();
  List<DomTextListener> textListener = new ArrayList<>();

  public RteDomVisitor()
  {
    registerListener("SPAN", new RteSmileyDomElementListener());
    registerListener("IMG", new RteImageDomElementListener());
    registerListener("A", new RteLinkDomElementListener());
    registerListener("BR", new RteBrDomElementListener());
    registerListener("P", new RtePDomElementListener());
    registerListener("SPAN", new RteSpanDomElementListener());
    registerListener("PRE", new RteCodeDomElementListener());
    registerListener("CODE", new RteCodeFormatDomElementListener());
    registerListener("SPAN", new RteMacroDomElementListener());
    registerListener("DIV", new RteMacroDomElementListener());
    RteListDomElementListener listDomListener = new RteListDomElementListener();
    registerListener("UL", listDomListener);
    registerListener("OL", listDomListener);
    registerListener("LI", new RteLiDomentElementListener());
    RteTableDomElementListener tableListener = new RteTableDomElementListener();
    registerListener("TABLE", tableListener);
    registerListener("TBODY", tableListener);
    registerListener("TR", tableListener);
    registerListener("TH", tableListener);
    registerListener("TD", tableListener);

    RteSimpleStyleDomElementListener.registerListeners(this);
    RteHeadingStyleDomElementListener.registerListeners(this);
    textListener.add(new DomTextListener()
    {
      @Override
      public int getPrio()
      {
        return 10000;
      }

      @Override
      public boolean listen(DomTextEvent event)
      {
        event.getParseContext().addText(event.getText());
        return false;
      }

    });
  }

  public void registerListener(String elname, DomElementListener dl)
  {
    listener.putIfAbsent(elname, new ArrayList<>());
    List<DomElementListener> list = listener.get(elname);
    for (int i = 0; i < list.size(); ++i) {
      if (list.get(i).getPrio() > dl.getPrio()) {
        list.add(i, dl);
        return;
      }
    }
    list.add(dl);
  }

  @Override
  public void visit(HtmlDomWalker wk)
  {
    Node node = wk.currentNode;
    if (node instanceof Element) {
      Element el = (Element) node;
      List<DomElementListener> listeners = listener.get(el.getNodeName());
      if (listeners != null) {
        DomElementEvent event = new DomElementEvent(wk, this, el);
        for (DomElementListener listener : listeners) {
          if (listener.listen(event) == false) {
            return;
          }
        }
      }
    } else if (node instanceof Text) {
      Text tnode = (Text) node;
      DomTextEvent event = new DomTextEvent(wk, this, tnode);
      for (DomTextListener tl : textListener) {
        if (tl.listen(event) == false) {
          return;
        }
      }
    } else if (node instanceof Comment) {
      ; // nothing
    } else if (node instanceof DocumentFragment) {
      ; // nothing
    } else {
      LOG.warn("Unkonwn Node type: " + node);
    }
  }

}
