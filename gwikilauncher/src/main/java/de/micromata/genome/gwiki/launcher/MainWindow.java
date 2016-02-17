package de.micromata.genome.gwiki.launcher;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.jetty.JettyStartListener;
import de.micromata.genome.gwiki.model.GWikiLogCategory;
import de.micromata.genome.javafx.logging.LoggingController;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.LogExceptionAttribute;
import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.genome.util.types.Pair;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class MainWindow implements Initializable
{
  private GWikiLauncherServer server = new GWikiLauncherServer();
  @FXML
  private Button startServerButton;
  @FXML
  private Button stopServerButton;

  @FXML
  private Button openBrowser;

  @FXML
  private Pane loggingPane;

  @FXML
  private LoggingController loggingController;

  @Override
  public void initialize(URL location, ResourceBundle resources)
  {

    startServerButton.setOnAction(e -> {
      startServer();
    });
    stopServerButton.setOnAction(e -> {
      stopServer();
    });
    stopServerButton.setDisable(true);

    openBrowser.setOnAction(e -> {
      launchBrowser();
    });
    loggingPane.widthProperty().addListener(new ChangeListener<Number>()
    {
      @Override
      public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth)
      {
        loggingController.adjustWidth(newSceneWidth.doubleValue());
      }
    });
    loggingPane.heightProperty().addListener(new ChangeListener<Number>()
    {
      @Override
      public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth)
      {
        loggingController.adjustHeight(newSceneWidth.doubleValue());
      }
    });
    loggingController.adjustHeight(loggingPane.getHeight());
    loggingController.adjustWidth(loggingPane.getWidth());
  }

  public void startServer()
  {
    if (LocalSettings.localSettingsExists() == false) {
      loggingController.warn("GWiki is not configured.");
      return;
    }
    server.start(new JettyStartListener()
    {

      @Override
      public void started(StartSucces success, Exception exception)
      {
        switch (success) {
          case Error:
            loggingController.error(exception.getMessage());
            break;
          case NoConfiguration:
            loggingController.warn("GWiki is not configured.");
            break;
          case AlreadyRunning:
            loggingController.info("GWiki server already started.");
          case Success:
            startServerButton.setDisable(true);
            stopServerButton.setDisable(false);
            loggingController.info("GWiki server started.");
            break;
        }
      }

    });

  }

  public void stopServer()
  {
    server.stop(new JettyStartListener()
    {

      @Override
      public void stopped(StartSucces success, Exception exception)
      {
        if (success == StartSucces.Error) {
          GLog.error(GWikiLogCategory.Wiki, "Cannot stop server: " + exception.getMessage(),
              new LogExceptionAttribute(exception));
        } else {
          stopServerButton.setDisable(true);
          startServerButton.setDisable(false);
          GLog.note(GWikiLogCategory.Wiki, "Server stopped");
        }
      }
    });

  }

  public boolean isServerRunning()
  {
    return server.isRunning();
  }

  @FXML
  private void openConfigDialog(ActionEvent event)
  {
    final Pair<Pane, ConfigurationDialog> load = FxUtils.loadScene("/fxml/ConfigurationDialog.fxml", Pane.class,
        ConfigurationDialog.class);
    Pane root = load.getKey();
    ConfigurationDialog controller = load.getValue();
    controller.mainWindow = this;
    Stage stage = new Stage();
    stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
      controller.closeDialog();
      e.consume();
    });
    //    controller.setOwningStage(stage);
    final Scene s = new Scene(root, ConfigurationDialog.PREF_WIDTH, ConfigurationDialog.PREF_HEIGHT);
    controller.setParent(root);
    controller.setScene(s);
    controller.setStage(stage);
    stage.setScene(s);
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setResizable(false);
    stage.show();
    stage.setTitle("Configuration");
  }

  @FXML
  private void closeApplication(ActionEvent event)
  {
    if (server != null) {
      server.stop();
    }
    Platform.exit();
    System.exit(0); // NOSONAR    System.exit(...) and Runtime.getRuntime().exit(...) should not be called" Main app exit.
  }

  private void launchBrowser()
  {
    String puburl = LocalSettings.get().get("gwiki.public.url");
    if (StringUtils.isBlank(puburl) == true) {
      loggingController.error("No public url configured");
      return;
    }

    Desktop desktop = null;
    if (Desktop.isDesktopSupported()) {
      desktop = Desktop.getDesktop();
    }

    if (desktop != null) {
      try {
        desktop.browse(new URI(puburl));
      } catch (final IOException e) {
        loggingController.error("Can't launch browser: " + e.getMessage(), e);
      } catch (final URISyntaxException e) {
        loggingController.error("Can't launch browser: " + e.getMessage(), e);
      }
    }
  }

  public LoggingController getLoggingController()
  {
    return loggingController;
  }

}
