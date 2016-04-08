/**
 * plugin.js
 * 
 * Released under LGPL License. Copyright (c) 1999-2015 Ephox Corp. All rights
 * reserved
 * 
 * License: http://www.tinymce.com/license Contributing:
 * http://www.tinymce.com/contributing
 */

/* global tinymce:true */

function SmileyInfo() {
	this.shortCut = null;
	this.shortName = null;
	this.source = null;
};

tinymce.PluginManager
    .add(
        'smileys',
        function(editor, url) {
	        var emoticons = gwikiEditSmiles;
	        var gwikiEditSmiles = [];

	        var url = gwedit_buildUrl("edit/WeditService");

	        $.ajax(url, {
	          data : {
		          method_onGetSmileys : 'true'
	          },
	          dataType : "json",
	          global : false,
	          success : function(data) {
		          gwikiEditSmiles = data;
//		          console.debug('got smilies: ' + data);
	          },
	          fail : function(jqXHR, textStatus, errorThrown) {
		          console.error("got json error: " + textStatus);
	          }
	        });

	        function getHtml() {
		        var emoticonsHtml;
		        var emoticons = gwikiEditSmiles;
		        var colCount = 0;
		        if (emoticons.length > 0) {
			        colCount = Math.sqrt(emoticons.length);
		        }
		        emoticonsHtml = '<table role="list" class="mce-grid">';

		        for (var i = 0; i < emoticons.length;) {
			        emoticonsHtml += '<tr>';
			        var cc = 0;
			        for (; i < emoticons.length && cc < colCount; ++i) {
			        	++cc;
				        var em = emoticons[i];
				        var url = em.source;
				        var alt = em.shortName;
				        var shortName = em.shortName;
				        if (em.shortCut) {
					        alt += " (" + em.shortCut + ")";
				        }
				        
				        emoticonsHtml += "<td><span class='mceNonEditable' title='" + alt + "' style='display: inline-block; height:18px; width:18px; background-image: url("
				            + url
				            + ")' data-wiki-smiley='"
				            + shortName
				            + "' data-wiki-src='"
				            + url
				            + "' data-wiki-alt='"
				            + alt
				            + "'>&nbsp;</span></td>";

			        }
			        emoticonsHtml += '</tr>';
		        }

		        emoticonsHtml += '</table>';
		        return emoticonsHtml;
	        }

	        editor
	            .addButton(
	                'emoticons',
	                {
	                  type : 'panelbutton',
	                  panel : {
	                    role : 'application',
	                    autohide : true,
	                    html : getHtml,
	                    onclick : function(e) {
		                    var linkElm = editor.dom.getParent(e.target, 'span');
		                    if (linkElm) {
			                    var url = linkElm.getAttribute('data-wiki-src');
			                    var alt = linkElm.getAttribute('data-wiki-alt');
			                    var shortName = linkElm.getAttribute('data-wiki-smiley');

			                    editor
			                        .insertContent("<span class='mceNonEditable' style='display: inline-block; height:18px; width:18px; background-image: url("
			                            + url
			                            + ")' data-wiki-smiley='"
			                            + shortName
			                            + "' data-wiki-src='"
			                            + url
			                            + "' data-wiki-alt='" + alt + "'>&nbsp;</span>");

			                    this.hide();
		                    }
	                    }
	                  },
	                  tooltip : 'Smileys'
	                });
        });
