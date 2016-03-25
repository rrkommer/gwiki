package de.micromata.genome.gwiki.launcher.config;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.types.Pair;
import de.micromata.mgc.application.MgcApplicationInfo;
import de.micromata.mgc.javafx.ControllerService;
import de.micromata.mgc.javafx.ModelGuiField;
import de.micromata.mgc.javafx.launcher.MgcLauncher;
import de.micromata.mgc.javafx.launcher.gui.AbstractConfigTabController;
import de.micromata.mgc.javafx.launcher.gui.generic.JdbcConfigTabController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiFilesystemController extends AbstractConfigTabController<GWikiFilesystemConfigModel>
{
  @ModelGuiField
  @FXML
  private CheckBox useContextXml;
  @FXML
  private Pane contextXmlPane;
  @ModelGuiField
  @FXML
  private TextField contextXml;

  @FXML
  private Button contextXmlFileSelector;
  @ModelGuiField
  @FXML
  private ComboBox<String> storageType;
  @FXML
  private Pane filesystemPane;
  @FXML
  private Pane localFilesystemPane;
  @FXML
  private Button storageFilePathSelector;
  @ModelGuiField
  @FXML
  private TextField storageFilePath;
  @FXML
  private Pane jdbcMasterPane;
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
  public void initializeWithModel()
  {
    MgcApplicationInfo info = MgcLauncher.get().getApplication().getApplicationInfo();
    info.getName();
    Pair<Pane, JdbcConfigTabController> jdbc = ControllerService.get()
        .loadControlWithModel(JdbcConfigTabController.class, Pane.class, model.getJdbcConfigModel(), this);
    jdbcPane = jdbc.getFirst();
    jdbcController = jdbc.getSecond();
    jdbcMasterPane.getChildren().add(jdbcPane);
    jdbcPane.setLayoutY(30);
    fromModel();
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
    useContextXml.setOnAction(event -> switchContextXml(useContextXml.isSelected()));
    contextXmlFileSelector.setOnAction(event -> {
      FileChooser chooser = new FileChooser();
      chooser.setInitialDirectory(new File("."));
      chooser.getExtensionFilters().add(new ExtensionFilter("XML File", "*.xml"));
      File res = chooser.showOpenDialog(getConfigDialog().getStage());
      if (res != null) {
        contextXml.setText(res.getAbsolutePath());
      }
    });
  }

  protected void switchContextXml(boolean contentXml)
  {
    contextXmlPane.setVisible(contentXml);
    filesystemPane.setVisible(contentXml == false);
  }

  @Override
  public void fromModel()
  {
    super.fromModel();
    switchContextXml(useContextXml.isSelected());
    boolean jdbc = model.isJdbcStorageType();
    switchToStoragetype(jdbc);
    if (jdbc == true) {
      jdbcController.fromModel();
    }

  }

  private void switchToStoragetype(boolean jdbc)
  {
    localFilesystemPane.setVisible(jdbc == false);
    jdbcPane.setVisible(jdbc);
  }

  @Override
  public void toModel()
  {
    super.toModel();
    boolean jdbc = model.isJdbcStorageType();
    if (jdbc == true) {
      jdbcController.toModel();
    }
  }

  @Override
  public String getTabTitle()
  {
    return "Wiki Filesystem";
  }

}
