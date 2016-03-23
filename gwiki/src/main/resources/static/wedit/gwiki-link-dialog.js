/**
 * deprecated
 * 
 * @param parentWindow
 * @param pageType
 * @param currentLink
 * @param callback
 */
// function gwikiEditShowLinkSuggestOld(parentWindow, pageType, currentLink,
// callback) {
// var modc = $("#editDialogBox");
// var dlghtml = "gwiki.editor.wikilink.dialog.title".i18n() + ": <input
// size=\"30\" type=\"text\" id=\"linkprtitle\"";
// if (currentLink.title) {
// dlghtml += " value=\"" + gwikiEscapeAttr(currentLink.title) + "\"";
// }
// dlghtml += "><br/>\n" + "gwiki.editor.wikilink.dialog.link".i18n()
// + ": <input size=\"30\" type=\"text\" id=\"linkpropt\"";
// if (currentLink.url) {
// dlghtml += " value=\"" + gwikiEscapeAttr(currentLink.url) + "\"";
// }
// dlghtml += ">";
// modc.html(dlghtml);
//
// var buttons = {};
// buttons["gwiki.common.cancel".i18n()] = function() {
// $(dialog).dialog('close');
// parentWindow.focus();
// }
// buttons["gwiki.common.ok".i18n()] = function() {
// var lt = $("#linkpropt").attr('value');
// $(dialog).dialog('close');
// parentWindow.focus();
// var ret = {
// url : $("#linkpropt").val(),
// title : $("#linkprtitle").val()
// };
// callback(ret);
// }
//
// var dialog = $("#editDialogBox").dialog({
// modal : true,
// dialogClass: 'jquiNoDialogTitle',
// open : function(event, ui) {
// $("#linkpropt").focus();
// },
// close : function(event, ui) {
// $(dialog).dialog('destroy');
// },
// overlay : {
// backgroundColor : '#000',
// opacity : 0.5
// },
// buttons : buttons,
// Abbrechen : function() {
// $(this).dialog('close');
// parentWindow.focus();
// }
// });
// $('#linkpropt').autocomplete({
// source : "./PageSuggestions?pageType=" + gwikiEscapeUrlParam(pageType),
// matchContains : true,
// minChars : 0,
// width : 300,
// cacheLength : 1,
// max : 1000,
// formatItem : function(row) {
// return row[1] + "<br><i>(" + row[0] + ")</i>";
// }
// }).result(function(event, item) {
// $("#linkprtitle").val(item[1]);
// })
// }
function WikiLinkItem(linktype) {
	// one of wiki, attachment, image
	this.type = linktype;
	this.found = false;
	this.url = null;
	this.title = null;
	this.tip = null;
	this.titleDefined = false;
	this.styleClass = null;
	this.windowTarget = null;
	this.element = null;
	this.textRange = null;
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
	dlghtml +=  "<input size=\"50\" type=\"text\" id=\"linkprtitle\"";
	if (currentLink.title) {
		dlghtml += " value=\"" + gwikiEscapeAttr(currentLink.title) + "\"";
	}
	dlghtml += "></p>";
	dlghtml += "<p><label for='linkpropt' style='display:block;'>"
		dlghtml += "gwiki.editor.wikilink.dialog.link".i18n() + "</label>" 
	    + "<input size=\"50\" type=\"text\" id=\"linkpropt\"";
	if (currentLink.windowTarget) {
		dlghtml += " value=\"" + gwikiEscapeAttr(currentLink.windowTarget) + "\"";
	}
	dlghtml += "></p>";
	dlghtml += "<div  style='display:block;' id='gwikdlglnkdetails'>(+)</div>";
	dlghtml += "<div id='gwikidlnkdetailsdiv' style='display: none;'>";
	if (currentLink.type == 'wiki') {
		dlghtml += "<p><label for='windowTarget' style='display:block;'>"
			dlghtml += "gwiki.editor.wikilink.dialog.windowTarget".i18n() + "</label>" 
		    + "<input size=\"50\" type=\"text\" id=\"windowTarget\"";
		if (currentLink.windowTarget) {
			dlghtml += " value=\"" + gwikiEscapeAttr(currentLink.windowTarget) + "\"";
		}
		dlghtml += "></p>";
		
	}
	
	dlghtml += "<p><label for='linkstyleClass' style='display:block;'>"
		dlghtml += "gwiki.editor.wikilink.dialog.linkstyleClass".i18n() + "</label>" 
	    + "<input size=\"50\" type=\"text\" id=\"linkstyleClass\"";
	if (currentLink.styleClass) {
		dlghtml += " value=\"" + gwikiEscapeAttr(currentLink.styleClass) + "\"";
	}
	dlghtml += "></p>";
	dlghtml += "</div>";
	dlghtml += "<div id='filechooser' style='width:350px; height:200px; margin-top: 20px; font-family: verdana; font-size: 10px;'></div>";
	dlghtml += "</form>";
	dlghtml += "</div><div id='gweditdlgpreviewdiv' style='display:" + (isImage ? 'inline-block' : 'none')
	    + "; vertical-align: top; padding: 5px;'>" + "<div id='gweditdlgpreview'></div>" + "</div></div>";
	modc.html(dlghtml);

	var buttons = {};
	buttons["gwiki.common.cancel".i18n()] = function() {
		$(dialog).dialog('close');
		parentWindow.focus();
	}
	buttons["gwiki.common.ok".i18n()] = function() {
		var lt = $("#linkpropt").attr('value');
		$(dialog).dialog('close');
		parentWindow.focus();
		var ret = new WikiLinkItem(currentLink.type);
		ret.url = $("#linkpropt").val();
		ret.title = $("#linkprtitle").val();
		callback(ret);
	}

	var dialog = $("#editDialogBox").dialog({
	  width : isImage ? 800 : 400,
	  dialogClass : 'jquiNoDialogTitle',
	  modal : true,
	  open : function(event, ui) {
	  	$('#gwikdlglnkdetails').on('click', function() {
	  		$('#gwikidlnkdetailsdiv').toggle();
//	  		if ($('#gwikidlnkdetailsdiv').visible()) {
//	  			$('#gwikidlnkdetailsdiv').hide();
//	  		} else {
//	  			$('#gwikidlnkdetailsdiv').show();
//	  		}
	  	});
		  $("#linkpropt").focus();
		  
		  var tree = $("#filechooser").jstree({
		    plugins : [ 'search', 'themes', 'ui' ],
		    core : {
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
		  $('#linkprtitle').keyup(function() {
			  if (to) {
				  clearTimeout(to);
			  }
			  to = setTimeout(function() {
				  var v = $('#linkprtitle').val();
				  $('#filechooser').jstree(true).search(v);
			  }, 250);
		  });
		  $('#linkpropt').keyup(function() {
			  if (to) {
				  clearTimeout(to);
			  }
			  to = setTimeout(function() {
				  var v = $('#linkpropt').val();
				  $('#filechooser').jstree(true).search(v);
			  }, 250);
		  });
		  // tree.on('select_node.jstree', function(event) {
		  // var node = event.node;
		  // var item = event.selected;
		  // console.debug('selected: ' + item);
		  // });
		  tree.on("changed.jstree", function(e, data) {
			  console.debug('changed: ' + data);
			  var selNone = data.node.data;
			  $("#linkpropt").val(selNone.url);
			  $("#linkprtitle").val(selNone.title);
			  if (selNone.type == 'image') {
			  	var img = $("<img>")
			  		.attr('src', gwedit_buildUrl(selNone.url))
			  		.attr('style', 'max-height: 300px; max-width: 300px;')
			  	$('#gweditdlgpreview').html('');
			  	$('#gweditdlgpreview').append(img);
			  }
		  });
		  tree.on('ready.jstree', function(event) {
			  var tree = $('#filechooser').jstree(true);
			  if (currentLink.url) {
				  var ln = currentLink.url.replace(/\//g, '_');
				  var st = tree.get_node(ln);
				  tree.select_node(ln);
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
	$('#linkpropt').focus(function() {
		// TODO replace
		$('#linkpropt').autocomplete({
		  source : "./PageSuggestions?pageType=" + gwikiEscapeUrlParam(currentLink.type),
		  matchContains : true,
		  minChars : 0,
		  width : 460,
		  cacheLength : 1,
		  max : 1000,
		  formatItem : function(row) {
			  return row[1] + "<br><i>(" + row[0] + ")</i>";
		  },
		  select : function(event, ui) {
			  $("#linkprtitle").val(item[1]);
		  }
		})._renderItem = function(ul, row) {
			return row[1] + "<br><i>(" + row[0] + ")</i>";
		};
	});
}

function wedit_find_parent_node_type_el(el, nodeName) {
	if (el.nodeName == nodeName) {
		return el;
	}
	if (el.parentNode) {
		return wedit_find_parent_node_type_el(el.parentNode, nodeName);
	}
	return null;
}
function wedit_find_parent_node_type(ed, nodeName) {
	var rng = ed.selection.getRng(true);
	var startC = rng.startContainer;
	return wedit_find_parent_node_type_el(startC, nodeName);
}

function wedit_find_current_link(ed) {
	var ret = new WikiLinkItem('wiki');
	var startC = wedit_find_parent_node_type(ed, 'A');
	if (!startC) {
		return ret;
	}
	ret.element = startC;
	ret.url = ret.element.getAttribute('data-wiki-target');
	if (!ret.url) {
		ret.url = ret.element.getAttribute('href');
	}
	ret.title = ret.element.getAttribute('data-wiki-title');
	if (!ret.title) {
		ret.title = ret.element.getAttribute('title');
	}
	ret.tip = ret.element.getAttribute('data-wiki-tip');
	ret.styleClass = ret.element.getAttribute('data-wiki-styleClass');
	ret.windowTarget = ret.element.getAttribute('data-wiki-windowTarget');

	return ret;
}

function wedit_update_or_insert_link(ed, olditem, newItem) {
	if (olditem.element) {
		olditem.element.setAttribute('href', newItem.url);
		olditem.element.setAttribute('title', newItem.title);
		olditem.element.textContent = newItem.title;
	} else {
		gwedit_insert_pagelink(ed, newItem);
	}
}

function wedit_show_link_dialog(ed) {
	console.debug('link selected');
	var item = wedit_find_current_link(ed);
	gwikiEditShowLink(ed, item, function(newitem) {
		wedit_update_or_insert_link(ed, item, newitem);
	});
}

function wedit_show_image_dialog(ed) {
	console.debug('image selected');
}
function wedit_show_attachment_dialog(ed) {
	console.debug('attachment selected');
}
