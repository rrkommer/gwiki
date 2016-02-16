package de.micromata.genome.gwiki.launcher;

import java.io.IOException;

import de.micromata.genome.util.runtime.RuntimeIOException;
import de.micromata.genome.util.types.Pair;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class FxUtils
{
  public static <T extends Node, C> Pair<T, C> loadScene(String path, Class<T> nodeClass, Class<C> controlerClass)
  {
    FXMLLoader fxmlLoader = new FXMLLoader(FxUtils.class.getResource(path));
    try {
      Object loaded = fxmlLoader.load();
      Object controler = fxmlLoader.getController();
      return Pair.make((T) loaded, (C) controler);

    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  public static void runInToolkitThread(final Runnable runnable)
  {

    if (Platform.isFxApplicationThread() == true) {
      runnable.run();
      return;
    }

    Platform.runLater(runnable);
  }
}
