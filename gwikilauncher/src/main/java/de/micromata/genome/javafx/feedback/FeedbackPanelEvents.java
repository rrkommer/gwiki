package de.micromata.genome.javafx.feedback;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Event for signaling that feedback messages should be deleted.
 * 
 * @author Daniel (d.ludwig@micromata.de)
 * 
 */
public class FeedbackPanelEvents extends Event
{

  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 2469273350952726996L;

  /**
   * The event type.
   */
  public static final EventType<FeedbackPanelEvents> CLEAR = new EventType<>("clearMessageEvent");

  /**
   * Update the owning windows height.
   */
  public static final EventType<FeedbackPanelEvents> UPDATE_WINDOW_HEIGHT = new EventType<>("updateWindow");

  /**
   * Constructor with {@link #CLEAR} eventtype as default.
   */
  public FeedbackPanelEvents()
  {
    super(CLEAR);
  }

  /**
   * Constructor.
   * 
   * @param type wanted event type.
   */
  public FeedbackPanelEvents(EventType<FeedbackPanelEvents> type)
  {
    super(type);
  }
}