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
if (typeof gwikiEditSmiles === 'undefined') {
	gwikiEditSmiles = [ {
	  shortCut : ';)',
	  shortName : 'wink',
	  source : '/static/tinymce/plugins/emoticons/img/smiley-wink.gif'
	} ];
}

tinymce.PluginManager
    .add(
        'smileys',
        function(editor, url) {
	        var emoticons = gwikiEditSmiles;
	        var icons = [ [ "plus-button" ] ];
	        function getHtml() {
		        var emoticonsHtml;
		        var colCount = emoticons.length * 4 / 3;
		        emoticonsHtml = '<table role="list" class="mce-grid">';

		        for (var i = 0; i < emoticons.length; ++i) {
			        emoticonsHtml += '<tr>';
			        var cc = 0;
			        for (; i < emoticons.length && cc < colCount; ++i) {
				        var em = emoticons[i];
				        var url = em.source;
				        var alt = em.shortName;
				        var shortName = em.shortName;
				        if (em.shortCut) {
					        alt += "(" + em.shortCut + ")";
				        }
				        emoticonsHtml += "<td><span class='mceNonEditable' style='display: inline-block; height:18px; width:18px; background-image: url("
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
	                'smileys',
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
