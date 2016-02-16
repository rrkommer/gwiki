/**
 *  Project: VLS
 *  Copyright(c) 2015 by Deutsche Post AG
 *  All rights reserved.
 */
package de.micromata.genome.javafx.feedback;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import de.micromata.genome.gwiki.launcher.FXGuiUtils;
import de.micromata.genome.gwiki.launcher.LoggingItem;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.InsetsBuilder;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.ColumnConstraintsBuilder;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.RowConstraintsBuilder;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.stage.Window;

/**
 * A control that displays Feedback Messages.
 * 
 * Does automatically resize the owning window.
 * 
 * Style-Class: feedback
 * 
 * @author Daniel (d.ludwig@micromata.de)
 * 
 */
public class FeedbackPanel extends GridPane implements Initializable
{
  /**
   * Percent divisor...
   */
  private static final int PERC_DIV = 100;

  /**
   * Radius of circle rendered in front of messages.
   */
  private static final int LI_RADIUS = 4;

  /**
   * Spacing of hbox content.
   */
  private static final int HBOX_SPACING = 10;

  /**
   * Hbox left padding.
   */
  private static final int HBOX_PAD_LEFT = 10;

  /**
   * Style class for this control.
   */
  private static final String CSS_CLASS_FEEDBACK = "feedback";

  /**
   * Column width in percent.
   */
  private static final int COLUMN_WIDTH_PERCENT = 100;

  /**
   * Padding.
   */
  private static final int PADDING = 10;

  /**
   * Stores the size of the owning stage before rendering existing messages.
   * 
   * This value is used to resize the owning window to its initial size.
   */
  private double initialWindowHeight = -1d;

  /**
   * Required to calculate Label Heights.
   */
  private final List<Label> messages = new ArrayList<>();

  /**
   * Constructor.
   */
  public FeedbackPanel()
  {
    setMinHeight(0);
    setPrefHeight(0);
    setMaxHeight(0);

    // Width as computed by the parent container.
    setMinWidth(Region.USE_COMPUTED_SIZE);
    setPrefWidth(Region.USE_COMPUTED_SIZE);
    setMaxWidth(Region.USE_COMPUTED_SIZE);

    // move the messages a little bit to the center of the view.
    final Insets padding = InsetsBuilder.create().left(PADDING).top(PADDING).right(PADDING).build();
    setPadding(padding);

    setId(CSS_CLASS_FEEDBACK);
  }

  /**
   * Adds a message to this feedback panel, computes the new size of this control and resizes the window (stage).
   * 
   * @param message Message to render.
   */
  public final void addMessage(LoggingItem message)
  {
    messageToRow(message);

    computePanelHeight();
  }

  /**
   * Adds a collection of messages and computes the required height of this panel.
   * 
   * @param messages messages to add.
   */
  public final void addMessages(Collection<LoggingItem> messages)
  {
    for (LoggingItem li : messages) {
      messageToRow(li);
    }

    computePanelHeight();
  }

  /**
   * set the new window height based on the required height of this feedbackpanel.
   */
  public final void updateWindowHeight()
  {
    if (getScene() == null) {
      return;
    }
    final Window window = getScene().getWindow();
    window.setHeight(initialWindowHeight() + getPrefHeight());
  }

  /**
   * Removes all messages from this feedbackpanel, sets its height to zero and resizes the owning window to its initial
   * size.
   */
  public final void clearMessages()
  {
    if (getScene() == null) {
      return;
    }
    Window window = getScene().getWindow();
    window.setHeight(initialWindowHeight());
    setPrefHeight(0);
    setMinHeight(0);

    messages.clear();
    getChildren().clear();
    getRowConstraints().clear();
  }

  /**
   * 
   * {@inheritDoc}
   * 
   */
  @Override
  public void initialize(URL arg0, ResourceBundle arg1)
  {
    ColumnConstraints column = ColumnConstraintsBuilder.create().hgrow(Priority.ALWAYS)
        .percentWidth(COLUMN_WIDTH_PERCENT).build();
    getColumnConstraints().add(column);

    // for some reason the grid pane already has one row.
    getRowConstraints().clear();
  }

  /**
   * 
   * {@inheritDoc}
   * 
   */
  @Override
  protected double computePrefHeight(double width)
  {
    double height = 0;
    for (Label msg : messages) {
      height += FXGuiUtils.computeLabelHeight(width, msg);
    }
    return height + (messages.size() * 10);
  }

  /**
   * Computes the height of this panel depending on the messages to render.
   */
  private void computePanelHeight()
  {
    // half the width and 80% provided good results
    // the padding is used because otherwise other containers may overlap. Multiplied by 2 for bottom and top.
    final double computedHeight = computePrefHeight(getScene().getWidth());

    setPrefHeight(computedHeight);
    setMinHeight(computedHeight);
    setMaxHeight(computedHeight);
  }

  /**
   * Compute the initial window size (only height).
   * 
   * @return the height.
   */
  private double initialWindowHeight()
  {
    // window.getHeight can return nan if the view was not rendered before.
    int compare = Double.compare(initialWindowHeight, Double.NaN);
    int windowHeight = (int) initialWindowHeight;
    if (windowHeight == -1 || compare == 0) {
      Window window = getScene().getWindow();
      initialWindowHeight = window.getHeight();
    }

    return initialWindowHeight;
  }

  /**
   * Converts a message to a grid row and adds it automatically.
   * 
   * @param message the message to convert.
   */
  private void messageToRow(final LoggingItem message)
  {
    RowConstraints row = RowConstraintsBuilder.create().minHeight(10).prefHeight(USE_COMPUTED_SIZE).fillHeight(false)
        .vgrow(Priority.NEVER).build();
    getRowConstraints().add(row);
    int current = getRowConstraints().size() - 1;
    addRow(current, messageContainer(message, row));
  }

  /**
   * Adds all the necessary nodes to the container.
   * 
   * @param message message to render.
   * @param row the current row, needed to set its height depending on the calculated text height.
   * @return the rendered message control.
   */
  private Parent messageContainer(final LoggingItem message, final RowConstraints row)
  {
    final Label label = LabelBuilder.create().text(message.getMessage()).wrapText(true).build();
    Scene scene = getScene();
    final double hboxWidth = (scene.getWidth() * 97) / PERC_DIV;

    // the height of the label text is highly dependent from the horizontal space that is available
    // for the label. If this value is not correct calculated we get invalid height values.
    final double hboxHeight = FXGuiUtils.computeLabelHeight(getScene().getWidth()
        - (HBOX_PAD_LEFT + HBOX_SPACING + LI_RADIUS + (getScene().getWidth() - hboxWidth)), label);
    row.setMinHeight(hboxHeight);
    row.setPrefHeight(hboxHeight);
    row.setPrefHeight(hboxHeight);
    final HBox hbox = HBoxBuilder.create().alignment(Pos.CENTER_LEFT).minHeight(hboxHeight)
        .prefHeight(hboxHeight).maxHeight(hboxHeight).spacing(HBOX_SPACING).id("row")
        .padding(InsetsBuilder.create().left(HBOX_PAD_LEFT).build()).id("message")
        .styleClass(message.getType().toString()).build();

    final DoubleBinding widthBinding = new DoubleBinding()
    {
      @Override
      protected double computeValue()
      {
        return hboxWidth;
      }
    };

    hbox.minWidthProperty().bind(widthBinding);
    hbox.prefWidthProperty().bind(widthBinding);

    final Circle dot = CircleBuilder.create().radius(LI_RADIUS).strokeWidth(0).styleClass(message.getType().toString())
        .id("icon").build();
    hbox.getChildren().add(dot);
    messages.add(label);
    hbox.getChildren().add(label);
    return hbox;
  }
}
