//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.launcher.config;

import org.apache.commons.lang3.StringUtils;

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
  public void initializeWithModel()
  {
    fromModel();
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
  public void toModel()
  {
    model.setSystemUserEnabled(sysUserEnabled.isSelected());
    if (sysUserEnabled.isSelected() == true) {
      model.setSystemUserName(systemUserName.getText());
      model.setSystemUserClearPass(systemUserClearPass.getText());
    }

  }

  @Override
  public void fromModel()
  {
    enableSystemUser(model.isSystemUserEnabled());
    systemUserName.setText(model.getSystemUserName());
    if (StringUtils.isNotBlank(model.getSystemUserEncPass()) == true) {
      systemUserClearPass.setText("");
    }

  }

  @Override
  public String getTabTitle()
  {
    return "System User";
  }

}
