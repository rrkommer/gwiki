var gwikiInsertNewAttachmentDialog_html = "  <form><fieldset>" + "<label for='linkprtitle'>"
    + "gwiki.editor.wikilink.dialog.title".i18n() + " </label><input size='50' type='text' id='linkprtitle'>"
    + "<label for='linkpropt'>" + "gwiki.editor.wikilink.dialog.link".i18n()
    + " </label><input size='50' type='text' id='linkpropt'><br/>" + "<p><span id='editDlgInserImageMsg'></span></p>";
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
			resobj.title = $('#linkprtitle').val();
			if (clipData.isImage) {
				twedit_attach_insertImageLink(ed, resobj);
			} else {
				twedit_attach_insertFileLink(ed, resobj);
			}
		});
	}

	var dialog = $("#editDialogBox").dialog({
	  modal : true,
	  width : 600,
	  open : function(event, ui) {
		  $('#linkpropt').val(fileName);
		  $('#linkprtitle').val(fileName);

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
function twedit_attach_insertFileLink(ed, resobj) {
	var item = {
	  key : resobj.tmpFileName,
	  title : resobj.title
	}
	gwedit_insert_pagelink(ed, item);
}
function twedit_attach_insertImageLink(ed, resobj) {

	tweid_editfocus(ed);
	resobj.key = resobj.tmpFileName;
	gwedit_replace_image_before(ed, resobj);

}

function twedit_attach_storeAttachment(ed, clipData, fileName, title, callback) {
	console.debug("parentid: " + gwikiContext.gwikiEditPageId);
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
		  console.debug("image uploaded: " + result);
		  var resobj = eval('(' + result + ')');
		  callback(resobj);
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
