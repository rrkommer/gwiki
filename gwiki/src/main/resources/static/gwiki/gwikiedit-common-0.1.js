function gwikiEditPagePath() {
	
	var path = document.getElementById('editPagePath').value;
	var modc = $("#editDialogBox");
	var dlghtml = "<b>NOTE:</b> Use this function to manipulate the path at your own risk.<br /><br />" +
			"<label for=\"pathtextfield\" style=\"margin-right:10px\">Path:</label><input size=\"44\" type=\"text\" id=\"pathtextfield\""
				+ " value='" + path + "'/><br/>\n";

	modc.html(dlghtml);
	
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
		buttons : {
			'Cancel' : function() {
				$(dialog).dialog('close');
			},
			'OK' : function() {
				var value = $("#pathtextfield").attr('value');
				$(dialog).dialog('close');
				document.getElementById('editPagePath').value = value;
			}
		},
		Abbrechen : function() {
			$(this).dialog('close');
		}
	});
}
