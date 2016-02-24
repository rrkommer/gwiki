package de.micromata.genome.gwiki.launcher.config;

import org.apache.commons.lang.StringUtils;

import de.micromata.mgc.javafx.launcher.gui.AbstractConfigTabController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiSystemUserController extends AbstractConfigTabController<GWikiSystemUserConfigModel>
{
  @FXML
  private CheckBox sysUserEnabled;
  @FXML
  private TextField systemUserName;
  @FXML
  private TextField systemUserClearPass;

  @Override
  public void initializeWithModel(GWikiSystemUserConfigModel model)
  {
    fromModel(model);
    sysUserEnabled.setOnAction(e -> {
      enableSystemUser(sysUserEnabled.isSelected());
    });

  }

  private void enableSystemUser(boolean enable)
  {
    sysUserEnabled.setSelected(enable);
    systemUserName.setDisable(enable == false);
    systemUserClearPass.setDisable(enable == false);
  }

  @Override
  public void toModel(GWikiSystemUserConfigModel modelObject)
  {
    modelObject.setSystemUserEnabled(sysUserEnabled.isSelected());
    if (sysUserEnabled.isSelected() == true) {
      modelObject.setSystemUserName(systemUserName.getText());
      modelObject.setSystemUserClearPass(systemUserClearPass.getText());
    }

  }

  @Override
  public void fromModel(GWikiSystemUserConfigModel modelObject)
  {
    enableSystemUser(modelObject.isSystemUserEnabled());
    systemUserName.setText(modelObject.getSystemUserName());
    if (StringUtils.isNotBlank(modelObject.getSystemUserEncPass()) == true) {
      systemUserClearPass.setText("");
    }

  }

  @Override
  public String getTabTitle()
  {
    return "System User";
  }

}
