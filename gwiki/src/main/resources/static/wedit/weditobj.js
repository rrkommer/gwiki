jQuery.fn.wedit = function(settings) {
	var config = {
			autocompletegetitemshandler: wedit_autocomplete_getdummyentries,
			drophandler: wedit_storeDroppedImage,
			stdkeydownhandler: standardEditKeyEvent,
			keydownhandler: standardEditKeyEvent,
			keyuphandler : null,
			ctrlSpaceAttchars:  [ '!', '[', '{'],
			linkAutoCompleteUrl: ''
	};
	if (settings) {
		$.extend(config, settings);
	}
	
	var wedit = this;
	return this.each(function() {
		var jthis = $(this);
		wedit_bindfocushandler(jthis);
		wedit_registerkeyhandler(jthis, config);	
		wedit_registerdragndrop(jthis, config);
		wedit_registerClipboard(jthis, config);
		wedit_register_modifaction_monitor(jthis, config);
	});
};

$(document).ready(function() {
	var edit = $('div[contenteditable="true"]').wedit();
});