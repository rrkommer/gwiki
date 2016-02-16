package de.micromata.genome.gwiki.launcher;

import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public abstract class AbstractControler implements Initializable
{
  protected Parent parent;
  protected Scene scene;
  protected Stage stage;

  public Parent getParent()
  {
    return parent;
  }

  public void setParent(Parent parent)
  {
    this.parent = parent;
  }

  public Scene getScene()
  {
    return scene;
  }

  public void setScene(Scene scene)
  {
    this.scene = scene;
  }

  public Stage getStage()
  {
    return stage;
  }

  public void setStage(Stage stage)
  {
    this.stage = stage;
  }

}
