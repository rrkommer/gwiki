package de.micromata.genome.gwiki.launcher;

import org.apache.commons.lang.StringUtils;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Helper Methods for GUI Layouting.
 * 
 * @author Daniel (d.ludwig@micromata.de)
 *
 */
public class FXGuiUtils
{

  /**
   * Computes the required height of a text for a given font and a wrapping width. Wrapping width defines when a text
   * needs to be wrapped if its {@link Label#wrapTextProperty()} is true.
   * 
   * @param font font of the label.
   * @param text text to render.
   * @param wrappingWidth wrapping width.
   * @return height.
   */
  public static double computeTextHeight(Font font, String text, double wrappingWidth)
  {
    final Text helper = new Text();
    helper.setText(text);
    helper.setFont(font);

    helper.setWrappingWidth((int) wrappingWidth);
    return helper.getLayoutBounds().getHeight();
  }

  /**
   * Computes the required height of the label to fully render its text.
   * 
   * @param width used if the {@link Label#wrapTextProperty()} is true. Then this value is the max line width when the
   *          text needs to be wrapped.
   * @param msg the label.
   * @return required height.
   */
  public static double computeLabelHeight(double width, Label msg)
  {
    final Font font = msg.getFont();
    final String str = msg.getText();
    return computeTextHeight(font, str,
        msg.isWrapText() ? width : 0);
  }
  
  /**
   * Creates a "safe" CSS Selector from a String. That means replacing all empty spaces with a unqiue value,
   * because spaces in CSS Selectors are interpreted as the beginning of a new selector.
   * @param value value to convert.
   * @return a safe CSS Selector.
   */
  public static String cssSafeSelectorId(final String value) {
    return StringUtils.replace(value, " ", "$BLANK$");
  }
}
