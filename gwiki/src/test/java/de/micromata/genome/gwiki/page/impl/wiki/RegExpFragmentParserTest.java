////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////


// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

////////////////////////////////////////////////////////////////////////////


// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

////////////////////////////////////////////////////////////////////////////


package de.micromata.genome.gwiki.page.impl.wiki;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.uwyn.jhighlight.tools.StringUtils;

import junit.framework.TestCase;

public class RegExpFragmentParserTest extends TestCase
{
  String getSouroundPattern(String word)
  {
    String dec = Pattern.quote(word);
    String altw = "_-+~^*";
    altw = StringUtils.replace(altw, word, "");
    altw = Pattern.quote(altw);
    String matchWord = "[\\w\\d" + altw + "]";
    
    String matchWordSpacesEndsWithWord = "(?:[^\\n" + dec + "]*" + matchWord + ")";
    String rex = "(" + dec + ")(" + matchWord + matchWordSpacesEndsWithWord + ")(" + dec + ")(.*)";

    System.out.println("reg: " + rex);
    return rex;
  }

  String getLineSuroundPattern(String word)
  {
    String dec = Pattern.quote(word);
    String rex = "\n(" + dec + ")[ \\t\\n](.+)($)(.*)";
    return rex;

  }

  public void rxTrue(String regexp, String text)
  {
    // RegExpDecoratorReplacer repl = new RegExpDecoratorReplacer(regexp);
    Pattern p = Pattern.compile(regexp, Pattern.DOTALL + Pattern.MULTILINE);
    Matcher m = p.matcher(text);
    boolean found = m.find();

    assertTrue("Pattern: " + regexp + "does not match: " + text, found);

  }

  public void rxFalse(String regexp, String text)
  {
    Pattern p = Pattern.compile(regexp, Pattern.DOTALL + Pattern.MULTILINE);
    Matcher m = p.matcher(text);
    boolean found = m.find();

    assertFalse("Pattern: " + regexp + "should not match: " + text, found);
  }
  public void NotestEndOfInput()
  {
    Pattern p = Pattern.compile("[\\d]+[A$]?", Pattern.DOTALL + Pattern.MULTILINE);
    Matcher m = p.matcher("1234A");
    assertTrue(m.find());
    m = p.matcher("1234");
    assertTrue(m.find());
    m = p.matcher("1234X");
    assertFalse(m.find());
    
  }

  public void NotestWordSuroundDecorator()
  {
    String rex = getSouroundPattern("-");
    // bekomme ich nicht hin rxFalse(rex, "-sd fl-asdf\n");
    rxTrue(rex, "-hehllo-\n");
    rxFalse(rex, "- Hello");
    rxTrue(rex, "-Hello-");
    rxTrue(rex, "-*Hello*-");
    rxTrue(rex, "-Hello Leute-");
    rxFalse(rex, "-Hello\nLeute-");
    rxFalse(rex, "- HelloLeute -");
    rxFalse(rex, "-HelloLeute -");
    rxFalse(rex, "- HelloLeute- sdfs");
    rxTrue(rex, "-HelloLeute- und -Folks-");
    rxTrue(rex, "x -Hello Leute- x\ndfdf");
    
  }

  public void testLineSuroundDecorator()
  {
    String rex = getLineSuroundPattern("-");
    rxTrue(rex, "\n- hehllo\n");
    rxTrue(rex, "\n- hehllo-\n");
    rxFalse(rex, "- hehllo");
    rxFalse(rex, " - hehllo");
    rxFalse(rex, "-hehllo");
    rxFalse(rex, "\nsdf - hehllo\n");
    rxFalse(rex, "\nx - hehllo");
    rxFalse(rex, "- Software nebst Inhalten auf den lokalen Rechner laden.\n");
  }

  
  public void testNewLineDecorator()
  {
    String dec = "\\*";
    String rex = "\n(" + dec + "\\s?)(.*?)(\\n.*)";
    rxTrue(rex, "\n* Hallo\n");
    rxTrue(rex, "\n*  Hallo\n");
    rxTrue(rex, "\n* Hallo \n");
  }
}
