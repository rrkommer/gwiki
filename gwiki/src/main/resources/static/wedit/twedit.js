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
  start : '# ',
  cmd : 'InsertOrderedList'
}, {
  start : '- ',
  cmd : 'InsertUnorderedList'
} ];

var twedit_tinyMenu = {
  edit: { title: 'Edit', items: 'undo redo  | cut copy paste selectall | searchreplace' },
  insert: { title: 'Insert', items: 'link charmap' },
  format: { title: 'Format', items: 'bold italic underline strikethrough superscript subscript | removeformat' },
  table: { title: 'Table', items: 'inserttable tableprops deletetable | cell row column' }
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
	      auto_focus: editId,
	      language : "locale".i18n(),
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
//		      ed.on('change', function(e) {
//			      // console.log('the event object ' + e);
//			      // console.log('the editor object ' + ed);
//			      // console.log('the content ' + ed.getContent());
//		      });
		      ed.on('paste', function(e) {
			      twedit_paste(this, e);
		      });
		      
		      ed.on('keydown', function(event) {
			      if (event.which == 83 && event.ctrlKey == true) { // CTRL+S
				      onSaveOptRedit(event, false);
				      event.stopPropagation();
				      event.preventDefault();
			      }
		      }, true);
		      twedit_bind_native_paste(ed, '#' + editId);
	      },

	      theme : 'modern',
//	      forced_root_block: false, // 'p', // br instead of p
	      keep_styles: false, // otherwise h1. will not terminated. ! does not working!

	      plugins : 'gwiki visualblocks tweditac noneditable paste textpattern fullscreen searchreplace contextmenu  table textcolor colorpicker', //
	      paste_data_images : true,
	      menu: false, //twedit_tinyMenu,
	      menubar: false,
	      // menubar : "cut copy paste | undo redo | styleselect | bold italic |
				// bullist
	      // numlist",
	      toolbar : "fullscreen | cut copy paste| undo redo | searchreplace | wikilink imagelink attachmentlink  wikinewmacro | styleselect bold italic | bullist numlist | table forecolor backcolor attribs code",
	      table_toolbar : "tableprops tabledelete | tableinsertrowbefore tableinsertrowafter tabledeleterow | tableinsertcolbefore tableinsertcolafter tabledeletecol",
	      // toolbar1 : 'insertfile undo redo | styleselect | bold italic |
	      // alignleft
	      // aligncenter alignright alignjustify | bullist numlist outdent indent
	      // |
	      // link image',
	      // toolbar2 : 'print preview media | forecolor backcolor emoticons',
	      // image_advtab : true,
	      /*
				 * templates : [ { title : 'Test template 1', content : 'Test 1' }, {
				 * title : 'Test template 2', content : 'Test 2' } ],
				 */
	      browser_spellcheck: true,
	      image_advtab : true,
	      content_css : gwikiContentCssArray
	    });
	return editId;
}

function twedit_setContent(partName, html)
{
	var editId = 'gwikihtmledit' + partName;
	tinymce.get(editId).setContent(html);
}



function wedit_cleanuphtml(content) {
	
	//will not work on text like: h1. bla<br/>blub.
	// jquery-1.12.1.js:1502 Uncaught Error: Syntax error, unrecognized expression: h1. Titel<br />asdf
//	var cnode = $(content); 
//	var attr = cnode.attr("class");
//	if (attr && attr.indexOf("gwikiContent") != -1) {
//		content = cnode.html();
//	}
//	} 
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