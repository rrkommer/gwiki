
function wikiEditInsertImage(options, field) {
	if (options.parentPageId == undefined || options.parentPageId == '') {
		alert('Page to insert has saved initially.');
		return;
	}
	gwikiEditField = field;
	var modc = $("#editDialogBox");
	modc.html("<div id='editDlgInserImage'></div>");
	var dialog = $("#editDialogBox")
			.dialog(
					{
						modal : true,
						width : 800,
						position : 'top',
						open : function(event, ui) {
							jQuery('#editDlgInserImage')
									.load(
											"UploadAppletWindow?allowedFileType=img&jscb=gwikieditInsertImageCb&storeTmpFile=false&parentPageId="
													+ options.parentPageId + " #uploadappletbody");
							gwikiEditCurrentDialog = this;
						},
						close : function(event, ui) {
							$(this).dialog('destroy');
							field.focus();
							gwikiEditCurrentDialog = undefined;
						},
						overlay : {
							backgroundColor : '#000',
							opacity : 0.5
						},
						buttons : {
							'OK' : function() {
								var lt = $("#linkpropt").attr('value');

								// $(field).wrap('[' + lt + ']', '')
						$(this).dialog('close');
						field.focus();
						insertIntoTextArea(field, linkstart + lt + linkend, '');
					}
				},
				'Abbrechen' : function() {
					$(this).dialog('close');
				}
					});
}

function wikiEditShowSuggest(options, field, pageType, linkstart, linkend) {

	var modc = $("#editDialogBox");
	modc.html("Link: <input size=\"35\" type=\"text\" id=\"linkpropt\">");
	var dialog = $("#editDialogBox").dialog(
			{
				modal : true,
				open : function(event, ui) {
					$('#linkpropt').autocomplete(
							options.linkAutoCompleteUrl + "?pageType=" + pageType, {
								matchContains : true,
								minChars : 0,
								width : 350,
								formatItem : function(row) {
									return row[1] + "<br><i>(" + row[0] + ")</i>";
								}
							}).result(function(event, item) {
						$(dialog).dialog('close');
						field.focus();
						insertIntoTextArea(field, linkstart + item[0] + linkend, '');

					});

				},
				close : function(event, ui) {
					$(this).dialog('destroy');
					field.focus();
				},
				overlay : {
					backgroundColor : '#000',
					opacity : 0.5
				},
				buttons : {
					'OK' : function() {
						var lt = $("#linkpropt").attr('value');

						// $(field).wrap('[' + lt + ']', '')
				$(this).dialog('close');
				field.focus();
				insertIntoTextArea(field, linkstart + lt + linkend, '');
			}
		},
		Abbrechen : function() {
			$(this).dialog('close');
		}
			});
}
