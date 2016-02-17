package de.micromata.genome.gwiki.launcher;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.jetty.GWikiStarterConfig;
import de.micromata.genome.gwiki.jetty.ValidationContext;
import de.micromata.genome.gwiki.jetty.ValidationMessage;
import de.micromata.genome.javafx.feedback.FeedbackPanel;
import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.genome.util.runtime.LocalSettingsEnv;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class ConfigurationDialog extends AbstractControler
{
  private static final String FILESYSTEM_STORAGE = "Filesystem";
  /**
   * Preferred Scene Width.
   */
  public static final int PREF_WIDTH = 600;

  /**
   * Preferred Scene Height.
   */
  public static final int PREF_HEIGHT = 441;

  MainWindow mainWindow;
  @FXML
  private CheckBox startServerImmediatelly;
  @FXML
  private CheckBox launchBrowserOnStartup;

  @FXML
  private TextField serverPort;
  @FXML
  private TextField serverContextPath;

  @FXML
  private TextField publicUrl;
  @FXML
  private TextField publicEmail;
  @FXML
  private CheckBox sysUserEnabled;
  @FXML
  private TextField sysUserName;
  @FXML
  private TextField sysPassword;

  @FXML
  private TextField storageFilePath;
  @FXML
  private Button storageFilePathSelector;

  @FXML
  private ComboBox<String> jdbcDriver;

  @FXML
  private TextField jdbcUrl;

  @FXML
  private TextField jdbcUser;
  @FXML
  private TextField jdbcPassword;

  private GWikiStarterConfig starterConfig = new GWikiStarterConfig();
  @FXML
  protected FeedbackPanel feedback;

  @Override
  public void initialize(URL location, ResourceBundle resources)
  {

    starterConfig.initFromLocalSettings(LocalSettings.get());
    fromModel();

    serverPort.textProperty().addListener((tc, oldVal, newVal) -> {
      String oldUrl = publicUrl.getText();
      String sc = serverContextPath.getText();
      String tu = "http://localhost:" + oldVal + sc;
      if (StringUtils.equals(oldUrl, tu) == true) {
        String nu = "http://localhost:" + newVal + sc;
        publicUrl.setText(nu);
      }
    });

    sysUserEnabled.setOnAction(e -> {
      enableSystemUser(sysUserEnabled.isSelected());
    });
    storageFilePathSelector.setOnAction(e -> {
      DirectoryChooser fileChooser = new DirectoryChooser();
      fileChooser.setInitialDirectory(new File(storageFilePath.getText()));
      File res = fileChooser.showDialog(getStage());
      if (res != null) {
        storageFilePath.setText(res.getAbsolutePath());
      }
    });

    List<String> items = new ArrayList<>();

    for (StorageTypes sb : StorageTypes.values()) {
      items.add(sb.getDescription());
    }
    if (starterConfig.isUseJdbc() == false) {
      jdbcDriver.setValue(StorageTypes.LOCAL_FILESYSTEM.getDescription());
    } else {
      StorageTypes st = StorageTypes.byDriver(starterConfig.getJdbcDriver());
      if (st != null) {
        jdbcDriver.setValue(st.getDescription());
      }
    }
    jdbcDriver.setItems(FXCollections.observableArrayList(items));
    jdbcDriver.valueProperty().addListener((observable, oldValue, newValue) -> {
      StorageTypes db = StorageTypes.byDescription(newValue);
      StorageTypes olddb = StorageTypes.byDescription(oldValue);

      jdbcUrl.promptTextProperty().set(db.getSampleUrl());
      if (StringUtils.isBlank(jdbcUrl.getText()) == true) {
        jdbcUrl.setText(db.getSampleUrl());
      } else if (olddb != null && StringUtils.equals(oldValue, olddb.getDescription()) == true) {
        jdbcUrl.setText(db.getSampleUrl());
      }
      enableJdbc(db.isRequireUrls());
    });

  }

  private void enableJdbc(boolean db)
  {
    storageFilePathSelector.setDisable(db);
    storageFilePath.setDisable(db);
    jdbcUrl.setDisable(db == false);
    jdbcUser.setDisable(db == false);
    jdbcPassword.setDisable(db == false);
  }

  private void enableSystemUser(boolean enable)
  {
    sysUserEnabled.setSelected(enable);
    sysUserName.setDisable(enable == false);
    sysPassword.setDisable(enable == false);
  }

  protected void fromModel()
  {
    sysUserName.setText(starterConfig.getSystemUserName());
    if (StringUtils.isNotBlank(starterConfig.getSystemUserEncPass()) == true) {
      sysPassword.setText("");
    }
    serverPort.setText(starterConfig.getServerPort());

    serverContextPath.setText(starterConfig.getServerContextPath());
    publicUrl.setText(starterConfig.getPublicUrl());

    publicEmail.setText(starterConfig.getPublicEmail());
    enableSystemUser(starterConfig.isSystemUserEnabled());

    storageFilePath.setText(starterConfig.getStorageFilePath());
    jdbcDriver.setValue(starterConfig.getJdbcDriver());
    jdbcUrl.setText(starterConfig.getJdbcUrl());
    jdbcUser.setText(starterConfig.getJdbcUserName());
    jdbcPassword.setText(starterConfig.getJdbcPassword());

    enableJdbc(starterConfig.isUseJdbc());
    if (starterConfig.isUseJdbc() == false) {
      jdbcDriver.setValue(StorageTypes.LOCAL_FILESYSTEM.getDescription());
    } else {
      StorageTypes st = StorageTypes.byDriver(starterConfig.getJdbcDriver());
      if (st == null) {
        st = StorageTypes.LOCAL_DERBY;
      }
      jdbcDriver.setValue(st.getDescription());
    }
  }

  protected void toModel()
  {
    starterConfig.setSystemUserEnabled(sysUserEnabled.isSelected());
    if (sysUserEnabled.isSelected() == true) {
      starterConfig.setSystemUserName(sysUserName.getText());
      starterConfig.setSystemUserClearPass(sysPassword.getText());
    }
    starterConfig.setServerPort(serverPort.getText());
    starterConfig.setServerContextPath(serverContextPath.getText());
    starterConfig.setPublicUrl(publicUrl.getText());
    starterConfig.setPublicEmail(publicEmail.getText());
    StorageTypes st = StorageTypes.byDescription(jdbcDriver.getValue());
    if (st == null) {
      st = StorageTypes.LOCAL_FILESYSTEM;
    }
    starterConfig.setStorageFilePath(storageFilePath.getText());
    starterConfig.setUseJdbc(st.isJdbc());
    starterConfig.setJdbcDriver(st.getDriver());
    starterConfig.setJdbcUrl(jdbcUrl.getText());
    starterConfig.setJdbcUserName(jdbcUser.getText());
    starterConfig.setJdbcPassword(jdbcPassword.getText());

  }

  public void closeDialog()
  {
    stage.hide();
  }

  @FXML
  private void onCancel(ActionEvent event)
  {
    closeDialog();
  }

  @FXML
  private void onSave(ActionEvent event)
  {
    toModel();
    ValidationContext ctx = new ValidationContext();
    starterConfig.validate(ctx);
    if (ctx.hasErrors() == true) {
      toFeedback(ctx);
      return;
    } else {
      starterConfig.storeConfig(ctx);
      if (ctx.hasErrors() == true) {
        toFeedback(ctx);
        return;
      }
      reinitServer();
      closeDialog();
    }
  }

  private void reinitServer()
  {
    if (mainWindow.isServerRunning() == true) {
      mainWindow.stopServer();
    }
    LocalSettings.reset();
    LocalSettingsEnv.reset();
    if (mainWindow.isServerRunning() == true) {
      mainWindow.startServer();
    }

  }

  protected void toFeedback(ValidationContext ctx)
  {
    feedback.clearMessages();
    for (ValidationMessage vm : ctx.getMessages()) {
      LoggingItem li = new LoggingItem(vm.getMessage(), vm.getLevel());
      feedback.addMessage(li);
    }
  }
}
