/**
 * E for edit
 */
$(document).keydown(function(event) {
	if (event.which != 69) {
		return;
	}
	if (!gwikiContext.pageId) {
		return;
	}
	
	var el = document.activeElement;
	if (el && el.nodeName == 'INPUT') {
		return;
	}
	window.location.href = gwikiLocalUrl('edit/EditPage') + "?pageId=" + gwikiEscapeUrlParam(gwikiContext.pageId);

});