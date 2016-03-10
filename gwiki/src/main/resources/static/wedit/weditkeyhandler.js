
function standardEditKeyEvent(event, weditconfig) {
	console.debug("stdkeypup	: " + event.which + "; ctr: " + event.ctrlKey + "; shift " + event.shiftKey + "; alt "
	    + event.altKey);
	if (event.ctrlKey == true) {
		switch (event.which) {
		case 32: // space
			event.stopPropagation();
			wedit_autoComplete("#editordiv", "", weditconfig);
			break;
		case 69: // e
			wedit_moveToEndline();
			event.stopPropagation();
			return false;
		case 70: // f debug only
			wedit_moveforward(2);
			return false;
		}

	}

}

function wedit_registerkeyhandler(jnode, weditconfig) {
	jnode.keydown(function(event) {
		return weditconfig.keydownhandler(event, weditconfig);
	});
	jnode.keyup(function(event) {
		if (weditconfig.keyuphandler) {
			return weditconfig.keyuphandler(event, weditconfig);
		}
	});
}
