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
		  // Register commands
		  ed.addCommand('mceGWikiInsertLink', t._insertLink, t);
		  ed.addCommand('mceGWikiInsertImage', t._insertImage, t);
		  ed.addCommand('mceGWikiInsertScreenshot', t._insertScreenshot, t);
		  ed.addCommand('mceGWikiInsertChoosenLink', t._insertChoosenLink, t);
		  ed.addCommand('mceGWikiSaveAction', t._saveAction, t);
		  ed.addCommand('mceGWikiCancelAction', t._cancelAction, t);

		  // Register buttons
		  ed.addButton('wikilink', {
		    title : "gwiki.editor.tinymce.gwiki.wikilink.title".i18n(),
		    cmd : 'mceGWikiInsertLink',
		    'class' : 'mce-ico mce-i-link',
		    image : '../../static/wedit/img/iconlink.png'
		  });
		  /**
			 * "\e014" image "\e017": <> e019: auge
			 */
		  ed.addButton('wikichoosenlink', {
		    title : "gwiki.editor.tinymce.gwiki.wikilink.title".i18n(),
		    cmd : 'mceGWikiInsertChoosenLink',
		    icon : 'wikichoosenlink',
		    image : '../../static/wedit/img/iconlink.png'
		  // text: "%"
		  // image: '../../static/wedit/img/image.png'
		  });
		  ed.addButton('wikisaveaction', {
		    title : "gwiki.editor.button.tooltip.save".i18n(),
		    shortcut : 'Meta+S',
		    text : 'S',
		    cmd : 'mceGWikiSaveAction',
		    'class' : 'mce-ico mce-i-save'
		  });
		  ed.addButton('wikicancelaction', {
		    title : "gwiki.editor.button.tooltip.cancel".i18n(),
		    cmd : 'mceGWikiCancelAction',
		    'class' : 'mceIcon mce_cancel',
		    text : 'C'
		  });
		  ed.addButton('wikieditmacro', {
		    title : "Edit Macro",
		    onClick : function() {
			    t._editMacro(t.editor)
		    },
		    text : 'Edit Macro'
		  });
		  ed.addButton('wikinewmacro', {
		    title : "Insert Macro",
		    onClick : function() {
			    t._insertMacro(t.editor)
		    },
		    text : '{M}'
		  });
		  ed.addButton('wikidelmacro', {
		    title : "Delete Macro",
		    onClick : function() {
			    t._deleteMacro(t.editor)
		    },
		    text : 'Delete'
		  });
		  var linktoolbaritems = 'wikichoosenlink';

		  ed.addContextToolbar(this._isLink, linktoolbaritems);

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
	  _insertLink : function(ui, v) {
		  var inst = this.editor;
		  var elm = inst.selection.getNode();

		  elm = inst.dom.getParent(elm, "A");
		  var action = "insert";
		  var href = '';
		  var title = '';
		  if (elm != null && elm.nodeName == "A") {
			  action = "update";
			  href = inst.dom.getAttrib(elm, 'href');
			  title = inst.dom.getAttrib(elm, 'title');
		  } else {
		  }
		  if (href.match("^" + gwikiContextPath) == gwikiContextPath) {
			  href = href.substring(gwikiContextPath.length + 1);
		  }
		  // TODO replace
		  gwikiEditShowLink(inst, 'gwiki', {
		    url : href,
		    title : title
		  }, function(result) {
			  var newUrl = gwikiContextPath + "/" + result.url;

			  i = inst.selection.getBookmark();
			  if (elm == null) {
				  var html = "<a href=\"" + gwikiEscapeAttr(newUrl) + "\" title=\"" + gwikiEscapeAttr(result.title) + "\">"
				      + gwikiEscapeAttr(result.title) + "</a>";
				  tinyMCE.activeEditor.execCommand('mceInsertContent', false, html);
			  } else {

				  // elm.setAttrib('href', newUrl);
				  tinyMCE.activeEditor.dom.setAttrib(elm, 'href', newUrl);
				  tinyMCE.activeEditor.dom.setAttrib(elm, 'title', result.title);
				  // elm.setHTML(escape(itemTitle));
				  tinyMCE.activeEditor.dom.setHTML(elm, tinyMCE.activeEditor.dom.encode(result.title));
			  }
		  });
	  },
	  _insertImage : function(ui, v) {
		  var inst = this.editor;
		  var elm = inst.selection.getNode();

		  elm = inst.dom.getParent(elm, "img");
		  var action = "insert";
		  var href = '';
		  var title = '';
		  if (elm != null && elm.nodeName == "IMG") {
			  action = "update";
			  href = inst.dom.getAttrib(elm, 'src');
			  title = inst.dom.getAttrib(elm, 'alt');
		  } else {
		  }
		  if (href.match("^" + gwikiContextPath) == gwikiContextPath) {
			  href = href.substring(gwikiContextPath.length + 1);
		  }

		  gwikiEditShowLink(inst, 'image', {
		    url : href,
		    title : title
		  }, function(result) {
			  var newUrl = gwikiContextPath + "/" + result.url;

			  i = inst.selection.getBookmark();
			  if (elm == null) {
				  var html = "<img src=\"" + gwikiEscapeAttr(newUrl) + "\" alt=\"" + gwikiEscapeAttr(result.alt) + "\"/>";
				  tinyMCE.activeEditor.execCommand('mceInsertContent', false, html);
			  } else {

				  tinyMCE.activeEditor.dom.setAttrib(elm, 'src', newUrl);
				  tinyMCE.activeEditor.dom.setAttrib(elm, 'alt', result.title);
				  // elm.setHTML(escape(itemTitle));
				  tinyMCE.activeEditor.dom.setHTML(elm, tinyMCE.activeEditor.dom.encode(result.title));
			  }
		  });
	  },
	  _insertScreenshot : function(ui, v) {
		  gwikiEditInsertImageDialog({
			  parentPageId : gwikiEditPageId
		  }, this.editor, function(editor, newId) {
			  var newUrl = gwikiContextPath + "/" + newId;
			  var html = "<img src=\"" + gwikiEscapeAttr(newUrl) + "\"/>";
			  tinyMCE.activeEditor.execCommand('mceInsertContent', false, html);
		  });
		  // alert('insert screen');
	  },
	  _insertChoosenLink : function(ui, v) {
		  var inst = this.editor;

		  var elm = inst.selection.getNode();

		  elm = inst.dom.getParent(elm, "A");
		  var action = "insert";
		  var href = '';
		  var title = '';
		  if (elm != null && elm.nodeName == "A") {
			  action = "update";
			  href = inst.dom.getAttrib(elm, 'href');
			  title = inst.dom.getAttrib(elm, 'title');
		  } else {
		  }
		  if (href.match("^" + gwikiContextPath) == gwikiContextPath) {
			  href = href.substring(gwikiContextPath.length + 1);
		  }

		  gwikiEditShowLink(inst, 'gwiki', {
		    url : href,
		    title : title
		  }, function(result) {

			  // var newUrl = gwikiContextPath + "/" + result.url;
			  var item = {
			    key : result.url,
			    title : result.title
			  };
			  gwedit_insert_pagelink(tinyMCE.activeEditor, item, elm);

		  });
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