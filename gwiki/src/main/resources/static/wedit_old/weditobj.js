
var weditmap = new Map();



jQuery.fn.wedit = function(settings) {
	var config = {
			autocompletegetitemshandler: wedit_autocomplete_getdummyentries,
			drophandler: wedit_storeDroppedImage,
			stdkeydownhandler: standardEditKeyEvent,
			keydownhandler: standardEditKeyEvent,
			keyuphandler : null,
			
			ctrlSpaceAttchars:  [ '!', '[', '{'],
			linkAutoCompleteUrl: '',
			autocompleteImmediatelly: true
	};
	if (settings) {
		$.extend(config, settings);
	}
	
	var wedit = this;
	return this.each(function() {
		var jthis = $(this);
		var om = new Map();
		om.set('jthis', jthis);
		om.set('config', config);
		
		weditmap.set(jthis.attr('id'), om);
		jthis.css("white-space", "pre-wrap"); // pre but with break line if to long.
		jthis.css("display",  "inline-block"); // just nl instead of <div>
		wedit_bindfocushandler(jthis);
		wedit_registerkeyhandler(jthis, config);	
		wedit_registerdragndrop(jthis, config);
		wedit_registerClipboard(jthis, config);
		wedit_register_modifaction_monitor(jthis, config);
	});
};

