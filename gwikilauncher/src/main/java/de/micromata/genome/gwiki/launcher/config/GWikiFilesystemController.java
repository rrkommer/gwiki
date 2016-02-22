package de.micromata.genome.gwiki.launcher.config;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.types.Pair;
import de.micromata.genome.util.validation.ValContext;
import de.micromata.mgc.javafx.ControllerService;
import de.micromata.mgc.javafx.launcher.MgcLauncher;
import de.micromata.mgc.javafx.launcher.gui.AbstractConfigTabController;
import de.micromata.mgc.javafx.launcher.gui.generic.JdbcConfigTabController;
import de.micromata.mgc.launcher.MgcApplicationInfo;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiFilesystemController extends AbstractConfigTabController<GWikiFilesystemConfigModel>
{
  @FXML
  private ComboBox<String> storageType;
  @FXML
  private AnchorPane localFilesystemPane;
  @FXML
  private Button storageFilePathSelector;
  @FXML
  private TextField storageFilePath;
  private Pane jdbcPane;
  private JdbcConfigTabController jdbcController;

  private File getDefaultStorageFilePath()
  {
    if (StringUtils.isBlank(storageFilePath.getText()) == true) {
      return new File(".");
    }
    return new File(storageFilePath.getText());
  }

  @Override
  public void initializeWithModel(GWikiFilesystemConfigModel model)
  {
    MgcApplicationInfo info = MgcLauncher.get().getApplication().getApplicationInfo();
    info.getName();
    Pair<Pane, JdbcConfigTabController> jdbc = ControllerService.get()
        .loadControlWithModel(JdbcConfigTabController.class, Pane.class, model.getJdbcConfigModel());
    jdbcPane = jdbc.getFirst();
    jdbcController = jdbc.getSecond();
    tabPane.getChildren().add(jdbcPane);
    jdbcPane.setLayoutY(30);
    fromModel(model);
    storageType.setItems(FXCollections.observableArrayList(model.getAvailableFileSystemTypes()));
    storageType.setOnAction(event -> switchToStoragetype(storageType.getValue().equals("Database")));
    storageFilePathSelector.setOnAction(e -> {
      DirectoryChooser fileChooser = new DirectoryChooser();
      fileChooser.setInitialDirectory(getDefaultStorageFilePath());
      File res = fileChooser.showDialog(getConfigDialog().getStage());
      if (res != null) {
        storageFilePath.setText(res.getAbsolutePath());
      }
    });

  }

  @Override
  public void fromModel(GWikiFilesystemConfigModel modelObject)
  {
    storageType.setValue(modelObject.getStorageType());
    boolean jdbc = modelObject.isJdbcStorageType();
    switchToStoragetype(jdbc);
    if (jdbc == true) {
      jdbcController.fromModel(modelObject.getJdbcConfigModel());
    }
    storageFilePath.setText(modelObject.getStorageFilePath());

  }

  private void switchToStoragetype(boolean jdbc)
  {
    localFilesystemPane.setVisible(jdbc == false);
    jdbcPane.setVisible(jdbc);
  }

  @Override
  public void toModel(GWikiFilesystemConfigModel modelObject)
  {
    modelObject.setStorageType(storageType.getValue());
    boolean jdbc = modelObject.isJdbcStorageType();
    if (jdbc == true) {
      jdbcController.toModel(modelObject.getJdbcConfigModel());
    }
    modelObject.setStorageFilePath(storageFilePath.getText());
  }

  @Override
  public void mapValidationMessagesToGui(ValContext ctx)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public String getTabTitle()
  {
    return "Wiki Filesystem";
  }

}