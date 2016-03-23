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
function gwedit_ac_insert_macro(ed, item)
{
	wedit_deleteLeftUntil(ed, "{");
	gwedit_insert_macro(ed, item);
}

function gwedit_ac_insert_imagelink(ed, item) {
	wedit_deleteLeftUntil(ed, "!");
	wedit_update_or_insert_image(ed, item);
}



function gwedit_ac_insert_acpagelink(ed, item) {
	wedit_deleteLeftUntil(ed, "[");
	gwedit_insert_pagelink(ed, item);
}

