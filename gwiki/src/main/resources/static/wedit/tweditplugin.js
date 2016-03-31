/**
 * 
 * @author Roger Kommer
 */

(function() {
	var each = tinymce.each;

	tinymce.create('tinymce.plugins.GWikiPlugin', {
	  init : function(ed, url) {
		  var t = this;
		  t.editor = ed;
		  var ed = ed;
		  // Register commands
		  ed.addCommand('mceGWikiInsertLink', t._insertLink, t);
		  ed.addCommand('mceGWikiInsertImage', t._insertImage, t);
		  // ed.addCommand('mceGWikiInsertChoosenLink', t._insertChoosenLink, t);
		  ed.addCommand('mceGWikiSaveAction', t._saveAction, t);
		  ed.addCommand('mceGWikiCancelAction', t._cancelAction, t);

		  // Register buttons
		  // ed.addButton('wikilink', {
		  // title : "gwiki.editor.tinymce.gwiki.wikilink.title".i18n(),
		  // cmd : 'mceGWikiInsertLink',
		  // 'class' : 'mce-ico mce-i-link',
		  // image : '../../static/wedit/img/iconlink.png'
		  // });
		  /**
			 * "\e014" image "\e017": <> e019: auge
			 */
		  // ed.addButton('wikichoosenlink', {
		  // title : "gwiki.editor.tinymce.gwiki.wikilink.title".i18n(),
		  // cmd : 'mceGWikiInsertChoosenLink',
		  // icon : 'wikichoosenlink',
		  // image : '../../static/wedit/img/iconlink.png'
		  // // text: "%"
		  // // image: '../../static/wedit/img/image.png'
		  // });
		  // ed.addButton('wikisaveaction', {
		  // title : "gwiki.editor.button.tooltip.save".i18n(),
		  // shortcut : 'Meta+S',
		  // text : 'S',
		  // cmd : 'mceGWikiSaveAction',
		  // 'class' : 'mce-ico mce-i-save'
		  // });
		  // ed.addButton('wikicancelaction', {
		  // title : "gwiki.editor.button.tooltip.cancel".i18n(),
		  // cmd : 'mceGWikiCancelAction',
		  // 'class' : 'mceIcon mce_cancel',
		  // text : 'C'
		  // });
		  ed.addButton('wikisave', {
		    title : "Save and close editor (Ctrl+S)",
		    text : 'Save',
		    shortcut : 'Ctrl+S',
		    onClick : function() {
			    t._saveAction();
		    }
		  });
		  ed.addButton('wikicancel', {
		    title : "Cancel",
		    text : 'Cancel',
		    // shortcut : 'Ctrl+S',
		    onClick : function() {
		    	t._cancelAction();
		    }
		  });
		  var macroButton;
		  ed.addButton('wikinewmacro', {
		    title : "Insert/Edit Macro (Alt+M)",
		    text : '{M}',
		    shortcut : 'Alt+Meta+M',
		    onClick : function() {
			    t._insertMacro(t.editor)
		    },
		    onPostRender : function() {
			    macroButton = this;
		    },
		  });
		  var linkButton;
		  ed.addButton('wikilink', {
		    title : "Insert/Edit Link (Alt+L)",
		    onClick : function() {
			    t._insertLink(t.editor)
		    },
		    text : '[L]',
		    onPostRender : function() {
			    linkButton = this;
		    }
		  });
		  var imageButton;
		  ed.addButton('imagelink', {
		    title : "Insert Image (Alt+I)",
		    onClick : function() {
			    t._insertImage(t.editor)
		    },
		    text : '!I!',
		    onPostRender : function() {
			    imageButton = this;
		    }
		  });
		  var attachmentButton;
		  ed.addButton('attachmentlink', {
		    title : "Insert Attachment",
		    onClick : function() {
			    t._insertAttachment(t.editor)
		    },
		    text : '[A]',
		    onPostRender : function() {
			    attachmentButton = this;
		    }
		  });
		  ed.on('init', function() {
			  ed.addShortcut('Alt+M', 'Alt+M', function() {
				  t._insertMacro(t.editor);
			  });
			  ed.addShortcut('Alt+I', 'Alt+I', function() {
				  t._insertImage(t.editor);
			  });
			  ed.addShortcut('Alt+L', 'Alt+L', function() {
				  t._insertLink(t.editor);
			  });
			  ed.addShortcut('Ctrl+S', 'Ctrl+S', function() {
				  t._saveAction(t.editor);
			  });
		  });
		  ed.on('NodeChange', function(event) {
			  if (macroButton) {
				  macroButton.disabled(!twedit_is_macro_button_enabled(ed));
			  }
			  var linkenabled = twedit_is_link_button_enabled(ed);
			  if (linkButton) {
				  linkButton.disabled(!linkenabled);
			  }
			  if (attachmentButton) {
				  attachmentButton.disabled(!linkenabled);
			  }
			  if (imageButton) {
				  imageButton.disabled(!twedit_is_image_button_enabled(ed));
			  }
		  });

		  // for toolbar

		  ed.addButton('wikieditmacro', {
		    title : "Edit Macro",
		    image : '../../static/wedit/img/iconlink.png',
		    onClick : function() {
			    t._editMacro(t.editor)
		    },
		    text : 'Edit Macro'
		  });
		  ed.addButton('wikidelmacro', {
		    title : "Delete Macro",
		    onClick : function() {
			    t._deleteMacro(t.editor)
		    },
		    text : 'Delete'
		  });
		  ed.addContextToolbar(this._isMacro, "wikieditmacro wikidelmacro");

	  },
	  createControl : function(n, cm) {
		  var t = this;
		  switch (n) {
		  case 'wikiimage':
			  var c = cm.createSplitButton('wikiimage', {
			    title : "gwiki.editor.tinymce.gwiki.wikiimage.title".i18n(),
			    image : gwikiContextPath + gwikiServletPath + '/static/tiny_mce/plugins/gwiki/img/image.png',
			    'class' : 'mceIcon mce_image'
			  });
			  c.onRenderMenu.add(function(c, m) {
				  m.add({
				    title : "gwiki.editor.tinymce.gwiki.wikiimage.title".i18n(),
				    'class' : 'mceIcon mce_image'
				  }).setDisabled(1);
				  m.add({
				    title : "gwiki.editor.tinymce.gwiki.wikiimage.new".i18n(),
				    onclick : function() {
					    t._insertScreenshot();
				    }
				  });
				  m.add({
				    title : "gwiki.editor.tinymce.gwiki.wikiimage.existing".i18n(),
				    onclick : function() {
					    t._insertImage();
				    }
				  });
			  });
			  return c;
		  }
		  return null;
	  },
	  getInfo : function() {
		  return {
		    longname : 'GWiki RTE plugin',
		    author : 'Micromata GmbH, Roger Kommer',
		    authorurl : 'http://www.micromata.com',
		    // infourl :
		    // 'http://wiki.micromata.com/index.php/TinyMCE:Plugins/template',
		    version : tinymce.majorVersion + "." + tinymce.minorVersion
		  };
	  },
	  _editMacro : function(ed) {
		  wedit_hide_contextToollBar(ed);
		  var elm = ed.selection.getNode();
		  wedit_show_editmacro_dialog(ed, elm);
	  },
	  _insertMacro : function(ed) {
		  wedit_hide_contextToollBar(ed);
		  wedit_show_newmacro_dialog(ed);
	  },
	  _deleteMacro : function(ed) {
		  wedit_hide_contextToollBar(ed);
		  var elm = ed.selection.getNode();
		  wedit_macro_delete_current(ed, elm);
	  },
	  _insertLink : function(ed) {
		  wedit_hide_contextToollBar(ed);
		  wedit_show_link_dialog(ed);
	  },
	  _insertImage : function(ed) {
		  wedit_hide_contextToollBar(ed);
		  wedit_show_image_dialog(ed);
	  },
	  _insertAttachment : function(ed) {
		  wedit_hide_contextToollBar(ed);
		  wedit_show_attachment_dialog(ed);
	  },
	  _saveAction : function(ui, v) {
		  $("#gwikieditsavebutton").click();
	  },
	  _cancelAction : function(ui, v) {
		  $("#gwikieditcancelbutton").click();
	  },
	  _isLink : function(el) {
		  var found = el.tagName.toLowerCase() == 'a';
		  return found;
	  },
	  _isMacro : function(el) {
		  var eln = el.tagName.toLowerCase();
		  if (eln != 'div' && eln != 'span') {
			  return false;
		  }
		  var cls = el.getAttribute('class');
		  if (cls != null && cls.indexOf('weditmacrohead') != -1) {
			  return true;
		  }
		  return false;
	  }
	});

	// Register plugin
	tinymce.PluginManager.add('gwiki', tinymce.plugins.GWikiPlugin);
})();