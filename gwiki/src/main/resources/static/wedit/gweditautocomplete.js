/**
 * autocomplete on !, [ or {
 * 
 * @param weditconfig
 * @param completeChar
 * @param typedText
 * @param callback
 */
function gwedit_autocomplete_entries(completeChar, typedText, callback) {

	var url = gwedit_buildUrl("edit/WeditService") + "?method_onWeditAutocomplete=true&c=" + escape(completeChar) + "&q="
	    + escape(typedText);

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
	var url = gwedit_buildUrl(item.url);
	var html = "<img class='weditimg' data-pageid='" + item.url + "' src='" + url + "'/>";
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
		var encpageId = gwikiEscapeAttr(item.url);
		var html = "<a href='" + encpageId + "' data-pageid='" + encpageId + "' title='" + gwikiEscapeAttr(item.title)
		    + "'>" + gwikiEscapeAttr(item.title) + "</a>";
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
