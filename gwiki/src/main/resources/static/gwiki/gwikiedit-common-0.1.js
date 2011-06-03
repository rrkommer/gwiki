function gwikiEditPagePath() {
	
	var path = document.getElementById('editPagePath').value;
	var modc = $("#editDialogBox");
	var dlghtml = "<b>" + "gwiki.common.note".i18n() + ":</b> "
			+ "gwiki.editor.editpath.dialog.hint".i18n() + "<br /><br />"
			+ "<label for=\"pathtextfield\" style=\"margin-right:10px\">"
			+ "gwiki.common.path".i18n() + ":</label>"
			+ "<input size=\"44\" type=\"text\" id=\"pathtextfield\""
				+ " value='" + path + "'/><br/>\n";

	modc.html(dlghtml);
	
	var buttons = {};
	
	buttons["gwiki.common.cancel".i18n()] = function() {
		$(dialog).dialog('close');
	}
	
	buttons["gwiki.common.ok".i18n()] = function() {
		var value = $("#pathtextfield").attr('value');
		$(dialog).dialog('close');
		document.getElementById('editPagePath').value = value;		
	}
	
	var dialog = $("#editDialogBox").dialog({
		width : 500,
		modal : true,
		open : function(event, ui) {
			$("#pathtextfield").focus();
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
		}
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
			$(dialog).dialog('destroy');
		},
		overlay : {
			backgroundColor : '#000',
			opacity : 0.5
		},
		buttons : buttons,
		Abbrechen : function() {
			$(this).dialog('close');
		}
	});
}