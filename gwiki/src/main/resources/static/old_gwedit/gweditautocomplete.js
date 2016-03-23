/**
 * autocomplete on !, [ or {
 * 
 * @param weditconfig
 * @param completeChar
 * @param typedText
 * @param callback
 */
function gwedit_autocomplete_entries(jwedit, weditconfig, completeChar, typedText, callback) {

	var url = weditconfig.linkAutoCompleteUrl + "?method_onWeditAutocomplete=true&c=" + completeChar + "&q=" + typedText;

	var json = null;
	$.ajax(url, {
	  dataType : "text",
	  global : false,
	  success : function(data) {
		  console.debug("got json nested: " + data);
		  var jdata = eval('(' + data + ')');
		  console.debug("got json eval nested: " + jdata);
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

function gwedit_insert_imagelink(item) {
	var range = wedit_getSavedRange();
	
	var url = gwedit_buildUrl(item.url);
	var img = $("<img class='weditimg' data-pageid='" + item.url + "' src='" + url + "'/>");
	wedit_domobserver_ignore_next_image = true;
	var imgnode = img[0];
	range.insertNode(imgnode);
	wedit_removeLeftUntil("!", true);
	wedit_caretafterelement(imgnode);
	
}
function gwedit_insert_pagelink(item) {
	var range = wedit_getSavedRange();
	
	var url = gwedit_buildUrl(item.key);
	var img = $("<span class='weditpaglink' data-pageid='" + item.key + "'>" + item.title + "</span>");
	wedit_domobserver_ignore_next_image = true;
	var imgnode = img[0];
	range.insertNode(imgnode);
	wedit_removeLeftUntil("[", true);
	wedit_caretafterelement(imgnode);
	
}
