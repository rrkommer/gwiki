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
	var dlghtml = "Title: <input size=\"30\" type=\"text\" id=\"linkprtitle\"";
	if (currentLink.title) {
		dlghtml += " value=\"" + gwikiEscapeInput(currentLink.title) + "\"";
	}
	dlghtml += "><br/>\n"
			+ "Link: <input size=\"30\" type=\"text\" id=\"linkpropt\"";
	if (currentLink.url) {
		dlghtml += " value=\"" + gwikiEscapeInput(currentLink.url) + "\"";
	}
	dlghtml += ">";
	modc.html(dlghtml);
	var dialog = $("#editDialogBox").dialog( {
		modal : true,
		open : function(event, ui) {
			$("#linkpropt").focus();
			$('#linkpropt').autocomplete("./PageSuggestions?pageType=" + pageType, {
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
			});
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
				parentWindow.focus();
			},
			'OK' : function() {
				var lt = $("#linkpropt").attr('value');
				$(dialog).dialog('close');
				parentWindow.focus();
				var ret = {
					url : $("#linkpropt").val(),
					title : $("#linkprtitle").val()
				};
				callback(ret);
			}
		},
		Abbrechen : function() {
			$(this).dialog('close');
			parentWindow.focus();
		}
	});
}
var gwikiCurrentInsertScreenShotCb;
function gwikieditInsertScreenshotCb(fileName, tmpFileName) {

	$(gwikiEditCurrentDialog).dialog('close');
	gwikiEditField.focus();
	if (tmpFileName != null && tmpFileName != null && tmpFileName != "") {
		gwikiCurrentInsertScreenShotCb(gwikiEditField, tmpFileName);
	}
}
/**
 * 
 * @param options {
 *          parentPageId: pageId must be set }
 * @param parentWindow
 * @param callback
 *          with signature function (field, newImageId)
 * @return
 */
function gwikiEditInsertImageDialog(options, parentWindow, callback) {
	if (options.parentPageId == undefined || options.parentPageId == '') {
		alert('Page to insert has saved initially.');
		return;
	}
	gwikiEditField = parentWindow;

	var modc = $("#editDialogBox");
	modc.html("<div id='editDlgInserImage'></div>");
	var dialog = $("#editDialogBox").dialog(
			{
				modal : true,
				width : 800,
				position : 'top',
				open : function(event, ui) {
					var i = Math.round(10000 * Math.random())
					var url = gwikiContextPath + "/edit/UploadAppletWindow";
					// alert('open url now: ' + url);
					var params = {
						allowedFileType : 'img',
						jscb : 'gwikieditInsertScreenshotCb',
						storeTmpFile : 'false',
						parentPageId : options.parentPageId,
						r : i
					};
					jQuery('#editDlgInserImage').load(url + " #uploadappletbody", params,
							function(responseText, textStatus, XMLHttpRequest) {
								//alert('applet returned: ' + textStatus + "; " + responseText);
								this; // dom element
						});
					gwikiEditCurrentDialog = this;
					gwikiCurrentInsertScreenShotCb = callback;
				},
				close : function(event, ui) {
					$(this).dialog('destroy');
					parentWindow.focus();
					gwikiEditCurrentDialog = undefined;
				},
				overlay : {
					backgroundColor : '#000',
					opacity : 0.5
				},
				buttons : {
					'Cancel' : function() {
						$(dialog).dialog('close');
						parentWindow.focus();
					},
					'OK' : function() {
						var lt = $("#linkpropt").attr('value');

						// $(field).wrap('[' + lt + ']', '')
					$(this).dialog('close');
					parentWindow.focus();
					// callback(parentWindow, lt);
				}
				},
				'Abbrechen' : function() {
					$(this).dialog('close');
				}
			});
}

function gwikiEditShowLink(parentWindow, pageType, currentLink, callback) {
	var modc = $("#editDialogBox");
	var dlghtml = "Title: <input size=\"50\" type=\"text\" id=\"linkprtitle\"";
	if (currentLink.title) {
		dlghtml += " value=\"" + gwikiEscapeInput(currentLink.title) + "\"";
	}
	dlghtml += "><br/>\n"
			+ "Link: <input size=\"50\" type=\"text\" id=\"linkpropt\"";
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
	dlghtml += "        \"url\" : \"/edit/TreeChildren\",\n";
	dlghtml += "        \"data\" : function(n) {   return { \"method_onLoadAsync\" : \"true\", "
			+ "\"id\" : n.attr ? n.attr(\"id\") : \"\","
			+ "\"urlField\" : \"linkpropt\","
			+ "\"titleField\" : \"linkprtitle\" };      }\n";
	dlghtml += "      }";
	dlghtml += "    }\n";
	dlghtml += "  });\n";
	dlghtml += "});\n";
	dlghtml += "</script>";

	modc.html(dlghtml);
	var dialog = $("#editDialogBox").dialog({
		width : 500,
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
		buttons : {
			'Cancel' : function() {
				$(dialog).dialog('close');
				parentWindow.focus();
			},
			'OK' : function() {
				var lt = $("#linkpropt").attr('value');
				$(dialog).dialog('close');
				parentWindow.focus();
				var ret = {
					url : $("#linkpropt").val(),
					title : $("#linkprtitle").val()
				};
				callback(ret);
			}
		},
		Abbrechen : function() {
			$(this).dialog('close');
			parentWindow.focus();
		}
	});
}