package de.micromata.genome.gwiki.page.impl.wiki.rte.els;

import java.util.List;

import org.apache.commons.collections15.ArrayStack;
import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentTable;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentTable.Cell;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentTable.Row;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;
import de.micromata.genome.gwiki.utils.StringUtils;

public class RteTableDomElementListener implements DomElementListener
{
  private final Logger LOG = Logger.getLogger(RteTableDomElementListener.class);
  ArrayStack<GWikiFragment> tableStack = new ArrayStack<>();

  @Override
  public boolean listen(DomElementEvent event)
  {
    GWikiWikiParserContext parseContext = event.getParseContext();
    if (event.getElementName().equals("TABLE") == true) {
      GWikiFragment frag;
      NamedNodeMap attrs = event.element.getAttributes();
      if (attrs.getLength() == 0
          || (attrs.getLength() == 1 && event.getStyleClass().equals("gwikiTable") == true)) {
        frag = new GWikiFragmentTable();
      } else {
        frag = convertToBodyMacro(event,
            GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NewLineAfterStart,
                GWikiMacroRenderFlags.NewLineBeforeEnd, GWikiMacroRenderFlags.TrimTextContent));
      }
      tableStack.push(frag);
      List<GWikiFragment> childs = event.walkCollectChilds();
      addChildren(frag, childs);
      tableStack.pop();
      frag = checkConvertTable(frag, event);
      parseContext.addFragment(frag);
      return false;
    }
    if (event.getElementName().equals("TBODY") == true) {
      List<GWikiFragment> childs = event.walkCollectChilds();
      parseContext.addFragments(childs);
      return false;
    }

    if (event.getElementName().equals("TR") == true) {
      GWikiFragment top = tableStack.peek();
      if (top instanceof GWikiFragmentTable) {
        GWikiFragmentTable table = (GWikiFragmentTable) top;
        GWikiFragmentTable.Row row = new GWikiFragmentTable.Row();
        copyAttributes(row.getAttributes(), event);
        table.addRow(row);
        event.walkCollectChilds();
      } else if (top instanceof GWikiMacroFragment) {
        GWikiMacroFragment frag = convertToBodyMacro(event, 0);
        parseContext.addFragment(frag);
        List<GWikiFragment> childs = event.walkCollectChilds();
        frag.addChilds(childs);
      }
      return false;

    }
    if (event.getElementName().equals("TH") == true || event.getElementName().equals("TD") == true) {
      GWikiFragment top = tableStack.peek();
      if (top instanceof GWikiFragmentTable) {
        String en = event.getElementName().toLowerCase();
        GWikiFragmentTable table = (GWikiFragmentTable) top;
        GWikiFragmentTable.Cell cell = table.addCell(en);
        copyAttributes(cell.getAttributes(), event);
        List<GWikiFragment> childs = event.walkCollectChilds();
        table.addCellContent(childs);
      } else {
        GWikiMacroFragment frag = convertToBodyMacro(event, 0);
        parseContext.addFragment(frag);
        List<GWikiFragment> childs = event.walkCollectChilds();
        frag.addChilds(childs);

      }
    }
    return false;
  }

  protected GWikiFragment checkConvertTable(GWikiFragment frag, DomElementEvent event)
  {
    if ((frag instanceof GWikiFragmentTable) == false) {
      return frag;
    }
    GWikiFragmentTable table = (GWikiFragmentTable) frag;
    if (needConvert(table) == false) {
      return frag;
    }
    GWikiMacroFragment ret = convertToBodyMacro("table",
        GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NewLineAfterStart,
            GWikiMacroRenderFlags.NewLineBeforeEnd, GWikiMacroRenderFlags.TrimTextContent,
            GWikiMacroRenderFlags.NoWrapWithP));
    ret.getAttrs().getArgs().setStringValue("class", "gwikiTable");
    for (Row row : table.getRows()) {
      GWikiMacroFragment rowmacro = convertToBodyMacro("tr",
          GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NewLineAfterStart, GWikiMacroRenderFlags.NewLineBeforeEnd,
              GWikiMacroRenderFlags.TrimTextContent,
              GWikiMacroRenderFlags.NoWrapWithP));

      for (Cell cell : row.cells) {
        GWikiMacroFragment cellmacro = convertToBodyMacro(cell.attributes.getCmd(),
            GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.TrimTextContent,
                GWikiMacroRenderFlags.NoWrapWithP));
        String thdclass = "gwikitd";
        if (StringUtils.equals(cell.attributes.getCmd(), "th") == true) {
          thdclass = "gwikith";
        }
        cellmacro.getAttrs().getArgs().setStringValue("class", thdclass);
        cellmacro.addChilds(cell.attributes.getChildFragment().getChilds());
        rowmacro.addChild(cellmacro);
      }
      ret.addChild(rowmacro);
    }
    return ret;
  }

  private boolean needConvert(GWikiFragmentTable table)
  {
    for (Row row : table.getRows()) {
      for (Cell cell : row.cells) {
        String source = cell.getAttributes().getChildFragment().getSource();
        if (source.contains("\n") == true) {
          return true;
        }
      }
    }
    return false;
  }

  protected void addChildren(GWikiFragment frag, List<GWikiFragment> childs)
  {
    if (frag instanceof GWikiMacroFragment) {
      GWikiMacroFragment bm = (GWikiMacroFragment) frag;
      bm.addChilds(childs);
    } else {

    }
  }

  protected GWikiMacroFragment convertToBodyMacro(DomElementEvent event, int macroRenderModes)
  {
    MacroAttributes ma = convertToMaAttributes(event);
    return new GWikiMacroFragment(new GWikiHtmlBodyTagMacro(macroRenderModes), ma);
  }

  protected GWikiMacroFragment convertToBodyMacro(String tag, int macroRenderModes)
  {
    MacroAttributes ma = new MacroAttributes(tag);
    return new GWikiMacroFragment(new GWikiHtmlBodyTagMacro(macroRenderModes), ma);
  }

  protected MacroAttributes convertToMaAttributes(DomElementEvent event)
  {
    String en = event.getElementName().toLowerCase();
    MacroAttributes ma = new MacroAttributes(en);
    copyAttributes(ma, event);
    return ma;
  }

  protected void copyAttributes(MacroAttributes target, DomElementEvent event)
  {
    NamedNodeMap attrs = event.element.getAttributes();
    for (int i = 0; i < attrs.getLength(); ++i) {
      String k = attrs.item(i).getLocalName();
      String v = attrs.item(i).getNodeValue();
      target.getArgs().setStringValue(k, v);
    }
  }
}
