package de.micromata.genome.gwiki.launcher;

import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.genome.util.types.Pair;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiLauncher extends Application
{
  public static void main(String args[])
  {
    LocalSettings.localSettingsPrefixName = "gwiki";
    launch(args);
  }

  @Override
  public void start(Stage stage)
  {
    stage.setTitle("GWiki");
    stage.setWidth(1150);
    stage.setHeight(700);
    //    stage.getIcons().add(new Image(getClass().getResourceAsStream("/print_dhl_logo_64x64.png")));
    Pair<Parent, MainWindow> lc = FxUtils.loadScene("/fxml/MainWindow.fxml", Parent.class, MainWindow.class);
    MainWindow mainWindow = lc.getSecond();
    Scene scene = new Scene(lc.getFirst());
    stage.setScene(scene);

    stage.show();
  }
}
