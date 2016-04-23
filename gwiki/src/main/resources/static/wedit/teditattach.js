$(document).ready(function() {
	$('.gwikiattachment_dropfile').on('dragover', function(event) {
		event.preventDefault();
		// return true;
	});
	$('.gwikiattachment_dropfile').on('drop', function(event) {
		console.debug('gwikiattachment_dropfile');

		event.stopPropagation();
		event.preventDefault();
		var dt = event.originalEvent.dataTransfer;
		var cp = new ClipData(event, dt);
		if (cp.hasAttachment == false) {
			return;
		}
		cp.waitForBinaryData(function(clipData) {
			storeAttachments(clipData);
		});

	});
});

function storeAttachments(clipData) {
	if (clipData.fileName == null) {
		// TODO err
		return;
	}
	twedit_attach_storeAttachment(null, clipData, clipData.fileName, '', gwikiAttachmentParentPageId, function(result) {
		if (result.rc != 0) {
			alert(result.rm);
		} else {
			alert("gwiki.attachment.fileuploaded".i18n())
			$("#gwikieditcancelbutton").click();
		}
	});
}
