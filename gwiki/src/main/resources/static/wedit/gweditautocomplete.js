/**
 * autocomplete on !, [ or {
 * 
 * @param weditconfig
 * @param completeChar
 * @param typedText
 * @param callback
 */
function gwedit_autocomplete_entries(completeChar, typedText, callback) {

	var url = gwedit_buildUrl("edit/WeditService") + "?method_onWeditAutocomplete=true&c=" + completeChar + "&q="
	    + typedText;

	var json = null;
	$.ajax(url, {
	  dataType : "text",
	  global : false,
	  success : function(data) {
		  // console.debug("got json nested: " + data);
		  var jdata = eval('(' + data + ')');
		  // console.debug("got json eval nested: " + jdata);
		  if (jdata.ret == 0) {
			  callback(jdata.list);
		  } else {
			  log.warn("Error get list: " + jdata.ret + "; " + jdata.message);
		  }

	  },
	  fail : function(jqXHR, textStatus, errorThrown) {
		  console.error("got json error: " + textStatus);
	  }
	});
}

function gwedit_insert_imagelink(ed, item) {
	wedit_deleteLeftUntil(ed, "!");
	gwedit_insert_imagelink_direct(ed, item);
}
function gwedit_replace_image_before(ed, item) {
	var range = ed.selection.getRng(true);
	var startc = range.startContainer;
	var starto = range.startOffset;
	if (range.startContainer.childNodes.length > starto - 1) {
		var oldimg = range.startContainer.childNodes[starto - 1];
		if (oldimg.nodeName.toLowerCase() == "img") {
			range.startContainer.removeChild(range.startContainer.childNodes[starto - 1]);
		} else {
			console.debug("no img found to replace (no img)")
		}
	} else {
		console.debug("no img found to replace (no children)")
	}
	gwedit_insert_imagelink_direct(ed, item);
}
function gwedit_insert_imagelink_direct(ed, item) {
	var url = gwedit_buildUrl(item.key);
	var html = "<img class='weditimg' data-pageid='" + item.key + "' src='" + url + "'/>";
	var node = tedit_insertRaw(ed, html);
	$(node).on('click', function(el) {
		gwedit_click_imagelink(el);
	});
}

function gwedit_click_imagelink(el) {
	console.debug("Image clicked: " + el);
}

function gwedit_insert_acpagelink(ed, item) {
	wedit_deleteLeftUntil(ed, "[");
	gwedit_insert_pagelink(ed, item);
}

function gwedit_insert_pagelink(ed, item, activeNode) {

	// i = id.selection.getBookmark();
	if (activeNode == null) {
		var encpageId = gwikiEscapeInput(item.key);
		var html = "<a href='" + encpageId + "' data-pageid='" + encpageId + "' title='" + gwikiEscapeInput(item.title)
		    + "'>" + gwikiEscapeInput(item.title) + "</a>";
		tinyMCE.activeEditor.execCommand('mceInsertContent', false, html);
	} else {

		// elm.setAttrib('href', newUrl);
		tinyMCE.activeEditor.dom.setAttrib(activeNode, 'href', item.key);
		tinyMCE.activeEditor.dom.setAttrib(activeNode, 'title', item.title);
		tinyMCE.activeEditor.dom.setAttrib(activeNode, 'data-pageid', item.key);
		// elm.setHTML(escape(itemTitle));
		tinyMCE.activeEditor.dom.setHTML(activeNode, tinyMCE.activeEditor.dom.encode(item.title));
	}

}

function gwedit_insert_macro(ed, item) {
	console.debug("insert macro");
	wedit_deleteLeftUntil(ed, "{");
	var withbody = item.macro_withbody;
	var evalbody = item.macro_evalbody;
	var bodyid = wedit_genid("mbody_");
	var html = "<div contenteditable='false' class='mceNonEditable weditmacrohead' data-macrohead='" + item.key + "'>"
	    + item.key;
	if (withbody) {
		html += "<div id='" + bodyid + "' contenteditable='true' class='mceEditable weditmacrobody";
		if (!evalbody) {
			html += " editmacrobd_pre";
		}
		html += "'>";
		if (evalbody) {
			html += " ";
		} else {
			html += "<pre id='" + bodyid + "'>\n</pre>";
		}

		html += "</div>";
	}
	html += "</div>";
	var node = tedit_insertRaw(ed, html);
	if (withbody) {

		if (node.childNodes.length >= 2) {
			var body = node.childNodes[1];
			// var body = ed.$.find("#" + bodyid);
			if (body.childNodes.length > 0) {
				var tn = body.childNodes[0];
				if (tn.childNodes.length > 0) {
					tn = tn.childNodes[0];
				}
				ed.selection.setCursorLocation(tn, 0);
			}
		}
	}
	$(node).on('click', function(el) {
		gwedit_click_macrolink(el);
	});
}

function gwedit_click_macrolink(el) {
	console.debug("Image clicked: " + el);

}
