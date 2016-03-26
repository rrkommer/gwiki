var wiki_textpattern_patterns = [ {
  start : '_',
  end : '_',
  format : 'italic'
}, {
  start : '*',
  end : '*',
  format : 'bold'
}, {
  start : 'h1. ',
  format : 'h1'
}, {
  start : 'h2. ',
  format : 'h2'
}, {
  start : 'h3. ',
  format : 'h3'
}, {
  start : 'h4. ',
  format : 'h4'
}, {
  start : 'h5. ',
  format : 'h5'
}, {
  start : 'h6. ',
  format : 'h6'
}, {
  start : '{{',
  end : "}}",
  format : 'code'
}, {
  start : '# ',
  cmd : 'InsertOrderedList'
}, {
  start : '- ',
  cmd : 'InsertUnorderedList'
} ];

var twedit_style_formats = [ {
  title : 'Bold text',
  inline : 'b'
}, {
  title : 'Italic',
  inline : 'i'
}, {
  title : 'Underlined',
  inline : 'u'
}, {
  title : 'Strike throug',
  inline : 'del'
}, {
  title : 'Superscript',
  inline : 'sup'
}, {
  title : 'Subscript',
  inline : 'sub'

}, {
  title : 'Code',
  inline : 'code'
} ];

var twedit_formats = {
  alignleft : {
    selector : 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img',
    classes : 'left'
  },
  aligncenter : {
    selector : 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img',
    classes : 'center'
  },
  alignright : {
    selector : 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img',
    classes : 'right'
  },
  alignfull : {
    selector : 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img',
    classes : 'full'
  },
  strikethrough : {
	  inline : 'del'
  },
  forecolor : {
    inline : 'span',
    classes : 'forecolor',
    styles : {
	    color : '%value'
    }
  },
  hilitecolor : {
    inline : 'span',
    classes : 'hilitecolor',
    styles : {
	    backgroundColor : '%value'
    }
  }
};

var twedit_tinyMenu = {
  edit : {
    title : 'Edit',
    items : 'undo redo  | cut copy paste selectall | searchreplace'
  },
  insert : {
    title : 'Insert',
    items : 'link charmap'
  },
  format : {
    title : 'Format',
    items : 'bold italic underline strikethrough superscript subscript | removeformat'
  },
  table : {
    title : 'Table',
    items : 'inserttable tableprops deletetable | cell row column'
  }
};

var twedit_editors = {

};

function twedit_create(editId, content) {

	var ed = tinymce
	    .init({
	      homeUrl : gwikiHomeUrl,
	      selector : '#' + editId,
	      height : 500,
	      tweidac_checkac_start : [ '!', '[', '{' ],
	      visualblocks_default_state : false,
	      end_container_on_empty_block : true,
	      cache_suffix : '?' + Date.now(),
	      paste_data_images : true,
	      paste_preprocess : twedit_preprocess,
	      paste_postprocess : twedit_postprocess,
	      textpattern_patterns : wiki_textpattern_patterns,
	      auto_focus : editId,
	      language : "locale".i18n(),
	      theme : 'modern',
	      keep_styles : false,

	      plugins : ['gwiki tweditac',
	                 'visualblocks noneditable paste textpattern fullscreen searchreplace contextmenu table textcolor colorpicker',
	                 'hr anchor'], //
	      paste_data_images : true,
	      menu : false, // twedit_tinyMenu,
	      menubar : false,
	      toolbar1 : "wikisave wikicancel | fullscreen | cut copy paste | undo redo | searchreplace | wikilink imagelink attachmentlink  wikinewmacro | removeformat bold italic | bullist numlist outdent indent ",
	      toolbar2 : "formatselect styleselect |  alignleft aligncenter alignright alignjustify | fontselect fontsizeselect forecolor backcolor | hr charmap |  table ",
	      table_toolbar : "tableprops tabledelete | tableinsertrowbefore tableinsertrowafter tabledeleterow | tableinsertcolbefore tableinsertcolafter tabledeletecol",
	      browser_spellcheck : true,
	      image_advtab : true,
	      content_css : gwikiContentCssArray,
	      block_formats : 'Paragraph=p;Header 1=h1;Header 2=h2;Header 3=h3;Header 4=h4;Header 5=h5;Quote=blockquote;Preformatted=pre',
	      style_formats : twedit_style_formats,
	      init_instance_callback : function(editor) {
		      editor.contentWindow.document.addEventListener('keydown', function(event) {
			      if (event.keyCode == 9) {
				      tedit_insert_into_text(editor, "\t");
				      event.stopPropagation();
				      event.preventDefault();
			      }
		      }, true);
	      },
	      setup : function(ed) {
		      twedit_editors[editId] = ed;
		      // ed.on('change', function(e) {
		      // // console.log('the event object ' + e);
		      // // console.log('the editor object ' + ed);
		      // // console.log('the content ' + ed.getContent());
		      // });
		      ed.on('paste', function(e) {
			      twedit_paste(this, e);
		      });
		      ed.on('FullscreenStateChanged', function(e) {
			      var nstate = e.state;
			      gwikiSetCookie('wikirtefullscreen', "" + nstate, 30);
		      });
		      var fullscreenstate = gwikiGetCookie('wikirtefullscreen');
		      if (fullscreenstate == 'true') {
			      var localed = ed;
			      setTimeout(function() {
				      localed.execCommand('mceFullScreen', true);
				      //				      
			      }, 200);
		      }
		      ed.on('keydown', function(event) {
			      // all keys because overwerite
			      if (twedit_check_valid_range_for_del(ed) == false) {
				      event.stopPropagation();
				      event.preventDefault();
				      return;
			      }
			      // }
			      if (event.ctrlKey == true) {
				      if (event.which == 83) { // CTRL+S
					      onSaveOptRedit(event, false);
					      event.stopPropagation();
					      event.preventDefault();
				      }
				      // not working
				      // else if (event.which == 65) { // A
				      // wedit_tinyCommand_beginLine(ed, event);
				      // } else if (event.which == 68) { // D
				      // wedit_tinyCommand_delete(ed, event);
				      // } else if (event.which == 69) { // E
				      // wedit_tinyCommand_endLine(ed, event);
				      // }
			      } else if (event.altKey == true) { // ALT
				      // if (event.which == 77) { // M
				      // if (twedit_is_macro_button_enabled(ed) == true) {
				      // wedit_show_newmacro_dialog(ed);
				      // }
				      // } else if (event.which == 76) { // L
				      // if (twedit_is_link_button_enabled(ed) == true) {
				      // wedit_show_link_dialog(ed);
				      // }
				      // }
			      }
		      }, true);
		      twedit_bind_native_paste(ed, '#' + editId);
	      }

	    });
	return editId;
}

function twedit_setContent(partName, html) {
	var editId = 'gwikihtmledit' + partName;
	tinymce.get(editId).setContent(html);
}

function wedit_cleanuphtml(content) {

	// will not work on text like: h1. bla<br/>blub.
	// jquery-1.12.1.js:1502 Uncaught Error: Syntax error, unrecognized
	// expression: h1. Titel<br />asdf
	// var cnode = $(content);
	// var attr = cnode.attr("class");
	// if (attr && attr.indexOf("gwikiContent") != -1) {
	// content = cnode.html();
	// }
	// }
	return content;
}

function wedit_getCursorCoords(range) {
	var x = 0, y = 0;

	var startc = range.startContainer;
	// a trick to get coordinates.
	var span = document.createElement("span");
	span.appendChild(document.createTextNode("\u200b") /*
																											 * Zero-width space
																											 * character
																											 */);
	range.insertNode(span);

	var rect = span.getBoundingClientRect();
	span.parentNode.removeChild(span);

	y = rect.top;
	x = rect.left;
	return {
	  x : x,
	  y : y
	};
}

// Prevent jQuery UI dialog from blocking focusin
$(document).on('focusin', function(e) {
	if ($(e.target).closest(".mce-window, .moxman-window").length) {
		e.stopImmediatePropagation();
	}
});