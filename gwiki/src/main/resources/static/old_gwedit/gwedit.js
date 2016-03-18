function gwedit_buildUrl(pageId) {
	// return gwikiContextPath + gwikiServletPath + pageId;
	return gwikiHomeUrl + pageId;
}

function gwedit_safeHandler(partName) {
	gwikiRestoreFromRte(gwikiCurrentPart);
	gwikiUnsetContentChanged()
}

/**
 * ajustments of wedit
 */
function restoreWeditToTextArea(partName, callback) {
	var id = "weditordiv" + partName;
	var wm = weditmap.get(id);
	var el = wm.get('jthis');
	var weditconfig = wm.get('config');

	var html = el.html();

	var url = weditconfig.linkAutoCompleteUrl + "?method_onWeditToWiki=true";

	$.ajax(url, {
	  dataType : "text",
	  data : "txt=" + encodeURIComponent(html),
	  global : false,
	  success : function(data) {
		  console.debug("got json nested: " + data);
		  var jdata = eval('(' + data + ')');
		  console.debug("got json eval nested: " + jdata);
		  if (jdata.ret == 0) {
			  $("#textarea" + partName).val(jdata.text);
			  callback(partName);
		  } else {
			  log.warn("Error get list: " + jdata.ret + "; " + jdata.message);
		  }

	  },
	  fail : function(jqXHR, textStatus, errorThrown) {
		  console.error("got json error: " + textStatus);
	  }
	});
	console.debug("restoreWeditToTextArea: " + partName + html);

}
