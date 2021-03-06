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

package de.micromata.genome.gwiki.plugin.msotextextractor_1_0;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import de.micromata.genome.gwiki.page.attachments.TextExtractor;
import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * Extracts text from a excel sheet.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class ExcelTextExtractor implements TextExtractor
{

  public String extractText(String fileName, InputStream data)
  {
    try {
      HSSFWorkbook wb = new HSSFWorkbook(data);
      ExcelExtractor extr = new ExcelExtractor(wb);
      String text = extr.getText();
      text = WordTextExtractor.reworkWordText(text);
      return text;
    } catch (IOException ex) {
      throw new RuntimeIOException("Failure to extract word from " + fileName + "; " + ex.getMessage(), ex);
    }
  }

}
