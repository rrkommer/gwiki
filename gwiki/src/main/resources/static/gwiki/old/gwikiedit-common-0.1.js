function gwikiEditPagePath(inputField) {
	var modc = $("#editDialogBox");
	var dlghtml = "<p>" + "gwiki.editor.editpath.dialog.hint".i18n() + "</p>" + "<p><b>" + "gwiki.common.note".i18n()
	    + ":</b> " + "gwiki.editor.editpath.dialog.note".i18n() + "</p>"
	    + "<label for=\"pathtextfield\" style=\"margin-right:10px\">" + "gwiki.common.path".i18n() + ":</label>"
	    + "<input size=\"44\" type=\"text\" id=\"pathtextfield\"" + " value='" + inputField.val() + "'/><br/>\n";

	dlghtml += "<div id='filechooser' style='width:350px; height:200px; margin-top: 20px; font-family: verdana; font-size: 10px;'></div>";

	//
	// dlghtml += "<script type='text/javascript'>";
	// dlghtml += "$(function () {";
	// dlghtml += " $(\"#filechooser\").jstree({";
	// dlghtml += " \"themes\" : { \"theme\" : \"classic\", \"dots\" : true,
	// \"icons\" : true },";
	// dlghtml += " \"plugins\" : [ \"themes\", \"html_data\", \"ui\" ],";
	// dlghtml += " \"html_data\" : {\n";
	// dlghtml += " \"ajax\" : {";
	// dlghtml += " \"url\" : \"./TreeChildren\",\n";
	// dlghtml += " \"data\" : function(n) { return { \"method_onLoadAsync\" :
	// \"true\", "
	// + "\"id\" : n.attr ? n.attr(\"id\") : \"\","
	// + "\"urlField\" : \"pathtextfield\" }; }\n";
	// dlghtml += " }";
	// dlghtml += " }\n";
	// dlghtml += " });\n";
	// dlghtml += "});\n";
	// dlghtml += "</script>";

	modc.html(dlghtml);
	// alert(dlghtml)

	var buttons = {};
	buttons["gwiki.common.cancel".i18n()] = function() {
		$(dialog).dialog('close');
	}
	buttons["gwiki.common.ok".i18n()] = function() {
		var value = $("#pathtextfield").attr('value');
		$(dialog).dialog('close');
		inputField.val(value);
	}

	var dialog = $("#editDialogBox").dialog({
	  width : 500,
	  height : 500,
	  modal : true,
	  open : function(event, ui) {
		  var to = $('#pathtextfield');
		  $("#pathtextfield").focus();
		  var initDir = $("#pathtextfield").val();
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
		        url : './TreeChildren',
		        dataType : 'json',
		        data : function(n) {
			        return {
			          method_onGetPhysicalPaths : 'true',
			          id : n.id
			        }
		        }
		      }
		    }
		  });
		  $('#pathtextfield').keyup(function() {
			  if (to) {
				  clearTimeout(to);
			  }
			  to = setTimeout(function() {
				  var v = $('#pathtextfield').val();
				  $('#filechooser').jstree(true).search(v, true, true);
			  }, 250);
		  });

		  tree.on("changed.jstree", function(e, data) {
			  // console.debug('changed: ' + data);
			  var selNone = data.node.data;
			  $("#pathtextfield").val(selNone.url);
		  });
		  tree.on('ready.jstree', function(event) {
			  var tree = $('#filechooser').jstree(true);
			  if (initDir) {
				  var ln = initDir.replace(/\//g, '_');
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
	  // close : function(event, ui) {
	  // $(dialog).dialog('destroy');
	  // },
	  overlay : {
	    backgroundColor : '#000',
	    opacity : 0.5
	  },
	  buttons : buttons
	});
}

function gwikiEditSettings() {
	$('#EditorSettings').hide();
	var modc = $("#EditorSettings");
	var buttons = {};

	buttons["gwiki.common.cancel".i18n()] = function() {
		$(dialog).dialog('close');
	}

	buttons["gwiki.common.ok".i18n()] = function() {
		$(dialog).dialog('close');
	}

	var dialog = $("#EditorSettings").dialog({
	  width : 640,
	  modal : true,
	  open : function(event, ui) {
		  $("#pathtextfield").focus();
	  },
	  close : function(event, ui) {
		  $('#EditorSettings').hide();
		  $(dialog).dialog('destroy');
		  $('#EditorSettings').appendTo('form#editForm');
	  },
	  overlay : {
	    backgroundColor : '#000',
	    opacity : 0.5
	  },
	  buttons : buttons
	});
}

$(document).ready(function() {
	var linkAutoCompleteUrl = gwikiLocalUrl("edit/WeditService");

	$('.wikiPageEditText').autocomplete({
	  source : function(req, callback) {
		  $.ajax({
		    url : linkAutoCompleteUrl,
		    type : 'GET',
		    data : {
		      method_onPageIdAutocomplete : true,
		      pageType : 'wiki',
		      q : req.term
		    },
		    success : function(data) {
			    callback(data);
		    },
		    fail : function(jqXHR, textStatus, errorThrown) {
			    console.error("got error: " + textStatus);
		    }
		  });
	  },
	  open : function(event, ui) {
		  var elem = $(event.target);
		  var onTopElem = elem.closest('.ui-front');
		  if (onTopElem.length > 0) {
			  var widget = elem.autocomplete('widget');
			  widget.zIndex(onTopElem.zIndex() + 1);
		  }
	  },
	  autoFocus : true,
	  matchContains : true,
	  cacheLength : 2,
	  matchSubset : false,
	  minChars : 2,
	  width : 350,
	  scroll : true,
	  scrollHeight : 400,
//	  appendTo : ".ui-dialog",
	  select : function(even, ui) {
		  var item = ui.item;
		  var v = item.key;
		  $(this).val(v);
		  return false;
	  },
	  create : function() {
		  $(this).data('ui-autocomplete')._renderItem = function(ul, item) {
			  return $("<li>" + item.label + "</li>").appendTo(ul);
		  }
	  }
	});

});