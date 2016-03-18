/**
 * 
 * @param editor
 * @param pageType
 *          wiki or image
 * @param currentLink {
 *          url:, title: }
 * @param callback
 *          currentLink structure
 * @return nothing
 */
function gwikiEscapeInput(str) {
	return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
}

function gwikiEditShowLinkSuggest(parentWindow, pageType, currentLink, callback) {
	var modc = $("#editDialogBox");
	var dlghtml = "gwiki.editor.wikilink.dialog.title".i18n() + ": <input size=\"30\" type=\"text\" id=\"linkprtitle\"";
	if (currentLink.title) {
		dlghtml += " value=\"" + gwikiEscapeInput(currentLink.title) + "\"";
	}
	dlghtml += "><br/>\n" + "gwiki.editor.wikilink.dialog.link".i18n()
	    + ": <input size=\"30\" type=\"text\" id=\"linkpropt\"";
	if (currentLink.url) {
		dlghtml += " value=\"" + gwikiEscapeInput(currentLink.url) + "\"";
	}
	dlghtml += ">";
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
		var ret = {
		  url : $("#linkpropt").val(),
		  title : $("#linkprtitle").val()
		};
		callback(ret);
	}

	var dialog = $("#editDialogBox").dialog({
	  modal : true,
	  open : function(event, ui) {
		  $("#linkpropt").focus();
	  },
	  close : function(event, ui) {
		  $(dialog).dialog('destroy');
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
	$('#linkpropt').autocomplete({
	  source : "./PageSuggestions?pageType=" + pageType,
	  matchContains : true,
	  minChars : 0,
	  width : 300,
	  cacheLength : 1,
	  max : 1000,
	  formatItem : function(row) {
		  return row[1] + "<br><i>(" + row[0] + ")</i>";
	  }
	}).result(function(event, item) {
		$("#linkprtitle").val(item[1]);
	})
}


function gwikiEditShowLink(parentWindow, pageType, currentLink, callback) {
	var modc = $("#editDialogBox");
	var dlghtml = "gwiki.editor.wikilink.dialog.title".i18n() + ": <input size=\"50\" type=\"text\" id=\"linkprtitle\"";
	if (currentLink.title) {
		dlghtml += " value=\"" + gwikiEscapeInput(currentLink.title) + "\"";
	}
	dlghtml += "><br/>\n" + "gwiki.editor.wikilink.dialog.link".i18n()
	    + ": <input size=\"50\" type=\"text\" id=\"linkpropt\"";
	if (currentLink.url) {
		dlghtml += " value=\"" + gwikiEscapeInput(currentLink.url) + "\"";
	}
	dlghtml += ">";
	dlghtml += "<div id='filechooser' style='width:350px; height:200px; margin-top: 20px; font-family: verdana; font-size: 10px;'></div>";

	dlghtml += "<script type='text/javascript'>";
	dlghtml += "$(function () {";
	dlghtml += "  $(\"#filechooser\").jstree({";
	dlghtml += "    \"themes\" : { \"theme\" : \"classic\", \"dots\" : true, \"icons\" : true },";
	dlghtml += "    \"plugins\" : [ \"themes\", \"html_data\", \"ui\" ],";
	dlghtml += "    \"html_data\" : {\n";
	dlghtml += "      \"ajax\" : {";
	dlghtml += "        \"url\" : \"./TreeChildren\",\n";
	dlghtml += "        \"data\" : function(n) {   return { \"method_onLoadAsync\" : \"true\", "
	    + "\"id\" : n.attr ? n.attr(\"id\") : \"\"," + "\"urlField\" : \"linkpropt\","
	    + "\"titleField\" : \"linkprtitle\" };      }\n";
	dlghtml += "      }";
	dlghtml += "    }\n";
	dlghtml += "  });\n";
	dlghtml += "});\n";
	dlghtml += "</script>";

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
		var ret = {
		  url : $("#linkpropt").val(),
		  title : $("#linkprtitle").val()
		};
		callback(ret);
	}

	var dialog = $("#editDialogBox").dialog({
	  width : 500,
	  modal : true,
	  open : function(event, ui) {
		  $("#linkpropt").focus();

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
		$('#linkpropt').autocomplete({
		  source : "./PageSuggestions?pageType=" + pageType,
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