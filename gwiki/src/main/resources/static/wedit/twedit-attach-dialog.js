var gwikiInsertNewAttachmentDialog_html = 
	"<h2>Insert new Image/Attachment</h2>\n"
	+ "<form><fieldset>" + "<p><label for='linkprtitle' style='display:block;'>"
    + "gwiki.editor.wikilink.dialog.title".i18n() + " </label><input size='50' type='text' id='linkprtitle'></p>"
    + "<p><label style='display:block;' for='linkpropt'>" + "gwiki.editor.wikilink.dialog.link".i18n()
    + " </label><input size='50' type='text' id='linkpropt'></p>" + 
    "<p><span id='editDlgInserImageMsg'></span></p>";
+" </fieldset></form>";
/**
 * 
 * @param options {
 *          parentPageId: pageId must be set }
 * @param parentWindow
 * @param callback
 *          with signature function (field, newImageId)
 * @return
 */
function gwikiInsertNewAttachmentDialog(ed, clipData) {

	if (gwikiContext.pageId == null) {
		alert('Page to insert has saved initially.');
		return;
	}
	var modc = $("#editDialogBox");
	modc.html(gwikiInsertNewAttachmentDialog_html);
	// gwikiEditField = parentWindow;
	var isImage = clipData.isImage;
	var fileName = clipData.fileName;
	if (!fileName) {
		fileName = "NewFile" + clipData.getFileExtension();
	}
	var modc = $("#editDialogBox");
	modc.html(gwikiInsertNewAttachmentDialog_html);

	var buttons = {};
	buttons["gwiki.common.cancel".i18n()] = function() {
		$(dialog).dialog('close');
		tweid_editfocus(ed);
	}
	buttons["gwiki.common.ok".i18n()] = function() {
		var fileName = $("#linkpropt").val();
		var title = $("#linkprtitle").val();
		twedit_attach_storeAttachment(ed, clipData, fileName, title, function(resobj) {
			if (resobj.rc != 0) {
				$("#editDlgInserImageMsg").html(resobj.rm);
				if (resobj.alternativeFileName) {
					$("#linkpropt").val(resobj.alternativeFileName);
				}
				return false;
			}
			$(dialog).dialog('close');
			var item = resobj.item;
			item.title = $('#linkprtitle').val();
			if (clipData.isImage) {
				twedit_attach_insertImageLink(ed, item);
			} else {
				twedit_attach_insertFileLink(ed, item);
			}
		});
	}

	var dialog = $("#editDialogBox").dialog({
	  modal : true,
	  dialogClass: 'jquiNoDialogTitle', 
	  width : 600,
	  open : function(event, ui) {
		  $('#linkpropt').val(fileName);
		  $('#linkprtitle').val(fileName);

	  },
	  close : function(event, ui) {
		  ed.focus();
	  },
	  overlay : {
	    backgroundColor : '#000',
	    opacity : 0.5
	  },
	  buttons : buttons
	});
}

function tweid_editfocus(ed) {
	try {
		ed.focus();
	} catch (e) {
		console.error("Error editor.focus(): " + e);
	}

}
function twedit_attach_insertFileLink(ed, item) {
	gwedit_insert_pagelink(ed, item);
}
function twedit_attach_insertImageLink(ed, item) {

	tweid_editfocus(ed);
	gwedit_replace_image_before(ed, item);

}

function twedit_attach_storeAttachment(ed, clipData, fileName, title, callback) {
//	console.debug("parentid: " + gwikiContext.gwikiEditPageId);
	$.ajax({
	  method : 'POST',
	  url : './UploadAttachment',
	  data : {
	    method_onUploadImage : true,
	    storeTmpFile : false,
	    parentPageId : gwikiContext.gwikiEditPageId,
	    json : true,
	    fileName : fileName,
	    title : title, // TODO not yet used in uplaod
	    encData : clipData.base64Data
	  },
	  success : function(result) {
		  callback(result);
	  },
	  error : function(xhr, status, error) {
		  var resobj = {
		    rc : -1,
		    rm : error
		  };
		  callback(resobj);
	  }
	});

}
