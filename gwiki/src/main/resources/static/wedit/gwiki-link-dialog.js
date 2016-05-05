/**
 * deprecated
 * 
 * @param parentWindow
 * @param pageType
 * @param currentLink
 * @param callback
 */

function WikiLinkItem(linktype) {
	// one of wiki, attachment, image
	this.type = linktype;
	this.found = false;
	this.url = null;
	this.title = null;
	this.tip = null;
	this.titleDefined = false;
	this.styleClass = null;
	this.width = null;
	this.height = null;
	this.windowTarget = null;
	this.element = null;
	this.textRange = null;
	this.thumbnail = null;
}

function gwikiEditShowLink(parentWindow, currentLink, callback) {
	var modc = $("#editDialogBox");
	var dlghtml = '';
	var isImage = currentLink.type == 'image';
	var opName = "Insert ";
	if (currentLink.found) {
		opName = "Edit "
	}
	if (currentLink.type == 'image') {
		opName += "Image Link";
	} else if (currentLink.type == 'attachment') {
		opName += "Attachment Link";
	} else {
		opName += "Page Link";
	}
	dlghtml += "<h4>" + opName + "</h4>\n";
	dlghtml += "<div><div style='display:inline-block; vertical-align: top; padding: 5px;'>";

	dlghtml += "<form>" + "<p><label for='linkprtitle' style='display:block;'>"
	dlghtml += "gwiki.editor.wikilink.dialog.title".i18n() + "</label>";
	dlghtml += "<input size=\"50\" type=\"text\" id=\"linkprtitle\"";
	if (currentLink.title) {
		dlghtml += " value=\"" + gwikiEscapeAttr(currentLink.title) + "\"";
	}
	dlghtml += "></p>";
	dlghtml += "<p><label for='linkpropt' style='display:block;'>"
	dlghtml += "gwiki.editor.wikilink.dialog.link".i18n() + "</label>"
	    + "<input size=\"50\" type=\"text\" id=\"linkpropt\"";
	if (currentLink.url) {
		dlghtml += " value=\"" + gwikiEscapeAttr(currentLink.url) + "\"";
	}
	dlghtml += "></p>";

	// /////////////////////////////////////////////////////////////////////////////
	// optionals

	dlghtml += "<div  style='display:block;' id='gwikdlglnkdetails'>"
	    + "<span style='background-color: #DCDCDC;border-style: solid;border-width: 2px;font-weight: bold;'>&nbsp;+&nbsp;</span></div>";
	dlghtml += "<div id='gwikidlnkdetailsdiv' style='display: none;'>";
	if (currentLink.type == 'wiki') {
		dlghtml += "<p><label for='gweditwindowTarget' style='display:block;'>"
		dlghtml += "gwiki.editor.wikilink.dialog.windowTarget".i18n() + "</label>"
		    + "<input size=\"50\" type=\"text\" id=\"gweditwindowTarget\"";
		if (currentLink.windowTarget) {
			dlghtml += " value=\"" + gwikiEscapeAttr(currentLink.windowTarget) + "\"";
		}
		dlghtml += "></p>";

	}
	if (currentLink.type == 'image') {
		dlghtml += "<div style='display: inline-block;'>";
		dlghtml += "<label for='gweditwidth' style='display:block;'>";
		dlghtml += "gwiki.editor.wikilink.dialog.width".i18n() + "</label>"
		dlghtml += "<input size='20' type='text' id='gweditwidth'";
		if (currentLink.width) {
			dlghtml += " value='" + gwikiEscapeAttr(currentLink.width) + "'";
		}
		dlghtml += ">";
		dlghtml += "</div><div style='display: inline-block;'>";
		dlghtml += "<label for='gweditheight' style='display:block;'>";
		dlghtml += "gwiki.editor.wikilink.dialog.height".i18n() + "</label>"
		    + "<input size='20' type='text' id='gweditheight'";
		if (currentLink.height) {
			dlghtml += " value='" + gwikiEscapeAttr(currentLink.height) + "'";
		}
		dlghtml += ">";
		dlghtml += "</div>";

		dlghtml += "<p><label for='gweditthumbnail' style='display:block;'>"
		dlghtml += "gwiki.editor.wikilink.dialog.thumbnail".i18n() + "</label>"
		    + "<input size='50' type='text' id='gweditthumbnail'";
		if (currentLink.thumbnail) {
			dlghtml += " value='" + gwikiEscapeAttr(currentLink.thumbnail) + "'";
		}
		dlghtml += "></p>";

	}
	dlghtml += "<p><label for='gweditstyleClass' style='display:block;'>"
	dlghtml += "gwiki.editor.wikilink.dialog.styleClass".i18n() + "</label>"
	    + "<input size='50' type='text' id='gweditstyleClass'";
	if (currentLink.styleClass) {
		dlghtml += " value=\"" + gwikiEscapeAttr(currentLink.styleClass) + "\"";
	}
	dlghtml += "></p>";
	dlghtml += "<p><label for='gweditstyle' style='display:block;'>"
	dlghtml += "gwiki.editor.wikilink.dialog.style".i18n() + "</label>" + "<input size='50' type='text' id='gweditstyle'";
	if (currentLink.style) {
		dlghtml += " value=\"" + gwikiEscapeAttr(currentLink.style) + "\"";
	}
	dlghtml += "></p>";
	dlghtml += "</div>";
	dlghtml += "<div id='filechooser' style='width:350px; height:200px; margin-top: 20px; font-family: verdana; font-size: 10px;'></div>";
	dlghtml += "</form>";
	dlghtml += "</div><div id='gweditdlgpreviewdiv' style='display:" + (isImage ? 'inline-block' : 'none')
	    + "; vertical-align: top; padding: 5px;'>" + "<div id='gweditdlgpreview'></div>" + "</div></div>";
	modc.html(dlghtml);

	var buttons = gwiki_dlg_create_ok_cancel_buttons('#editDialogBox', {
		onOk : function() {
			var lt = $("#linkpropt").attr('value');
			$(dialog).dialog('close');
			parentWindow.focus();
			var ret = new WikiLinkItem(currentLink.type);
			ret.url = $("#linkpropt").val();
			ret.title = $("#linkprtitle").val();
			if (currentLink.type == 'wiki') {
				ret.windowTarget = $('#gweditwindowTarget').val();
			}
			if (currentLink.type == 'image') {
				ret.width = $('#gweditwidth').val();
				ret.height = $('#gweditheight').val();
				ret.thumbnail = $('#gweditthumbnail').val();
			}
			ret.styleClass = $('#gweditstyleClass').val();
			ret.style = $('#gweditstyle').val();
			callback(ret);
		}
	})

	var isOnInitialOpen = false;
	var dialog = $("#editDialogBox").dialog(
	    {
	      width : isImage ? 800 : 400,
	      dialogClass : 'jquiNoDialogTitle',
	      modal : true,
	      open : function(event, ui) {
		      $('#gwikdlglnkdetails').on('click', function() {
			      $('#gwikidlnkdetailsdiv').toggle();
		      });
		      $("#linkpropt").focus();

		      var tree = $("#filechooser").jstree({
		        plugins : [ 'search', 'themes', 'ui' ],
		        core : {
		          animation : false,
		          themes : {
		            theme : 'classic',
		            dots : true,
		            icons : true
		          },
		          data : {
		            url : './TreeChildren?type=' + currentLink.type,
		            dataType : 'json',
		            data : function(n) {
			            return {
			              method_onLoadAsync : 'true',
			              id : n.id
			            }
		            }
		          }
		        }
		      });
		      var to = false;
		      $('#linkpropt').keyup(function(event) {
			      if (event.keyCode == 13) { // ENTER
				      var first = $('.jstree-search').first().attr('id');
				      if (!first) {
					      return;
				      }
				      var tree = $('#filechooser').jstree(true);
				      var node = tree.get_node(first);
				      tree.select_node(node);
				      setTimeout(function() {
					      gwiki_dlg_click_ok();
				      }, 100);
			      }
			      if (to) {
				      clearTimeout(to);
			      }
			      to = setTimeout(function() {
				      var v = $('#linkpropt').val();
				      $('#filechooser').jstree(true).search(v, true, true);
			      }, 250);
		      });
		      tree.on("changed.jstree", function(e, data) {
			      var selNone = data.node.data;
			      $("#linkpropt").val(selNone.url);
			      if (!isOnInitialOpen) {
				      $("#linkprtitle").val(selNone.title);
			      }
			      if (selNone.type == 'image') {
				      var img = $("<img>").attr('src', gwedit_buildUrl(selNone.url)).attr('style',
				          'max-height: 300px; max-width: 300px;')
				      $('#gweditdlgpreview').html('');
				      $('#gweditdlgpreview').append(img);
			      }
		      });
		      tree.on('ready.jstree', function(event) {
			      var tree = $('#filechooser').jstree(true);
			      if (currentLink.url) {
				      var ln = currentLink.url.replace(/\//g, '_');
				      var st = tree.get_node(ln);
				      isOnInitialOpen = true;
				      tree.select_node(ln);
				      isOnInitialOpen = false;
			      } else if (gwikiContext.gwikiEditPageId) {
				      var ln = gwikiContext.gwikiEditPageId.replace(/\//g, '_');
				      var st = tree.get_node(ln);
				      if (st) {
					      if (st.parent) {
						      tree.open_node(st.parent);
					      } else {
						      tree.open_node(st);
					      }
				      }
			      }
		      });
	      },
	      overlay : {
	        backgroundColor : '#000',
	        opacity : 0.5
	      },
	      buttons : buttons,
	      Abbrechen : function() {
		      $(this).dialog('close');
		      parentWindow.focus();

	      }
	    });

}

function wedit_show_link_dialog(ed) {
	var item = wedit_find_current_link(ed);
	gwikiEditShowLink(ed, item, function(newitem) {
		gwedit_insert_pagelink(ed, newitem, item.element);
	});
}

function wedit_show_image_dialog(ed) {
	var item = wedit_find_current_image(ed);
	gwikiEditShowLink(ed, item, function(newitem) {
		wedit_update_or_insert_image(ed, newitem, item.element);
	});
}
function wedit_show_attachment_dialog(ed) {
	var item = wedit_find_current_link(ed);
	item.type = 'attachment';
	gwikiEditShowLink(ed, item, function(newitem) {
		gwedit_insert_pagelink(ed, newitem, item.element);
	});
}
