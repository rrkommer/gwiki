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
		    'class' : 'mceIcon mce_link'
		  });
		  ed.addButton('wikichoosenlink', {
		    title : "gwiki.editor.tinymce.gwiki.wikilink.title".i18n(),
		    cmd : 'mceGWikiInsertChoosenLink',
		    'class' : 'mceIcon mce_link'
		  });
		  ed.addButton('wikisaveaction', {
		    title : "gwiki.editor.button.tooltip.save".i18n(),
		    cmd : 'mceGWikiSaveAction',
		    'class' : 'mceIcon mce_save'
		  });
		  ed.addButton('wikicancelaction', {
		    title : "gwiki.editor.button.tooltip.cancel".i18n(),
		    cmd : 'mceGWikiCancelAction',
		    'class' : 'mceIcon mce_cancel'
		  });

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
		    author : 'Micromata GmbH',
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

		  gwikiEditShowLinkSuggest(inst, 'gwiki', {
		    url : href,
		    title : title
		  }, function(result) {
			  var newUrl = gwikiContextPath + "/" + result.url;

			  i = inst.selection.getBookmark();
			  if (elm == null) {
				  var html = "<a href=\"" + gwikiEscapeInput(newUrl) + "\" title=\"" + gwikiEscapeInput(result.title) + "\">"
				      + gwikiEscapeInput(result.title) + "</a>";
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

		  gwikiEditShowLinkSuggest(inst, 'image', {
		    url : href,
		    title : title
		  }, function(result) {
			  var newUrl = gwikiContextPath + "/" + result.url;

			  i = inst.selection.getBookmark();
			  if (elm == null) {
				  var html = "<img src=\"" + gwikiEscapeInput(newUrl) + "\" alt=\"" + gwikiEscapeInput(result.alt) + "\"/>";
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
			  var html = "<img src=\"" + gwikiEscapeInput(newUrl) + "\"/>";
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

//			  var newUrl = gwikiContextPath + "/" + result.url;
			  var item = {
			  		key: result.url,
			  		title: result.title
			  };
			  gwedit_insert_pagelink(tinyMCE.activeEditor, item, elm);
			  
		  });
	  },
	  _saveAction : function(ui, v) {
		  $("#gwikieditsavebutton").click();
	  },
	  _cancelAction : function(ui, v) {
		  $("#gwikieditcancelbutton").click();
	  }
	});

	// Register plugin
	tinymce.PluginManager.add('gwiki', tinymce.plugins.GWikiPlugin);
})();