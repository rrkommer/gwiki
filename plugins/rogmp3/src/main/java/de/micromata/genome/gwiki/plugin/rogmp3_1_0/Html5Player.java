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

package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

import de.micromata.genome.gwiki.page.GWikiContext;

public class Html5Player
{
  public static void renderAudioPlayer(GWikiContext wikiContext)
  {
    String adio = " <audio src=\"./xxx.mp3\" type=\"audio/mpeg\"  id=\"player\" controls preload=\"none\">\r\n"
        + "  </audio><br/>\n"
        + "Aktuell: <p id=\"curplay\">curplay</p>";
    wikiContext.append(adio);

  }

  public static void renderTrackLink(GWikiContext wikiContext, String url, String text)
  {
    // TODO Auto-generated method stub
    String liline = "<li data-src='" + url + "' onclick=\"playPlayTrac($(this))\">" + text + "</li>\n";
    wikiContext.append(liline);
  }

  private static String localUrl(GWikiContext wikiContext)
  {
    return wikiContext.localUrl(wikiContext.getCurrentElement().getElementInfo().getId());
  }

  public static void renderAudioJs(GWikiContext wikiContext, String mediaPk, String tit, String trackPk)
  {

    String js = " <script type=\"text/JavaScript\">\r\n"
        + "    function setPlayTrac(trackli)\r\n"
        + "  {\r\n"
        + "    var aud = document.getElementById(\"player\");\r\n"
        + "    var firstds = trackli.attr('data-src');\r\n"
        + "    //alert(\"Data source is:\" + firstds);\r\n"
        + "    trackli.siblings().removeClass('playing');\r\n"
        + "    trackli.addClass('playing');\r\n"
        + "    aud.setAttribute(\"src\", firstds);\r\n"
        + "    var text = trackli.html();\r\n"
        + "    document.getElementById(\"curplay\").innerHTML = text;\r\n"
        + "    aud.load();\r\n"
        + "  } \r\n"
        + "  function playPlayTrac(trackli)\r\n"
        + "  {\r\n"
        + "    setPlayTrac(trackli);\r\n"
        + "    document.getElementById(\"player\").play();\r\n"
        + "  }\r\n"
        + "  \r\n"
        + "$(document).ready(function() {\r\n"

        + " var titplayer = document.getElementById('titlplayer');\n"
        + " if (titplayer) {"
        + "  titplayer.onended = function() {\r\n"
        + "     /* alert('upuse'); */\n"
        + "     var xmlhttp = new XMLHttpRequest();\r\n"
        + "     xmlhttp.open(\"GET\", \""
        + localUrl(wikiContext);
    if (tit != null) {
      js += "?upUse=true&tit=" + tit;
    } else
      if (mediaPk != null) {
      js += "?upUse=true&mediaPk=" + mediaPk;
    }

    js += "\", true);\r\n"
        + "    xmlhttp.send();\n"
        + "  };\n"
        + "}\n"
        + "\n"
        + "  var aud = document.getElementById(\"player\");\r\n"
        + "  function play(element)\r\n"
        + "  {\r\n"
        + "     var source = aud.getAttribute(\"data-src\");\r\n"
        + "     aud.setAttribute(\"src\", source);\r\n"
        + "     aud.play();\r\n"
        + "  }\r\n"
        + "  \r\n"
        + "  function setNextPlayTrac()\r\n"
        + "  {\r\n"
        + "    var next = $('ol li.playing').next();\r\n"
        + "    if (next.length) { \r\n"
        + "      setPlayTrac(next);\r\n"
        + "      aud.play();\r\n"
        + "    }\r\n"
        + "  }\r\n"
        + "    \r\n"
        + "    var firstol = $('ol li').first();\r\n"
        + "    setPlayTrac(firstol);\r\n"
        + "  \r\n"
        + "    aud.onended = function() {\r\n"
        + "      setNextPlayTrac();\r\n"
        + "    };\r\n"
        + "});\r\n"
        + "    </script>";
    wikiContext.addHeaderContent(js);

  }

  private static void renderAudioJsOld(GWikiContext wikiContext)
  {
    String adio = "<script type=\"text/javascript\" src=\"" + wikiContext.localUrl("inc/rogmp3/audiojs/audio.min.js") + "\"></script>\n";
    wikiContext.addHeaderContent(adio);
  }

  private void renderaudioJs(GWikiContext wikiContext)
  {
    String code = "<script>\r\n"
        + "      $(function() { \r\n"
        + "        // Setup the player to autoplay the next track\r\n"
        + "        var a = audiojs.createAll({\r\n"
        + "          trackEnded: function() {\r\n"
        + "            var next = $('ol li.playing').next();\r\n"
        + "            if (next.length) { \n"
        + "            next.addClass('playing').siblings().removeClass('playing');\r\n"
        + "            audio.load($('a', next).attr('data-src'));\r\n"
        + "            audio.play();\r\n"
        + "            }\r\n"
        + "          }\r\n"
        + "        });\r\n"
        + "        \r\n"
        + "        // Load in the first track\r\n"
        + "        var audio = a[0];\r\n"
        + "            first = $('ol a').attr('data-src');\r\n"
        + "        $('ol li').first().addClass('playing');\r\n"
        + "        audio.load(first);\r\n"
        + "\r\n"
        + "        // Load in a track on click\r\n"
        + "        $('ol li').click(function(e) {\r\n"
        + "          e.preventDefault();\r\n"
        + "          $(this).addClass('playing').siblings().removeClass('playing');\r\n"
        + "          audio.load($('a', this).attr('data-src'));\r\n"
        + "          audio.play();\r\n"
        + "        });\r\n"
        + "        // Keyboard shortcuts\r\n"
        + "        $(document).keydown(function(e) {\r\n"
        + "          var unicode = e.charCode ? e.charCode : e.keyCode;\r\n"
        + "             // right arrow\r\n"
        + "          if (unicode == 39) {\r\n"
        + "            var next = $('li.playing').next();\r\n"
        + "            if (!next.length) next = $('ol li').first();\r\n"
        + "            next.click();\r\n"
        + "            // back arrow\r\n"
        + "          } else if (unicode == 37) {\r\n"
        + "            var prev = $('li.playing').prev();\r\n"
        + "            if (!prev.length) prev = $('ol li').last();\r\n"
        + "            prev.click();\r\n"
        + "            // spacebar\r\n"
        + "          } else if (unicode == 32) {\r\n"
        + "            audio.playPause();\r\n"
        + "          }\r\n"
        + "        })\r\n"
        + "      });\r\n"
        + "    </script>";
    wikiContext.append(code);
    wikiContext.append("<audio></audio>"); // preload

  }

}
